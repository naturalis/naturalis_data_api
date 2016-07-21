package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static org.elasticsearch.search.sort.SortParseElement.DOC_FIELD_NAME;

import java.util.Map;
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
import nl.naturalis.nba.dao.es.query.QuerySpecTranslator;

public class SpecimenDwcaDao {

	private static final Logger logger;
	
	private String[] headers;
	private String[][] fields;

	static {
		logger = DAORegistry.getInstance().getLogger(SpecimenDwcaDao.class);
	}

	ZipOutputStream querySpecimens(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator qst = new QuerySpecTranslator(spec, SPECIMEN);
		SearchRequestBuilder request = qst.translate();
		request.addSort(DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(new TimeValue(1000));
		request.setSize(100);
		SearchResponse response = request.execute().actionGet();
		while (true) {
			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> data = hit.getSource();
			}
			String scrollId = response.getScrollId();
			SearchScrollRequestBuilder ssrb = client().prepareSearchScroll(scrollId);
			response = ssrb.setScroll(new TimeValue(1000)).execute().actionGet();
			if (response.getHits().getHits().length == 0) {
				break;
			}
		}
		return null;
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}
}
