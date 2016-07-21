package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.domainobject.util.debug.BeanPrinter;
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
import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

public class SpecimenDwcaDao {

	private static final Logger logger;

	private String[][] headers;
	private String[][] fields;

	static {
		logger = DAORegistry.getInstance().getLogger(SpecimenDwcaDao.class);
	}

	public void querySpecimens(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator qst = new QuerySpecTranslator(spec, SPECIMEN);
		SearchRequestBuilder request = qst.translate();
		request.addSort(DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(new TimeValue(250));
		request.setSize(1000);
		SearchResponse response = request.execute().actionGet();
		loadFieldsAndHeaders();
		for (int i = 0; i < headers.length; i++) {
			if (i != 0) {
				System.out.print(',');
			}
			System.out.print(headers[i][0]);
		}
		System.out.println();
//		BeanPrinter.out(headers);
//		BeanPrinter.out(fields);
//		if(true)
//			return;
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> data = hit.getSource();
				Object[] values = JsonUtil.readFields(data, fields);
				int j=0;
				for (int i = 0; i < headers.length; i++) {
					if (i != 0) {
						System.out.print(',');
					}
					if (headers[i][1] != null) {
						System.out.print(escapeCsv(headers[i][1]));
					}
					else {
						Object val = values[j++];
						if (val != null && val != JsonUtil.MISSING_VALUE) {
							if (val.getClass() == String.class) {
								String s = escapeCsv((String) val);
								System.out.print(s);
							}
							else {
								System.out.print(String.valueOf(val));
							}
						}
					}
				}
				System.out.println();
			}
			String scrollId = response.getScrollId();
			SearchScrollRequestBuilder ssrb = client().prepareSearchScroll(scrollId);
			response = ssrb.setScroll(new TimeValue(1000)).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
	}

	private void loadFieldsAndHeaders()
	{
		File confDir = DAORegistry.getInstance().getConfigurationDirectory();
		File propFile = FileUtil.newFile(confDir, "dwca/specimen/generic/fields.properties");
		ArrayList<String[]> hdrs = new ArrayList<>(32);
		ArrayList<String[]> flds = new ArrayList<>(32);
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
					flds.add(field.split("\\."));
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
		this.fields = flds.toArray(new String[flds.size()][]);
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
