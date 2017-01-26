package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.ScientificNameSummary;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;

class NameImportUtil {

	static long longHashCode(String s)
	{
		long h = 0;
		for (int i = 0; i < s.length(); i++) {
			h = 31 * h + s.charAt(i);
		}
		return h;
	}

	static List<ScientificNameSummary> loadNames(Collection<String> names)
	{
		DocumentType<ScientificNameSummary> dt = SCIENTIFIC_NAME_SUMMARY;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery(dt.getName());
		query.addIds(names.toArray(new String[names.size()]));
		request.setQuery(query);
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0)
			return Collections.emptyList();
		List<ScientificNameSummary> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameSummary sns = om.convertValue(hit.getSource(), dt.getJavaType());
			result.add(sns);
		}
		return result;
	}

	static List<ScientificNameSummary> loadNames2(Collection<String> names)
	{
		DocumentType<ScientificNameSummary> dt = SCIENTIFIC_NAME_SUMMARY;
		SearchRequestBuilder request = newSearchRequest(dt);
		String field = "fullScientificName";
		TermsQueryBuilder query = QueryBuilders.termsQuery(field, names);
		request.setQuery(query);
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<ScientificNameSummary> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameSummary sns = om.convertValue(hit.getSource(), dt.getJavaType());
			result.add(sns);
		}
		return result;
	}

	static List<Taxon> loadTaxa(Collection<String> names)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = termsQuery("acceptedName.fullScientificName", names);
		request.setQuery(query);
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0)
			return Collections.emptyList();
		List<Taxon> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			Taxon taxon = om.convertValue(hit.getSource(), dt.getJavaType());
			taxon.setId(hit.getId());
			result.add(taxon);
		}
		return result;
	}

	private NameImportUtil()
	{
	}

}
