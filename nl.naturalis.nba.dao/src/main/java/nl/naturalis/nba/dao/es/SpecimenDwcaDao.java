package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
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
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.csv.CsvColumnConfigurator;
import nl.naturalis.nba.dao.es.csv.CsvPrinter;
import nl.naturalis.nba.dao.es.csv.IColumn;
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

public class SpecimenDwcaDao {

	private static final TimeValue TIME_OUT = new TimeValue(200);

	private static final Logger logger;

	private String[][] headers;
	private String[][] paths;
	@SuppressWarnings("unused")
	private String[] fields;

	static {
		logger = DAORegistry.getInstance().getLogger(SpecimenDwcaDao.class);
	}

	public void querySpecimens(QuerySpec spec, OutputStream os) throws InvalidQueryException
	{
		ZipOutputStream zos = new ZipOutputStream(os);
		ZipEntry occurenceTxt = new ZipEntry("occurence.txt");
		try {
			zos.putNextEntry(occurenceTxt);
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
		PrintStream out = new PrintStream(zos);
		loadHeadersAndPaths();
		SearchResponse response = executeQuery(spec);
		for (int i = 0; i < headers.length; i++) {
			if (i != 0) {
				out.print(',');
			}
			out.print(headers[i][0]);
		}
		out.println();
		int processed = 0;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
					out.flush();
				}
				Map<String, Object> data = hit.getSource();
				printCsvRecord(data, out);
			}
			String scrollId = response.getScrollId();
			SearchScrollRequestBuilder ssrb = client().prepareSearchScroll(scrollId);
			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
	}

	@SuppressWarnings("static-method")
	public void queryDynamic(QuerySpec spec, OutputStream os) throws InvalidQueryException
	{
		ZipOutputStream zos = new ZipOutputStream(os);
		ZipEntry occurenceTxt = new ZipEntry("occurence.txt");
		try {
			zos.putNextEntry(occurenceTxt);
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
		CsvPrinter csvPrinter = createCsvPrinter(zos, "generic");
		SearchResponse response = executeQuery(spec);
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
			SearchScrollRequestBuilder ssrb = client().prepareSearchScroll(scrollId);
			response = ssrb.setScroll(TIME_OUT).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
	}

	public static CsvPrinter createCsvPrinter(ZipOutputStream zos, String collection)
	{
		String relativePath = "dwca/specimen/" + collection;
		File confDir = DAORegistry.getInstance().getConfigurationDirectory();
		File dwcaConfDir = FileUtil.newFile(confDir, relativePath);
		CsvColumnConfigurator ccc = CsvColumnConfigurator.getInstance();
		IColumn[] columns = ccc.getColumns(dwcaConfDir);
		CsvPrinter csvPrinter = new CsvPrinter(columns, zos);
		return csvPrinter;
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

	public void printCsvRecord(Map<String, Object> data, PrintStream out)
	{
		Object[] values = JsonUtil.readFields(data, paths);
		int j = 0;
		for (int i = 0; i < headers.length; i++) {
			if (i != 0) {
				out.print(',');
			}
			if (headers[i][1] != null) {
				out.print(escapeCsv(headers[i][1]));
			}
			else {
				Object val = values[j++];
				if (val != null && val != JsonUtil.MISSING_VALUE) {
					if (val.getClass() == String.class) {
						out.print(escapeCsv((String) val));
					}
					else {
						out.print(String.valueOf(val));
					}
				}
			}
		}
		out.println();
	}

	private void loadHeadersAndPaths()
	{
		File confDir = DAORegistry.getInstance().getConfigurationDirectory();
		File propFile = FileUtil.newFile(confDir, "dwca/specimen/generic/fields.config");
		ArrayList<String[]> hdrs = new ArrayList<>(32);
		ArrayList<String[]> pths = new ArrayList<>(32);
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(propFile))) {
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw missingDelimiter(propFile, lnr);
				}
				String[] header = new String[2];
				header[0] = chunks[0].trim();
				hdrs.add(header);
				String field = chunks[1].trim();
				if (field.charAt(0) == '*') {
					header[1] = field.substring(1);
				}
				else {
					pths.add(field.split("\\."));
				}
			}
		}
		catch (FileNotFoundException e) {
			String msg = "Missing configuration file: " + propFile.getAbsolutePath();
			throw new DwcaCreationException(msg);
		}
		catch (IOException e) {
			throw new DwcaCreationException(e);
		}
		this.headers = hdrs.toArray(new String[hdrs.size()][2]);
		this.paths = pths.toArray(new String[pths.size()][]);
		// this.fields = getFields(paths);
	}

	@SuppressWarnings("unused")
	private static String[] getFields(String[][] paths)
	{
		ArrayList<String> flds = new ArrayList<>(paths.length);
		for (String[] path : paths) {
			flds.add(getField(path));
		}
		return flds.toArray(new String[flds.size()]);
	}

	private static String getField(String[] path)
	{
		StringBuffer sb = new StringBuffer(32);
		for (int i = 0; i < path.length; i++) {
			try {
				Integer.parseInt(path[i]);
			}
			catch (NumberFormatException e) {
				if (i != 0)
					sb.append('.');
				sb.append(path[i]);
			}
		}
		return sb.toString();
	}

	private static DwcaCreationException missingDelimiter(File propFile, LineNumberReader lnr)
	{
		int lineNo = lnr.getLineNumber() + 1;
		String path = propFile.getAbsolutePath();
		String fmt = "Missing delimiter (=) in line %s of %s";
		String msg = String.format(fmt, lineNo, path);
		return new DwcaCreationException(msg);
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}

}
