package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getCsvFileName;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getEmlFile;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getFields;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getMetaXmlGenerator;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getQuerySpec;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.IOUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.csv.CsvPrinter;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaWriter {

	private static final Logger logger = DaoRegistry.getInstance().getLogger(DwcaWriter.class);
	private static final TimeValue TIME_OUT = new TimeValue(5000);

	private DataSetCollection dsc;
	private OutputStream out;

	/**
	 * Creates a {code DwcaWriter} for the specified data set collection,
	 * writing to the specified output stream.
	 * 
	 * @param dsc
	 * @param out
	 */
	public DwcaWriter(DataSetCollection dsc, OutputStream out)
	{
		this.dsc = dsc;
		this.out = out;
	}

	/**
	 * Writes a DarwinCore archive for the specified data set. The Elasticsearch
	 * query to be executed is specified in a file called "queryspec.json"
	 * residing in the
	 * {@link DwcaUtil#getDatasetDirectory(DataSetCollection, String) directory}
	 * created for the data set.
	 * 
	 * @param dataSet
	 * @throws InvalidQueryException
	 */
	public void processPredefinedQuery(String dataSet) throws InvalidQueryException
	{
		ZipOutputStream zos = new ZipOutputStream(out);
		IDataSetField[] fields = getFields(dsc);
		writeMetaXml(zos, fields);
		writeEmlXml(zos, dataSet);
		QuerySpec querySpec = getQuerySpec(dsc, dataSet);
		writeCsv(querySpec, fields, zos);
		close(zos);
	}

	/**
	 * Writes a DarwinCore archive containing the data retrieved using the
	 * specified {@link QuerySpec}.
	 * 
	 * @param querySpec
	 * @throws InvalidQueryException
	 */
	public void processDynamicQuery(QuerySpec querySpec) throws InvalidQueryException
	{
		ZipOutputStream zos = new ZipOutputStream(out);
		IDataSetField[] fields = getFields(dsc);
		writeMetaXml(zos, fields);
		writeEmlXml(zos, null);
		writeCsv(querySpec, fields, zos);
		close(zos);
	}

	private void writeMetaXml(ZipOutputStream zos, IDataSetField[] fields)
	{
		newZipEntry(zos, "meta.xml");
		MetaXmlGenerator metaXmlGenerator = getMetaXmlGenerator(dsc, fields);
		metaXmlGenerator.generateMetaXml(zos);
	}

	private void writeEmlXml(ZipOutputStream zos, String dataSet)
	{
		newZipEntry(zos, "eml.xml");
		File emlFile = getEmlFile(dsc, dataSet);
		try (InputStream is = new FileInputStream(emlFile)) {
			IOUtil.pipe(is, zos, 2048);
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}

	private void writeCsv(QuerySpec spec, IDataSetField[] fields, ZipOutputStream zos)
			throws InvalidQueryException
	{
		newZipEntry(zos, getCsvFileName(dsc));
		CsvPrinter csvPrinter = new CsvPrinter(fields, zos);
		SearchResponse response = executeQuery(spec);
		csvPrinter.printHeader();
		int processed = 0;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
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

	private static SearchResponse executeQuery(QuerySpec spec) throws InvalidQueryException
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

	private static void close(ZipOutputStream zos)
	{
		try {
			zos.closeEntry();
			zos.close();
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
	}
}
