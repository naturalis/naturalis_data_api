package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copyScientificName;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySourceSystem;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySummaryVernacularName;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

public class SpecimenTaxonomicEnricher2 {

	public static void main(String[] args)
	{
		SpecimenTaxonomicEnricher2 enricher = new SpecimenTaxonomicEnricher2();
		try {
			enricher.configureWithSystemProperties();
			enricher.enrich();
		}
		catch (Throwable t) {
			logger.error(t.getMessage());
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(SPECIMEN);
			ESClientManager.getInstance().closeClient();
		}
		System.exit(0);
	}

	private static final Logger logger = getLogger(SpecimenTaxonomicEnricher2.class);
	private static final List<TaxonomicEnrichment> NONE = new ArrayList<>(0);
	private static final byte[] NEW_LINE = "\n".getBytes();

	private int readBatchSize = 1000;
	private int writeBatchSize = 1000;

	private File tempFile;

	public void enrich() throws IOException, BulkIndexException
	{
		long start = System.currentTimeMillis();
		tempFile = getTempFile();
		logger.info("Writing enriched specimens to " + tempFile.getAbsolutePath());
		saveToTempFile();
		logger.info("Reading enriched specimens from " + tempFile.getAbsolutePath());
		importTempFile();
		tempFile.delete();
		logDuration(logger, getClass(), start);
	}

