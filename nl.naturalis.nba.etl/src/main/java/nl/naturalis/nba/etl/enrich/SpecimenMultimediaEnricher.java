package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createTempFile;

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

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.MultiMediaObjectDao;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.IOUtil;

public class SpecimenMultimediaEnricher {

	public static void main(String[] args)
	{
		SpecimenMultimediaEnricher enricher = new SpecimenMultimediaEnricher();
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

	private static final Logger logger = getLogger(SpecimenMultimediaEnricher.class);
	private static final byte[] NEW_LINE = "\n".getBytes();

	private int readBatchSize = 1000;
	private int writeBatchSize = 1000;

	private File tempFile;

	public void enrich() throws IOException, BulkIndexException
	{
		long start = System.currentTimeMillis();
		tempFile = createTempFile(getClass().getSimpleName());
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
		DocumentType<Specimen> dt = SPECIMEN;
		QueryCondition condition = new QueryCondition("sourceSystem.code", "=", "CRS");
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
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

	private static List<Specimen> enrichSpecimens(List<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Creating multimedia lookup table");
		}
		Map<String, List<ServiceAccessPoint>> multimediaLookupTable = createLookupTable(specimens);
		if (multimediaLookupTable.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No multimedia found for current batch of specimens");
			}
			return Collections.emptyList();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lookup table created ({} entries)", multimediaLookupTable.size());
		}
		List<Specimen> enriched = new ArrayList<>(specimens.size());
		for (Specimen specimen : specimens) {
			List<ServiceAccessPoint> uris = multimediaLookupTable.get(specimen.getId());
			if (uris != null) {
				specimen.setAssociatedMultiMediaUris(uris);
				enriched.add(specimen);
			}
		}
		return enriched;
	}

	private static Map<String, List<ServiceAccessPoint>> createLookupTable(List<Specimen> specimens)
	{
		ArrayList<String> ids = new ArrayList<>(specimens.size());
		for (Specimen specimen : specimens) {
			ids.add(specimen.getId());
		}
		QueryCondition condition = new QueryCondition("associatedSpecimenReference", IN, ids);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		query.setSize(10000);
		MultiMediaObjectDao dao = new MultiMediaObjectDao();
		QueryResult<MultiMediaObject> result;
		try {
			result = dao.query(query);
		}
		catch (InvalidQueryException e) {
			throw new ETLRuntimeException(e);
		}
		Map<String, List<ServiceAccessPoint>> lookups = new HashMap<>(ids.size());
		for (QueryResultItem<MultiMediaObject> item : result) {
			String specimenId = item.getItem().getAssociatedSpecimenReference();
			List<ServiceAccessPoint> uris = lookups.get(specimenId);
			if (uris == null) {
				uris = new ArrayList<>(4);
				lookups.put(specimenId, uris);
			}
			uris.addAll(item.getItem().getServiceAccessPoints());
		}
		return lookups;
	}

}
