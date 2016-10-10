package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeEmlXml;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeMetaXml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
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
import nl.naturalis.nba.dao.format.csv.CsvPrinter;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
class SingleDataSourceDwcaWriter implements IDwcaWriter {

	private static Logger logger = LogManager.getLogger(SingleDataSourceDwcaWriter.class);
	private static TimeValue TIME_OUT = new TimeValue(5000);

	private DwcaConfig dwcaConfig;
	private OutputStream out;

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
		SearchResponse response = executeQuery(querySpec);
		RandomEntryZipOutputStream rezos;
		try {
			rezos = createZipStream();
			writeCsvFiles(response, rezos);
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
		SearchResponse response;
		try {
			response = executeQuery(query);
		}
		catch (InvalidQueryException e) {
			/*
			 * Not the user's fault but the application maintainer's, because we
			 * got the QuerySpec from the config file, so we convert the
			 * InvalidQueryException to a DataSetConfigurationException
			 */
			fmt = "Invalid query specification for shared data source:\n%s";
			String queryString = JsonUtil.toPrettyJson(query);
			String msg = String.format(fmt, queryString);
			throw new DataSetConfigurationException(msg);
		}
		RandomEntryZipOutputStream rezos;
		try {
			rezos = createZipStream();
			logger.info("Adding CSV files");
			writeCsvFiles(response, rezos);
			ZipOutputStream zos = rezos.mergeEntries();
			writeMetaXml(dwcaConfig, zos);
			writeEmlXml(dwcaConfig, zos);
			zos.finish();
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		fmt = "Finished writing DarwinCore archive for data set \"{}\"";
		logger.info(fmt, dwcaConfig.getDataSetName());
	}

	private void writeCsvFiles(SearchResponse response, RandomEntryZipOutputStream rezos)
			throws DataSetConfigurationException, DataSetWriteException, IOException
	{
		CsvPrinter[] printers = getPrinters(rezos);
		DocumentFlattener[] flatteners = getFlatteners();
		String[] fileNames = getCsvFileNames();
		for (int i = 0; i < printers.length; i++) {
			rezos.setActiveEntry(fileNames[i]);
			printers[i].printBOM();
			printers[i].printHeader();
		}
		int processed = 0;
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
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
								continue ENTITY_OBJECT_LOOP;
							}
						}
						rezos.setActiveEntry(fileNames[i]);
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

	private CsvPrinter[] getPrinters(OutputStream out)
	{
		Entity[] entities = dwcaConfig.getDataSet().getEntities();
		CsvPrinter[] printers = new CsvPrinter[entities.length];
		for (int i = 0; i < entities.length; i++) {
			IField[] fields = entities[i].getFields();
			printers[i] = new CsvPrinter(fields, out);
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
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		logger.info("Elasticsearch documents to be processed: {}", response.getHits().totalHits());
		return response;
	}

}
