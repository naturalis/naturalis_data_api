package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_SCROLL_TIMEOUT;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_WRITE_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
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

class SpecimenNameImporter2 {

	public static void main(String[] args) throws Exception
	{
		try {
			ESUtil.truncate(DocumentType.SCIENTIFIC_NAME_GROUP);
			ESUtil.refreshIndex(SCIENTIFIC_NAME_GROUP);
			SpecimenNameImporter2 importer = new SpecimenNameImporter2();
			importer.importNames();
		}
		catch (Exception e) {
			logger.fatal("SpecimenNameImporter aborted unexpectedly", e);
			throw e;
		}
		finally {
			ESUtil.refreshIndex(SCIENTIFIC_NAME_GROUP);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(SpecimenNameImporter2.class);
	private static final byte[] NEW_LINE = "\n".getBytes();

	private boolean suppressErrors;
	private int readBatchSize = 1000;
	private int writeBatchSize = 1000;
	private int scrollTimeout = 60000;

	private File tempFile;

	void importNames() throws IOException, BulkIndexException
	{
		long start = System.currentTimeMillis();
		tempFile = getTempFile();
		logger.info("Read batch size: {}", readBatchSize);
		logger.info("Write batch size: {}", writeBatchSize);
		logger.info("Writing name groups to {}", tempFile.getAbsolutePath());
		saveToTempFile();
		logger.info("Reading name groups from {}", tempFile.getAbsolutePath());
		importTempFile();
		//tempFile.delete();
		logDuration(logger, getClass(), start);
	}

	void saveToTempFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		SpecimenToNameGroupConverter converter = new SpecimenToNameGroupConverter();
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(readBatchSize);
		extractor.setTimeout(scrollTimeout);
		List<Specimen> batch = extractor.nextBatch();
		int batchNo = 0;
		int processed = 0;
		int written = 0;
		try {
			while (batch != null) {
				Collection<ScientificNameGroup> nameGroups = converter.convert(batch);
				for (ScientificNameGroup sng : nameGroups) {
					byte[] json = JsonUtil.serialize(sng);
					bos.write(json);
					bos.write(NEW_LINE);
				}
				processed += batch.size();
				written += nameGroups.size();
				if ((++batchNo % 100) == 0) {
					logger.info("Specimens processed: {}", processed);
					logger.info("Specimen identifications processed: {}",
							converter.getNumIdentifications());
					logger.info("Name groups (lines) written: {}", written);
				}
				batch = extractor.nextBatch();
			}
		}
		finally {
			bos.close();
			logger.info("Specimens processed: {}", processed);
			logger.info("Specimen identifications processed: {}",
					converter.getNumIdentifications());
			logger.info("Name groups (lines) written: {}", written);
			logger.info("N.B. name groups written to temp file are not unique!");
		}
	}

	private void importTempFile() throws IOException, BulkIndexException
	{
		NameGroupMerger merger = new NameGroupMerger();
		BulkIndexer<ScientificNameGroup> indexer = new BulkIndexer<>(SCIENTIFIC_NAME_GROUP);
		List<ScientificNameGroup> batch = new ArrayList<>(writeBatchSize);
		LineNumberReader lnr = null;
		try {
			FileReader fr = new FileReader(tempFile);
			lnr = new LineNumberReader(fr, 4096);
			String line;
			while ((line = lnr.readLine()) != null) {
				ScientificNameGroup sng = JsonUtil.deserialize(line, ScientificNameGroup.class);
				batch.add(sng);
				if (batch.size() == writeBatchSize) {
					indexer.index(merger.merge(batch));
					batch.clear();
				}
				if (lnr.getLineNumber() % 1000 == 0) {
					logger.info("Lines read: {}", lnr.getLineNumber());
					logger.info("Name groups created: {}", merger.getNumCreated());
					logger.info("Name groups merged: {}", merger.getNumMerged());
				}
			}
		}
		finally {
			lnr.close();
		}
		if (batch.size() != 0) {
			indexer.index(merger.merge(batch));
		}
		logger.info("Lines read: {}", lnr.getLineNumber());
		logger.info("Name groups created: {}", merger.getNumCreated());
		logger.info("Name groups merged: {}", merger.getNumMerged());
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

	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
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

	private static File getTempFile() throws IOException
	{
		File tmpDir = DaoRegistry.getInstance().getFile("../tmp").getCanonicalFile();
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdir();
		}
		int time = (int) (new Date().getTime() / 1000);
		String fileName = String.format("SpecimenNameImporter-%s.json", time);
		return FileUtil.newFile(tmpDir, fileName);
	}

}