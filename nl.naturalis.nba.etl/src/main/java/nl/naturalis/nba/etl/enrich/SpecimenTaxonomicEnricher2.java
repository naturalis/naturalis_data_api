package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_SCROLL_TIMEOUT;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copyScientificName;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySourceSystem;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySummaryVernacularName;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

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
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import nl.naturalis.nba.dao.util.es.DocumentIterator;
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

	private static final Logger logger = getLogger(SpecimenTaxonomicEnricher.class);
	private static final List<TaxonomicEnrichment> NONE = new ArrayList<>(0);
	private static final byte[] NEW_LINE = "\n".getBytes();

	private int readBatchSize = 1000;
	private int writeBatchSize = 1000;
	private int scrollTimeout = 60000;

	private File tempFile;

	public void enrich() throws IOException, BulkIndexException
	{
		long start = System.currentTimeMillis();
		tempFile = getTempFile();
		logger.info("Saving enriched specimens to temp file: " + tempFile.getAbsolutePath());
		saveToTempFile();
		logger.info("Importing specimens from temp file");
		importTempFile();
		tempFile.delete();
		logDuration(logger, getClass(), start);
	}

	private void saveToTempFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(readBatchSize);
		extractor.setTimeout(scrollTimeout);
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
		prop = System.getProperty(SYS_PROP_ENRICH_SCROLL_TIMEOUT, "10000");
		try {
			setScrollTimeout(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid scroll timeout: " + prop);
		}
	}

	public int getReadBatchSize()
	{
		return readBatchSize;
	}

	public void setReadBatchSize(int readBatchSize)
	{
		this.readBatchSize = readBatchSize;
	}

	public int getWriteBatchSize()
	{
		return writeBatchSize;
	}

	public void setWriteBatchSize(int writeBatchSize)
	{
		this.writeBatchSize = writeBatchSize;
	}

	public int getScrollTimeout()
	{
		return scrollTimeout;
	}

	public void setScrollTimeout(int scrollTimeout)
	{
		this.scrollTimeout = scrollTimeout;
	}

	private static List<Specimen> enrichSpecimens(List<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating taxon lookup table");
		}
		HashMap<String, List<Taxon>> taxonLookupTable = createLookupTable(specimens);
		if (taxonLookupTable == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No taxa found for current batch of specimens");
			}
			return Collections.emptyList();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table created. {} unique name group(s) found in Taxon index",
					taxonLookupTable.size());
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
			enrichment.setSourceSystem(copySourceSystem(taxon.getSourceSystem()));
			enrichment.setTaxonId(taxon.getId());
			enrichments.add(enrichment);
		}
		return enrichments.isEmpty() ? NONE : enrichments;
	}

	private static HashMap<String, List<Taxon>> createLookupTable(List<Specimen> specimens)
	{
		HashSet<String> groups = new HashSet<>(specimens.size());
		for (Specimen specimen : specimens) {
			for (TaxonomicIdentification si : specimen.getIdentifications()) {
				groups.add(si.getScientificName().getScientificNameGroup());
			}
		}
		if (groups.size() > 1024) {
			String fmt = "Number of unique names in batch (%s) exceed maximum of 1024. "
					+ "Try decreasing read batch size";
			String msg = String.format(fmt, groups.size());
			throw new ETLRuntimeException(msg);
		}
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = newSearchRequest(dt);
		TermsQueryBuilder query = termsQuery("acceptedName.scientificNameGroup", groups);
		request.setQuery(constantScoreQuery(query));
		int maxNumTaxa = 10000;
		request.setSize(maxNumTaxa);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return null;
		}
		// Unlikely, but let's still trap it
		if (response.getHits().getTotalHits() > maxNumTaxa) {
			throw new ETLRuntimeException("Too many taxa found for current batch of specimens");
		}
		HashMap<String, List<Taxon>> table = new HashMap<>(groups.size());
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			List<Taxon> taxa = table.get(taxon.getAcceptedName().getScientificNameGroup());
			if (taxa == null) {
				taxa = new ArrayList<>(2);
				table.put(taxon.getAcceptedName().getScientificNameGroup(), taxa);
			}
			taxa.add(taxon);
		}
		return table;
	}

	private static File getTempFile() throws IOException
	{
		File tmpDir = DaoRegistry.getInstance().getFile("../tmp").getCanonicalFile();
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		int time = (int) (new Date().getTime() / 1000);
		String fileName = String.format("enriched-specimens-%s.json", time);
		return FileUtil.newFile(tmpDir, fileName);
	}

}
