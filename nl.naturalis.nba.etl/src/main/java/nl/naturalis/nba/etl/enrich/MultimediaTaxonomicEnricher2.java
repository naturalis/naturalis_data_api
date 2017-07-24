package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.NOT_ENRICHABLE;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createEnrichments;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createTempFile;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createTaxonLookupTableForMultiMedia;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.IOUtil;

public class MultimediaTaxonomicEnricher2 {

	public static void main(String[] args)
	{
		MultimediaTaxonomicEnricher2 enricher = new MultimediaTaxonomicEnricher2();
		try {
			enricher.configureWithSystemProperties();
			enricher.enrich();
		}
		catch (Throwable t) {
			logger.error("Error while enriching multimedia", t);
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESClientManager.getInstance().closeClient();
		}
		System.exit(0);
	}

	private static final Logger logger = getLogger(MultimediaTaxonomicEnricher2.class);
	private static final byte[] NEW_LINE = "\n".getBytes();

	private int readBatchSize = 1000;
	private int writeBatchSize = 1000;

	private File tempFile;

	public void enrich() throws IOException, BulkIndexException
	{
		long start = System.currentTimeMillis();
		tempFile = createTempFile(getClass().getSimpleName());
		logger.info("Saving enriched multimedia to temp file: " + tempFile.getAbsolutePath());
		saveToTempFile();
		logger.info("Importing multimedia from temp file");
		importFromTempFile();
		tempFile.delete();
		logDuration(logger, getClass(), start);
	}

	private void saveToTempFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		DocumentType<MultiMediaObject> dt = MULTI_MEDIA_OBJECT;
		QuerySpec qs = new QuerySpec();
		qs.setSize(readBatchSize);
		DirtyDocumentIterator<MultiMediaObject> extractor = new DirtyDocumentIterator<>(dt, qs);
		int batchNo = 0;
		int enriched = 0;
		List<MultiMediaObject> batch = extractor.nextBatch();
		try {
			while (batch != null) {
				List<MultiMediaObject> enrichedMultimedia = enrichMultimedia(batch);
				enriched += enrichedMultimedia.size();
				for (MultiMediaObject mmo : enrichedMultimedia) {
					byte[] json = JsonUtil.serialize(mmo);
					bos.write(json);
					bos.write(NEW_LINE);
				}
				if (++batchNo % 100 == 0) {
					logger.info("MultiMediaObject documents processed: {}",
							(batchNo * readBatchSize));
					logger.info("MultiMediaObject documents enriched: {}", enriched);
				}
				batch = extractor.nextBatch();
			}
		}
		finally {
			bos.close();
			logger.info("MultiMediaObject documents read: {}", (batchNo * readBatchSize));
			logger.info("MultiMediaObject documents enriched: {}", enriched);
		}
	}

	private void importFromTempFile() throws IOException, BulkIndexException
	{
		BulkIndexer<MultiMediaObject> indexer = new BulkIndexer<>(MULTI_MEDIA_OBJECT);
		List<MultiMediaObject> batch = new ArrayList<>(writeBatchSize);
		LineNumberReader lnr = null;
		int processed = 0;
		try {
			FileReader fr = new FileReader(tempFile);
			lnr = new LineNumberReader(fr, 4096);
			String line;
			while ((line = lnr.readLine()) != null) {
				MultiMediaObject mmo = JsonUtil.deserialize(line, MultiMediaObject.class);
				batch.add(mmo);
				if (batch.size() == writeBatchSize) {
					indexer.index(batch);
					batch.clear();
				}
				if (++processed % 100000 == 0) {
					logger.info("MultiMediaObject documents imported: {}", processed);
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

	private static List<MultiMediaObject> enrichMultimedia(List<MultiMediaObject> mmos)
	{
		Map<String, List<Taxon>> taxonLookupTable = createTaxonLookupTableForMultiMedia(mmos);
		if (taxonLookupTable.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No taxa found for current batch of multimedia");
			}
			return Collections.emptyList();
		}
		Map<String, List<TaxonomicEnrichment>> cache = new HashMap<>(mmos.size());
		List<MultiMediaObject> result = new ArrayList<>(mmos.size());
		for (MultiMediaObject mmo : mmos) {
			if (mmo.getIdentifications() == null) {
				continue;
			}
			boolean enriched = false;
			for (TaxonomicIdentification si : mmo.getIdentifications()) {
				String name = si.getScientificName().getScientificNameGroup();
				List<TaxonomicEnrichment> enrichments = cache.get(name);
				if (enrichments == null) {
					// Is this scientific name also present in the Taxon index?
					List<Taxon> taxa = taxonLookupTable.get(name);
					if (taxa == null) { // No
						cache.put(name, NOT_ENRICHABLE);
					}
					else {
						enrichments = createEnrichments(taxa);
						if (enrichments.isEmpty()) {
							cache.put(name, NOT_ENRICHABLE);
						}
						else {
							cache.put(name, enrichments);
							si.setTaxonomicEnrichments(enrichments);
							enriched = true;
						}
					}
				}
				else if (enrichments != NOT_ENRICHABLE) {
					si.setTaxonomicEnrichments(enrichments);
					enriched = true;
				}
			}
			if (enriched) {
				result.add(mmo);
			}
		}
		return result;
	}

}
