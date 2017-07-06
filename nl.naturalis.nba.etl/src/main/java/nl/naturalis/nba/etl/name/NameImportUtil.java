package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.ScientificNameGroup_old;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class NameImportUtil {

	private static final Logger logger = getLogger(NameImportUtil.class);

	static List<ScientificNameGroup_old> loadNameGroupsById(Collection<String> names)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Loading ScientificNameGroup documents for {} names", names.size());
		}
		DocumentType<ScientificNameGroup_old> dt = SCIENTIFIC_NAME_GROUP;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery(dt.getName());
		query.addIds(names.toArray(new String[names.size()]));
		request.setQuery(query);
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<ScientificNameGroup_old> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameGroup_old sns = om.convertValue(hit.getSource(), dt.getJavaType());
			sns.setId(hit.getId());
			result.add(sns);
		}
		return result;
	}

	static List<ScientificNameGroup_old> loadNameGroupsByName(Collection<String> names)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Loading ScientificNameGroup documents for {} names", names.size());
		}
		DocumentType<ScientificNameGroup_old> dt = SCIENTIFIC_NAME_GROUP;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = QueryBuilders.termsQuery("name", names);
		request.setQuery(QueryBuilders.constantScoreQuery(query));
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
		List<ScientificNameGroup_old> result = new ArrayList<>(hits.length);
		ObjectMapper om = dt.getObjectMapper();
		for (SearchHit hit : hits) {
			ScientificNameGroup_old sng = om.convertValue(hit.getSource(), dt.getJavaType());
			sng.setId(hit.getId());
			result.add(sng);
		}
		return result;
	}

	static List<Taxon> loadTaxa(Collection<String> names)
	{
		DocumentType<Taxon> dt = TAXON;
		SearchRequestBuilder request = ESUtil.newSearchRequest(dt);
		TermsQueryBuilder query = termsQuery("acceptedName.fullScientificName", names);
		request.setQuery(query);
		request.setSize(names.size());
		SearchResponse response = ESUtil.executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0) {
			return Collections.emptyList();
		}
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
