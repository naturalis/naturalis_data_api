package nl.naturalis.nba.dao.es.format.dwca;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.ConfigObject.MissingPropertyException;
import org.domainobject.util.ConfigObject.PropertyNotSetException;
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
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
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

	private DataSet dataSet;
	private ZipOutputStream zos;
	private ConfigObject dwcaConfig;

	public DwcaWriter(DataSet dataSet, ZipOutputStream zos)
	{
		this.dataSet = dataSet;
		this.zos = zos;
		this.dwcaConfig = ConfigObject.forResource("/dwca.properties");
	}

	public void write() throws DataSetConfigurationException
	{
		try {
			writeMetaXml();
			writeCsvFiles();
		}
		finally {
			finish(zos);
		}
	}

	public void writeCsvFiles() throws DataSetConfigurationException
	{
		for (Entity entity : dataSet.getEntities()) {
			newZipEntry(zos, getFileNameForEntity(entity));
			Path path = entity.getDataSource().getPath();
			QuerySpec query = entity.getDataSource().getQuerySpec();
			DocumentFlattener flattener = new DocumentFlattener(path);
			SearchResponse response;
			try {
				response = executeQuery(query);
			}
			catch (InvalidQueryException e) {
				String fmt = "Invalid query specification for entity %s:\n%s";
				String queryString = JsonUtil.toPrettyJson(query);
				String msg = String.format(fmt, entity, queryString);
				throw new DataSetConfigurationException(msg);
			}
			List<IField> fields = entity.getFields();
			CsvPrinter csvPrinter = new CsvPrinter(fields, flattener, zos);
			csvPrinter.printHeader();
			int processed = 0;
			while (true) {
				for (SearchHit hit : response.getHits().getHits()) {
					if (++processed % 50000 == 0) {
						logger.debug("Records processed: " + processed);
						csvPrinter.flush();
					}
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
		}
	}

	public void writeMetaXml() throws DataSetConfigurationException
	{
		newZipEntry(zos, "meta.xml");
		Archive archive = new Archive();
		Core core = new Core();
		archive.setCore(core);
		Entity taxon = dataSet.getEntity("TAXON");
		core.setFiles(new Files(getFileNameForEntity(taxon)));
		core.setRowType(getRowTypeForEntity(taxon));
		core.setFields(getMetaXmlFieldsForEntity(taxon));
		for (Entity entity : dataSet.getEntities()) {
			if (entity.getName().equals("TAXON"))
				continue;
			Extension extension = new Extension();
			extension.setFiles(new Files(getFileNameForEntity(entity)));
			extension.setRowType(getRowTypeForEntity(entity));
			extension.setFields(getMetaXmlFieldsForEntity(entity));
			archive.addExtension(extension);
		}
		MetaXmlWriter metaXmlWriter = new MetaXmlWriter(archive);
		metaXmlWriter.write(zos);
	}

	private String getFileNameForEntity(Entity entity) throws DataSetConfigurationException
	{
		String property = "taxon.entity." + entity.getName() + ".location";
		try {
			return dwcaConfig.required(property);
		}
		catch (PropertyNotSetException | MissingPropertyException e) {
			throw new DataSetConfigurationException(e.getMessage());
		}
	}

	private String getRowTypeForEntity(Entity entity) throws DataSetConfigurationException
	{
		String property = "taxon.entity." + entity.getName() + ".rowtype";
		try {
			return dwcaConfig.required(property);
		}
		catch (PropertyNotSetException | MissingPropertyException e) {
			throw new DataSetConfigurationException(e.getMessage());
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
		SearchResponse response = request.execute().actionGet();
		return response;
	}

	private static ZipEntry newZipEntry(ZipOutputStream zos, String name)
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

	private static void finish(ZipOutputStream zos)
	{
		try {
			zos.finish();
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}
}
