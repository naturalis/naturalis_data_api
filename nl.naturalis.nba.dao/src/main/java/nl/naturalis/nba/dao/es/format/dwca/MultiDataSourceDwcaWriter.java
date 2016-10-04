package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.writeEmlXml;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.writeMetaXml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
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
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.DataSetWriteException;
import nl.naturalis.nba.dao.es.format.DocumentFlattener;
import nl.naturalis.nba.dao.es.format.Entity;
import nl.naturalis.nba.dao.es.format.IField;
import nl.naturalis.nba.dao.es.format.csv.CsvPrinter;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
class MultiDataSourceDwcaWriter implements IDwcaWriter {

	private static Logger logger = LogManager.getLogger(MultiDataSourceDwcaWriter.class);
	private static TimeValue TIME_OUT = new TimeValue(5000);

	private DwcaConfig dwcaConfig;
	private ZipOutputStream zos;

	MultiDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out)
	{
		this.dwcaConfig = dwcaConfig;
		this.zos = new ZipOutputStream(out);
	}

	@Override
	public void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException
	{
		logger.info("Generating DarwinCore archive for user-defined query");
		try {
			writeMetaXml(dwcaConfig, zos);
			writeEmlXml(dwcaConfig, zos);
			writeCsvFilesForQuery(querySpec);
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
		try {
			writeMetaXml(dwcaConfig, zos);
			writeEmlXml(dwcaConfig, zos);
			writeCsvFilesForDataSet();
			zos.finish();
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		fmt = "Finished writing DarwinCore archive for data set \"{}\"";
		logger.info(fmt, dwcaConfig.getDataSetName());
	}

	private void writeCsvFilesForQuery(QuerySpec querySpec) throws InvalidQueryException,
			DataSetConfigurationException, DataSetWriteException, IOException
	{
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			String fileName = dwcaConfig.getCsvFileName(entity);
			logger.info("Adding CSV file for entity {}", entity.getName());
			zos.putNextEntry(new ZipEntry(fileName));
			writeCsvFile(entity, executeQuery(querySpec));
		}
	}

	private void writeCsvFilesForDataSet()
			throws DataSetConfigurationException, DataSetWriteException, IOException
	{
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			String fileName = dwcaConfig.getCsvFileName(entity);
			logger.info("Adding CSV file for entity {}", entity.getName());
			zos.putNextEntry(new ZipEntry(fileName));
			QuerySpec query = entity.getDataSource().getQuerySpec();
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
				String fmt = "Invalid query specification for entity %s:\n%s";
				String queryString = JsonUtil.toPrettyJson(query);
				String msg = String.format(fmt, entity, queryString);
				throw new DataSetConfigurationException(msg);
			}
			writeCsvFile(entity, response);
		}
	}

	private void writeCsvFile(Entity entity, SearchResponse response)
			throws DataSetWriteException, IOException
	{
		Path path = entity.getDataSource().getPath();
		DocumentFlattener flattener = new DocumentFlattener(path);
		List<IField> fields = entity.getFields();
		CsvPrinter csvPrinter = new CsvPrinter(fields, flattener, zos);
		csvPrinter.printHeader();
		int processed = 0;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				csvPrinter.printRecord(hit.getSource());
				if (++processed % 10000 == 0) {
					csvPrinter.flush();
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
		zos.flush();
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
