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
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.utils.FileUtil;
import nl.naturalis.nba.utils.IOUtil;

class SpecimenNameImporter2 {

	public static void main(String[] args) throws Exception
	{
		try {
			SpecimenNameImporter2 importer = new SpecimenNameImporter2();
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
		logger.info("Saving enriched specimens to temp file: " + tempFile.getAbsolutePath());
		saveToTempFile();
		logger.info("Importing specimens from temp file");
		importTempFile();
		tempFile.delete();
		logDuration(logger, getClass(), start);
	}

	void saveToTempFile() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		DocumentIterator<Specimen> extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(readBatchSize);
		extractor.setTimeout(scrollTimeout);
		List<Specimen> batch = extractor.nextBatch();
		int batchNo = 0;
		try {
			while (batch != null) {
				SpecimenToNameGroupConverter converter = new SpecimenToNameGroupConverter(batch);
				Collection<ScientificNameGroup> nameGroups = converter.convert();
				for (ScientificNameGroup sng : nameGroups) {
					byte[] json = JsonUtil.serialize(sng);
					bos.write(json);
					bos.write(NEW_LINE);
				}
				if ((++batchNo % 100) == 0) {
					logger.info("Specimens processed: {}", (batchNo * readBatchSize));
				}
				batch = extractor.nextBatch();
			}
		}
		finally {
			bos.close();
			logger.info("Specimens processed: {}", (batchNo * readBatchSize));
		}
	}

	private void importTempFile() throws IOException, BulkIndexException
	{
		BulkIndexer<ScientificNameGroup> indexer = new BulkIndexer<>(SCIENTIFIC_NAME_GROUP);
		List<ScientificNameGroup> batch = new ArrayList<>(writeBatchSize);
		LineNumberReader lnr = null;
		int processed = 0;
		try {
			FileReader fr = new FileReader(tempFile);
			lnr = new LineNumberReader(fr, 4096);
			String line;
			while ((line = lnr.readLine()) != null) {
				ScientificNameGroup sng = JsonUtil.deserialize(line, ScientificNameGroup.class);
				batch.add(sng);
				if (batch.size() == writeBatchSize) {
					NameGroupMerger merger = new NameGroupMerger(batch);
					indexer.index(merger.merge());
					batch.clear();
				}
				if (++processed % 100000 == 0) {
					logger.info("Records processed: {}", processed);
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
		String fileName = String.format("sng-%s.json", time);
		return FileUtil.newFile(tmpDir, fileName);
	}

}
