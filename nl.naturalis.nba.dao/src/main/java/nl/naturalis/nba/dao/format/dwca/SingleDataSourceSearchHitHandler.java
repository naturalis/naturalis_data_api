package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.format.csv.CsvRecordWriter;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

final class SingleDataSourceSearchHitHandler implements SearchHitHandler {

	private static final Logger logger = getLogger(SingleDataSourceSearchHitHandler.class);

	private final DwcaConfig dwcaConfig;
	private final RandomEntryZipOutputStream rezos;
	private final Entity[] entities;
	private final String[] fileNames;
	private final CsvRecordWriter[] printers;
	private final DocumentFlattener[] flatteners;

	private int processed = 0;
	private final int[] written;
	private final int[] filtered;

	SingleDataSourceSearchHitHandler(DwcaConfig dwcaConfig, RandomEntryZipOutputStream rezos)
			throws DataSetConfigurationException
	{
		this.dwcaConfig = dwcaConfig;
		this.rezos = rezos;
		entities = dwcaConfig.getDataSet().getEntities();
		fileNames = getCsvFileNames();
		printers = getPrinters(rezos);
		flatteners = getFlatteners();
		written = new int[entities.length];
		filtered = new int[entities.length];
	}

	@Override
	public boolean handle(SearchHit hit) throws DataSetWriteException
	{
		try {
			for (int i = 0; i < entities.length; i++) {
				// Squash current document and loop over resulting entity objects:
				List<EntityObject> eos = flatteners[i].flatten(hit.getSource());
				ENTITY_OBJECT_LOOP: for (EntityObject eo : eos) {
					// Loop over filters defined for current entity:
					for (IEntityFilter filter : entities[i].getFilters()) {
						if (!filter.accept(eo)) {
							filtered[i] += 1;
							continue ENTITY_OBJECT_LOOP;
						}
					}
					rezos.setActiveEntry(fileNames[i]);
					written[i] += 1;
					printers[i].printRecord(eo);
				}
			}
			if (++processed % 10000 == 0) {
				rezos.flush();
			}
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
		if (logger.isDebugEnabled() && processed % 100000 == 0) {
			logger.debug("Documents processed: " + processed);
		}
		return true;
	}

	void printHeaders() throws IOException
	{
		for (int i = 0; i < printers.length; i++) {
			rezos.setActiveEntry(fileNames[i]);
			printers[i].printBOM();
			printers[i].printHeader();
		}
	}

	void logStatistics()
	{
		logger.info("Documents processed: {}", processed);
		for (int i = 0; i < entities.length; i++) {
			logger.info("Records written for entity {}  : {}", entities[i].getName(), written[i]);
			logger.info("Records rejected by filters for entity {} : {}", entities[i].getName(),
					filtered[i]);
		}
	}

	private String[] getCsvFileNames() throws DataSetConfigurationException
	{
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		String[] fileNames = new String[entities.length];
		for (int i = 0; i < entities.length; i++) {
			fileNames[i] = dwcaConfig.getCsvFileName(entities[i]);
		}
		return fileNames;
	}

	private CsvRecordWriter[] getPrinters(OutputStream out)
	{
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		CsvRecordWriter[] printers = new CsvRecordWriter[entities.length];
		for (int i = 0; i < entities.length; i++) {
			IField[] fields = entities[i].getFields();
			printers[i] = new CsvRecordWriter(fields, out);
		}
		return printers;
	}

	private DocumentFlattener[] getFlatteners()
	{
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		DocumentFlattener[] flatteners = new DocumentFlattener[entities.length];
		for (int i = 0; i < entities.length; i++) {
			Path path = entities[i].getDataSource().getPath();
			flatteners[i] = new DocumentFlattener(path);
		}
		return flatteners;
	}
}
