package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeEmlXml;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeMetaXml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.format.csv.CsvRecordWriter;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.ESUtil;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
class SingleDataSourceDwcaWriter implements IDwcaWriter {

	private static final Logger logger = getLogger(SingleDataSourceDwcaWriter.class);
	private static final TimeValue TIME_OUT = new TimeValue(500);

	private final DwcaConfig dwcaConfig;
	private final OutputStream out;

	SingleDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out)
	{
		this.dwcaConfig = dwcaConfig;
		this.out = out;
	}

	@Override
	public void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException
	{
		logger.info("Generating DarwinCore archive for user-defined query");
		try {
			RandomEntryZipOutputStream rezos = createZipStream();
			writeCsvFiles(querySpec, rezos);
			ZipOutputStream zos = rezos.mergeEntries();
			writeMetaXml(dwcaConfig, zos);
			writeEmlXml(dwcaConfig, zos);
			zos.finish();
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		logger.info("Finished writing DarwinCore archive for user-defined query");
	}

	@Override
	public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException
	{
		String fmt = "Generating DarwinCore archive for data set \"{}\"";
		logger.info(fmt, dwcaConfig.getDataSetName());
		QuerySpec query = dwcaConfig.getDataSet().getSharedDataSource().getQuerySpec();
		try {
			RandomEntryZipOutputStream rezos = createZipStream();
			logger.info("Adding CSV files");
			writeCsvFiles(query, rezos);
			ZipOutputStream zos = rezos.mergeEntries();
			writeMetaXml(dwcaConfig, zos);
			writeEmlXml(dwcaConfig, zos);
			zos.finish();
		}
		catch (InvalidQueryException e) {
			/*
			 * Not the user's fault but the application maintainer's: the query
			 * was defined in the XML configuration file. So we convert the
			 * InvalidQueryException to a DataSetConfigurationException
			 */
			fmt = "Invalid query specification for shared data source:\n%s";
			String queryString = JsonUtil.toPrettyJson(query);
			String msg = String.format(fmt, queryString);
			throw new DataSetConfigurationException(msg);
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		fmt = "Finished writing DarwinCore archive for data set \"{}\"";
		logger.info(fmt, dwcaConfig.getDataSetName());
	}

	private void writeCsvFiles(QuerySpec querySpec, RandomEntryZipOutputStream rezos)
			throws DataSetConfigurationException, DataSetWriteException, IOException,
			InvalidQueryException
	{
		SearchResponse response = executeQuery(querySpec);
		CsvRecordWriter[] printers = getPrinters(rezos);
		DocumentFlattener[] flatteners = getFlatteners();
		String[] fileNames = getCsvFileNames();
		for (int i = 0; i < printers.length; i++) {
			rezos.setActiveEntry(fileNames[i]);
			printers[i].printBOM();
			printers[i].printHeader();
		}
		int processed = 0;
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		int[] written = new int[entities.length];
		int[] filtered = new int[entities.length];
		// The scroll loop:
		while (true) {
			// Loop over documents in current page:
			for (SearchHit hit : response.getHits().getHits()) {
				// Loop over entities in data set:
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
				if (logger.isDebugEnabled() && processed % 100000 == 0) {
					logger.debug("Documents processed: " + processed);
				}
			}
			String scrollId = response.getScrollId();
			Client client = ESClientManager.getInstance().getClient();
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
		logger.info("Documents processed: {}", processed);
		for (int i = 0; i < entities.length; i++) {
			logger.info("Records written for entity {}  : {}", entities[i].getName(), written[i]);
			logger.info("Records rejected by filters for entity {} : {}", entities[i].getName(),
					filtered[i]);
		}
		rezos.flush();
	}

	private RandomEntryZipOutputStream createZipStream()
			throws DataSetConfigurationException, DataSetWriteException, IOException
	{
		Entity coreEntity = dwcaConfig.getCoreEntity();
		String fileName = dwcaConfig.getCsvFileName(coreEntity);
		RandomEntryZipOutputStream rezos;
		rezos = new RandomEntryZipOutputStream(out, fileName);
		for (Entity e : dwcaConfig.getDataSet().getEntities()) {
			if (e.getName().equals(coreEntity.getName())) {
				continue;
			}
			fileName = dwcaConfig.getCsvFileName(e);
			rezos.addEntry(fileName, 1024 * 1024);
		}
		return rezos;
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

	private String[] getCsvFileNames() throws DataSetConfigurationException
	{
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		String[] fileNames = new String[entities.length];
		for (int i = 0; i < entities.length; i++) {
			fileNames[i] = dwcaConfig.getCsvFileName(entities[i]);
		}
		return fileNames;
	}

	private static SearchResponse executeQuery(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator qst = new QuerySpecTranslator(spec, DocumentType.TAXON);
		SearchRequestBuilder request = qst.translate();
		request.addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(TIME_OUT);
		request.setSize(1000);
		return ESUtil.executeSearchRequest(request);
	}

}
