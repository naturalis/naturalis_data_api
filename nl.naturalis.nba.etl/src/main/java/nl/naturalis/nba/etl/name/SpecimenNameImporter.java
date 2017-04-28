package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;

class SpecimenNameImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			SpecimenNameImporter importer = new SpecimenNameImporter();
			importer.importNames();
		}
		catch (Exception e) {
			logger.fatal("SpecimenNameImporter aborted unexpectedly", e);
			throw e;
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(SpecimenNameImporter.class);

	private boolean suppressErrors;
	private int batchSize = 1000;
	private int timeout = 60000;

	void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Specimen> extractor;
		SpecimenNameTransformer transformer;
		QuerySpec qs = new QuerySpec();
		qs.setConstantScore(true);
		qs.sortBy("identifications.scientificName.scientificNameGroup");
		logger.info("Initializing extractor for specimens. Batch size is {}", batchSize);
		extractor = new DocumentIterator<>(SPECIMEN, qs);
		logger.info("Number of specimens to be processed: {}",extractor.size());
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		transformer = new SpecimenNameTransformer(batchSize);
		BulkIndexer<ScientificNameGroup> indexer = new BulkIndexer<>(SCIENTIFIC_NAME_GROUP);
		List<Specimen> batch = extractor.nextBatch();
		int batchNo = 0;
		while (batch != null) {
			Collection<ScientificNameGroup> nameGroups = transformer.transform(batch);
			if (nameGroups != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Indexing {} new/updated name group(s)", nameGroups.size());
				}
				indexer.index(nameGroups);
				refreshIndex(SCIENTIFIC_NAME_GROUP);
			}
			if ((++batchNo % 10) == 0) {
				logger.info("Specimens processed: {}", (batchNo * batchSize));
				logger.info("Name groups created: {}", transformer.getNumCreated());
				logger.info("Name groups updated: {}", transformer.getNumUpdated());
				Specimen last = batch.get(batch.size() - 1);
				List<SpecimenIdentification> sis = last.getIdentifications();
				if (sis != null) {
					String group = sis.get(0).getScientificName().getScientificNameGroup();
					logger.info("Most recent name group: {}", group);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug(">>>>>>>> Loading next batch of specimens <<<<<<<<");
			}
			batch = extractor.nextBatch();
		}
		logger.debug("");
		logger.info("Specimens processed: {}", extractor.getDocCounter());
		logger.info("Name groups created: {}", transformer.getNumCreated());
		logger.info("Name groups updated: {}", transformer.getNumUpdated());
		logDuration(logger, getClass(), start);
	}

	boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	int getBatchSize()
	{
		return batchSize;
	}

	void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	int getTimeout()
	{
		return timeout;
	}

	void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

}
