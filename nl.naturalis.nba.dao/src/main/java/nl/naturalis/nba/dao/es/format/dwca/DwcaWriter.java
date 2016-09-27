package nl.naturalis.nba.dao.es.format.dwca;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.IOUtil;
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
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
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
public class DwcaWriter {

	private static Logger logger = LogManager.getLogger(DwcaWriter.class);
	private static TimeValue TIME_OUT = new TimeValue(5000);

	private DwcaConfig dwcaConfig;
	private ZipOutputStream zos;

	public DwcaWriter(DwcaConfig dwcaConfig, OutputStream out)
	{
		this.dwcaConfig = dwcaConfig;
		this.zos = new ZipOutputStream(out);
	}

	public void writeDwcaForQuery(QuerySpec querySpec)
			throws DataSetConfigurationException, InvalidQueryException
	{
		logger.info("Generating DarwinCore archive for user-defined query");
		writeEmlXml();
		writeMetaXml();
		writeCsvFilesForQuery(querySpec);
		finish();
		logger.info("Finished writing DarwinCore archive for user-defined query");
	}

	public void writeDwcaForDataSet() throws DataSetConfigurationException
	{
		logger.info("Generating DarwinCore archive for data set \"{}\"",
				dwcaConfig.getDataSetName());
		writeEmlXml();
		writeMetaXml();
		writeCsvFilesForDataSet();
		finish();
		logger.info("Finished writing DarwinCore archive for data set \"{}\"",
				dwcaConfig.getDataSetName());
	}

	private void writeCsvFilesForQuery(QuerySpec querySpec)
			throws DataSetConfigurationException, InvalidQueryException
	{

		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			String fileName = dwcaConfig.getCsvFileName(entity);
			logger.info("Generating CSV file for entity {}", entity.getName());
			newZipEntry(fileName);
			writeCsvFile(entity, executeQuery(querySpec));
		}
	}

	private void writeCsvFilesForDataSet() throws DataSetConfigurationException
	{
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			String fileName = dwcaConfig.getCsvFileName(entity);
			logger.info("Generating CSV file for entity {}", entity.getName());
			newZipEntry(fileName);
			QuerySpec query = entity.getDataSource().getQuerySpec();
			SearchResponse response;
			try {
				response = executeQuery(query);
			}
			catch (InvalidQueryException e) {
				/*
				 * Now it's not the user's fault but the application
				 * maintainer's, so we convert the InvalidQueryException to a
				 * DataSetConfigurationException
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
	{
		Path path = entity.getDataSource().getPath();
		DocumentFlattener flattener = new DocumentFlattener(path);
		List<IField> fields = entity.getFields();
		CsvPrinter csvPrinter = new CsvPrinter(fields, flattener, zos);
		csvPrinter.printHeader();
		int processed = 0;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				if (++processed % 10000 == 0)
					csvPrinter.flush();
				if (logger.isDebugEnabled() && processed % 100000 == 0)
					logger.debug("Documents processed: " + processed);
				csvPrinter.printRecord(hit.getSource());
			}
			String scrollId = response.getScrollId();
			Client client = ESClientManager.getInstance().getClient();
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
		flush();
	}

	//	private void writeCsvFiles(SearchResponse response)
	//	{
	//		int processed = 0;
	//		while (true) {
	//			for (SearchHit hit : response.getHits().getHits()) {
	//				Map<String, Object> data = hit.getSource();
	//				for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
	//
	//				}
	//			}
	//			String scrollId = response.getScrollId();
	//			Client client = ESClientManager.getInstance().getClient();
	//			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
	//			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
	//			if (response.getHits().getHits().length == 0) {
	//				break;
	//			}
	//		}
	//		flush();
	//	}

	private void writeMetaXml() throws DataSetConfigurationException
	{
		logger.info("Generating meta.xml");
		newZipEntry("meta.xml");
		Archive archive = new Archive();
		Core core = new Core();
		archive.setCore(core);
		Entity coreEntity = dwcaConfig.getCoreEntity();
		core.setFiles(new Files(dwcaConfig.getCsvFileName(coreEntity)));
		core.setRowType(dwcaConfig.getRowtype(coreEntity));
		core.setFields(getMetaXmlFieldsForEntity(coreEntity));
		for (Entity entity : dwcaConfig.getDataSet().getEntities()) {
			if (entity.getName().equals(coreEntity.getName()))
				continue;
			Extension extension = new Extension();
			extension.setFiles(new Files(dwcaConfig.getRowtype(entity)));
			extension.setRowType(dwcaConfig.getRowtype(entity));
			extension.setFields(getMetaXmlFieldsForEntity(entity));
			archive.addExtension(extension);
		}
		MetaXmlWriter metaXmlWriter = new MetaXmlWriter(archive);
		metaXmlWriter.write(zos);
		flush();
	}

	private void writeEmlXml() throws DataSetConfigurationException
	{
		logger.info("Adding eml.xml to archive ({})", dwcaConfig.getEmlFile());
		newZipEntry("eml.xml");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(dwcaConfig.getEmlFile());
			IOUtil.pipe(fis, zos, 2048);
			flush();
		}
		catch (FileNotFoundException e) { /* Already checked */
			throw new DaoException(e);
		}
		finally {
			IOUtil.close(fis);
		}
	}

	private static List<Field> getMetaXmlFieldsForEntity(Entity entity)
			throws DataSetConfigurationException
	{
		List<IField> entityFields = entity.getFields();
		List<Field> metaXmlFields = new ArrayList<>(entityFields.size());
		for (int i = 0; i < entityFields.size(); i++) {
			IField entityField = entityFields.get(i);
			URI term = entityField.getTerm();
			if (term == null) {
				String fmt = "Entity %s, field %s: term attribute required for DwCA files";
				String msg = String.format(fmt, entity.getName(), entityField.getName());
				throw new DataSetConfigurationException(msg);
			}
			metaXmlFields.add(new Field(i, term));
		}
		return metaXmlFields;
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

	private ZipEntry newZipEntry(String name)
	{
		ZipEntry entry = new ZipEntry(name);
		try {
			zos.putNextEntry(entry);
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
		return entry;
	}

	private void flush()
	{
		try {
			zos.flush();
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}

	private void finish()
	{
		try {
			zos.finish();
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}
}
