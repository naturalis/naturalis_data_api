package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.csv.CsvColumnConfigurator;
import nl.naturalis.nba.dao.es.csv.CsvPrinter;
import nl.naturalis.nba.dao.es.csv.IColumn;
import nl.naturalis.nba.dao.es.dwca.MetaXmlGenerator;
import nl.naturalis.nba.dao.es.dwca.OccurrenceMetaXmlGenerator;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

public class SpecimenDwcaDao {

	private static final TimeValue TIME_OUT = new TimeValue(200);

	private static final Logger logger;

	static {
		logger = DAORegistry.getInstance().getLogger(SpecimenDwcaDao.class);
	}

	@SuppressWarnings("static-method")
	public void queryDynamic(QuerySpec spec, OutputStream os) throws InvalidQueryException
	{
		ZipOutputStream zos = new ZipOutputStream(os);
		newZipEntry(zos, "meta.xml");
		IColumn[] columns = getColumns("dynamic");
		MetaXmlGenerator mxg = new OccurrenceMetaXmlGenerator(columns);
		mxg.generateMetaXml(zos);
		newZipEntry(zos, "occurrence.txt");
		CsvPrinter csvPrinter = new CsvPrinter(columns, zos);
		SearchResponse response = executeQuery(spec);
		csvPrinter.printHeader();
		int processed = 0;
		LOOP: while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
					csvPrinter.flush();
					break LOOP;
				}
				csvPrinter.printRecord(hit.getSource());
			}
			String scrollId = response.getScrollId();
			SearchScrollRequestBuilder ssrb = client().prepareSearchScroll(scrollId);
			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
		try {
			zos.closeEntry();
			zos.close();
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}

	public void queryStatic(String datasetName, OutputStream os) throws InvalidQueryException
	{
	}

	public static IColumn[] getColumns(String collection)
	{
		String relativePath = "dwca/specimen/" + collection;
		File confDir = DAORegistry.getInstance().getConfigurationDirectory();
		File dwcaConfDir = FileUtil.newFile(confDir, relativePath);
		CsvColumnConfigurator ccc = CsvColumnConfigurator.getInstance();
		IColumn[] columns = ccc.getColumns(dwcaConfDir);
		return columns;
	}

	public static SearchResponse executeQuery(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator qst = new QuerySpecTranslator(spec, SPECIMEN);
		SearchRequestBuilder request = qst.translate();
		request.addSort(DOC_FIELD_NAME, SortOrder.ASC);
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

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}

}