	private void saveToTempFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		DocumentType<Specimen> dt = DocumentType.SPECIMEN;
		QuerySpec qs = new QuerySpec();
		qs.setSize(readBatchSize);
		DirtyDocumentIterator<Specimen> extractor = new DirtyDocumentIterator<>(dt, qs);
		int batchNo = 0;
		int enriched = 0;
		List<Specimen> batch = extractor.nextBatch();
		try {
			while (batch != null) {
				List<Specimen> enrichedSpecimens = enrichSpecimens(batch);
				enriched += enrichedSpecimens.size();
				for (Specimen specimen : enrichedSpecimens) {
					byte[] json = JsonUtil.serialize(specimen);
					bos.write(json);
					bos.write(NEW_LINE);
				}
				if (++batchNo % 100 == 0) {
					logger.info("Specimen documents processed: {}", (batchNo * readBatchSize));
					logger.info("Specimen documents enriched: {}", enriched);
				}
				batch = extractor.nextBatch();
			}
		}
		finally {
			bos.close();
			logger.info("Specimen documents read: {}", (batchNo * readBatchSize));
			logger.info("Specimen documents enriched: {}", enriched);
		}
	}

	private void importTempFile() throws IOException, BulkIndexException
	{
		BulkIndexer<Specimen> indexer = new BulkIndexer<>(SPECIMEN);
		List<Specimen> batch = new ArrayList<>(writeBatchSize);
		LineNumberReader lnr = null;
		int processed = 0;
		try {
			FileReader fr = new FileReader(tempFile);
			lnr = new LineNumberReader(fr, 4096);
			String line;
			while ((line = lnr.readLine()) != null) {
				Specimen specimen = JsonUtil.deserialize(line, Specimen.class);
				batch.add(specimen);
				if (batch.size() == writeBatchSize) {
					indexer.index(batch);
					batch.clear();
				}
				if (++processed % 100000 == 0) {
					logger.info("Specimen documents imported: {}", processed);
				}
			}
		}
		finally {
			IOUtil.close(lnr);
		}
	}

	public void configureWithSystemProperties()
	{
		String prop = System.getProperty(SYS_PROP_ENRICH_READ_BATCH_SIZE, "1000");
		try {
			setReadBatchSize(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid read batch size: " + prop);
		}
		prop = System.getProperty(SYS_PROP_ENRICH_WRITE_BATCH_SIZE, "1000");
		try {
			setWriteBatchSize(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid write batch size: " + prop);
		}
	}

	public int getReadBatchSize()
	{
		return readBatchSize;
	}

	public void setReadBatchSize(int readBatchSize)
	{
		if (readBatchSize > 1024) {
			throw new IllegalArgumentException("readBatchSize must be less than 1025");
		}
		if (readBatchSize < 1) {
			throw new IllegalArgumentException("readBatchSize must be greater than 0");
		}
		this.readBatchSize = readBatchSize;
	}

	public int getWriteBatchSize()
	{
		return writeBatchSize;
	}

	public void setWriteBatchSize(int writeBatchSize)
	{
		if (writeBatchSize < 1) {
			throw new IllegalArgumentException("writeBatchSize must be greater than 0");
		}
		this.writeBatchSize = writeBatchSize;
	}

	private static List<Specimen> enrichSpecimens(List<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating taxon lookup table");
		}
		HashMap<String, List<Taxon>> taxonLookupTable = createLookupTable(specimens);
		if (taxonLookupTable.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No taxa found for current batch of specimens");
			}
			return Collections.emptyList();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table size: {}", taxonLookupTable.size());
		}
		HashMap<String, List<TaxonomicEnrichment>> cache = new HashMap<>(specimens.size());
		List<Specimen> result = new ArrayList<>(specimens.size());
		for (Specimen specimen : specimens) {
			boolean enriched = false;
			if (specimen.getIdentifications() == null) {
				continue;
			}
			for (TaxonomicIdentification si : specimen.getIdentifications()) {
				String nameGroup = si.getScientificName().getScientificNameGroup();
				List<TaxonomicEnrichment> enrichments = cache.get(nameGroup);
				if (enrichments == null) {
					List<Taxon> taxa = taxonLookupTable.get(nameGroup);
					if (taxa == null) {
						/*
						 * There are no taxon documents with this
						 * scientificNameGroup
						 */
						cache.put(nameGroup, NONE);
					}
					else {
						enrichments = createEnrichments(taxa);
						cache.put(nameGroup, enrichments);
						if (enrichments != NONE) {
							si.setTaxonomicEnrichments(enrichments);
							enriched = true;
						}
						/*
						 * Else there were taxon documents with this
						 * scientificNameGroup, but none of them had vernacular
						 * names and/or synonyms.
						 */
					}
				}
				else if (enrichments != NONE) {
					si.setTaxonomicEnrichments(enrichments);
					enriched = true;
				}
			}
			if (enriched) {
				result.add(specimen);
			}
		}
		return result;
	}

	private static List<TaxonomicEnrichment> createEnrichments(List<Taxon> taxa)
	{
		List<TaxonomicEnrichment> enrichments = new ArrayList<>(taxa.size());
		for (Taxon taxon : taxa) {
			if (taxon.getVernacularNames() == null && taxon.getSynonyms() == null) {
				continue;
			}
			TaxonomicEnrichment enrichment = new TaxonomicEnrichment();
			enrichment.setSourceSystem(copySourceSystem(taxon.getSourceSystem()));
			enrichment.setTaxonId(taxon.getId());
			enrichments.add(enrichment);
			if (taxon.getVernacularNames() != null) {
				for (VernacularName vn : taxon.getVernacularNames()) {
					enrichment.addVernacularName(copySummaryVernacularName(vn));
				}
			}
			if (taxon.getSynonyms() != null) {
				for (ScientificName sn : taxon.getSynonyms()) {
					enrichment.addSynonym(copyScientificName(sn));
				}
			}
		}
		return enrichments.isEmpty() ? NONE : enrichments;
	}

	private static HashMap<String, List<Taxon>> createLookupTable(List<Specimen> specimens)
	{
		HashMap<String, List<Taxon>> table = new HashMap<>(specimens.size() * 3);
		HashSet<String> names = new HashSet<>(1024);
		for (int i = 0; i < specimens.size(); i++) {
			Specimen specimen = specimens.get(i);
			if (names.size() + specimen.getIdentifications().size() > 1024) {
				QueryResult<Taxon> taxa = loadTaxa(names);
				addToLookupTable(taxa, table);
				names = new HashSet<>(1024);
			}
			else {
				for (TaxonomicIdentification si : specimen.getIdentifications()) {
					names.add(si.getScientificName().getScientificNameGroup());
				}
			}
		}
		return table;
	}

	private static QueryResult<Taxon> loadTaxa(HashSet<String> names)
	{
		String field = "acceptedName.scientificNameGroup";
		QueryCondition condition = new QueryCondition(field, "IN", names);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		query.setConstantScore(true);
		query.setSize(1024);
		TaxonDao taxonDao = new TaxonDao();
		QueryResult<Taxon> result;
		try {
			result = taxonDao.query(query);
		}
		catch (InvalidQueryException e) {
			throw new ETLRuntimeException(e);
		}
		return result;
	}

	private static void addToLookupTable(QueryResult<Taxon> taxa,
			HashMap<String, List<Taxon>> table)
	{
		for (QueryResultItem<Taxon> item : taxa) {
			Taxon taxon = item.getItem();
			String sng = taxon.getAcceptedName().getScientificNameGroup();
			List<Taxon> stored = table.get(sng);
			if (stored == null) {
				stored = new ArrayList<>(2);
				table.put(sng, stored);
			}
			stored.add(taxon);
		}
	}

	private static File getTempFile() throws IOException
	{
		File tmpDir = DaoRegistry.getInstance().getFile("../tmp").getCanonicalFile();
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		int time = (int) (new Date().getTime() / 1000);
		String fileName = String.format("SpecimenTaxonomicEnricher-%s.json", time);
		return FileUtil.newFile(tmpDir, fileName);
	}

}
