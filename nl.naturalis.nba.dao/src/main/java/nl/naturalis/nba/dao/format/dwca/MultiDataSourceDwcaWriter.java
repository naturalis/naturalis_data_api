package nl.naturalis.nba.dao.format.dwca;

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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QuerySpec;
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
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.es.ESUtil;

/**
 * Manages the assembly and creation of DarwinCore archives. Use this class if
 * you cannot generate all CSV files from a single query (each CSV file requires
 * a new query to be executed). With this class a separate query is issued for
 * each entity (i.e. each file generated as part of the DwC archive).
 * 
 * @author Ayco Holleman
 *
 */
/*
 * WARNING Since we currently don't have to deal with this situation, this class
 * has fallen a behind the SingleDataSourceDwcaWriter, so we need to update it
 * once we run into this situation again.
 */
class MultiDataSourceDwcaWriter implements IDwcaWriter {

	private static Logger logger = LogManager.getLogger(MultiDataSourceDwcaWriter.class);
	private static TimeValue TIME_OUT = new TimeValue(10000);

	private DwcaConfig cfg;
	private ZipOutputStream zos;

	MultiDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out)
	{
		this.cfg = dwcaConfig;
		this.zos = new ZipOutputStream(out);
	}

	@Override
	public void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException
	{
		logger.info("Generating DarwinCore archive for user-defined query");
		DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
		dwcaPreparator.prepare();
		try {
			logger.info("Adding meta.xml");
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getMetaXml());
			logger.info("Adding eml.xml ({})", cfg.getEmlFile());
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getEml());
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
		logger.info(fmt, cfg.getDataSetName());
		DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
		dwcaPreparator.prepare();
		try {
			logger.info("Adding meta.xml");
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getMetaXml());
			logger.info("Adding eml.xml ({})", cfg.getEmlFile());
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getEml());
			writeCsvFilesForDataSet();			
			zos.finish();
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		fmt = "Finished writing DarwinCore archive for data set \"{}\"";
		logger.info(fmt, cfg.getDataSetName());
	}

	private void writeCsvFilesForQuery(QuerySpec querySpec) throws InvalidQueryException,
			DataSetConfigurationException, DataSetWriteException, IOException
	{
		for (Entity entity : cfg.getDataSet().getEntities()) {
			String fileName = cfg.getCsvFileName(entity);
			logger.info("Adding CSV file for entity {}", entity.getName());
			zos.putNextEntry(new ZipEntry(fileName));
			writeCsvFile(entity, executeQuery(querySpec));
		}
	}

	private void writeCsvFilesForDataSet()
			throws DataSetConfigurationException, DataSetWriteException, IOException
	{
		for (Entity entity : cfg.getDataSet().getEntities()) {
			String fileName = cfg.getCsvFileName(entity);
			logger.info("Adding CSV file for entity {}", entity.getName());
			zos.putNextEntry(new ZipEntry(fileName));
			QuerySpec query = entity.getDataSource().getQuerySpec();
			SearchResponse response;
			try {
				response = executeQuery(query);
			}
			catch (InvalidQueryException e) {
				/*
				 * Not the user's fault but the application maintainer's,
				 * because we got the QuerySpec from the config file, so we
				 * convert the InvalidQueryException to a
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
			throws DataSetWriteException, IOException
	{
		Path path = entity.getDataSource().getPath();
		DocumentFlattener flattener = new DocumentFlattener(path);
		IField[] fields = entity.getFields();
		CsvRecordWriter csvPrinter = new CsvRecordWriter(fields, zos);
		csvPrinter.printBOM();
		csvPrinter.printHeader();
		int processed = 0;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				List<EntityObject> eos = flattener.flatten(hit.getSource());
				ENTITY_OBJECT_LOOP: for (EntityObject eo : eos) {
					for (IEntityFilter filter : entity.getFilters()) {
						if (!filter.accept(eo)) {
							continue ENTITY_OBJECT_LOOP;
						}
					}
					csvPrinter.printRecord(eo);
				}
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
		request.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(TIME_OUT);
		request.setSize(1000);
		return ESUtil.executeSearchRequest(request);
	}
}
