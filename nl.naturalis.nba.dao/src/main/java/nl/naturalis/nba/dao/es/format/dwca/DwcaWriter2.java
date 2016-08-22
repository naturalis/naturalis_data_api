package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaUtil.getCsvFileName;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.csv.CsvPrinter;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

/**
 * Manages the assemblage and creation of DarwinCore archives.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaWriter2 {

	private static Logger logger = getLogger(DwcaWriter2.class);
	private static TimeValue TIME_OUT = new TimeValue(5000);

	private DataSet ds;
	private ZipOutputStream zos;

	/**
	 * Creates a {code DwcaWriter} for the specified data set collection,
	 * writing to the specified output stream.
	 * 
	 * @param dsc
	 * @param out
	 */
	public DwcaWriter2(DataSet ds, ZipOutputStream zos)
	{
		this.ds = ds;
		this.zos = zos;
	}

	private void writeCsv(QuerySpec spec, IDataSetField[] fields, ZipOutputStream zos)
			throws InvalidQueryException
	{
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
