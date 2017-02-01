package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.LinkedHashSet;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.utils.CollectionUtil;

class MatchesConditionTranslator extends ConditionTranslator {

	private static final String MY_MULTIFIELD = DEFAULT_MULTIFIELD.getName();

	MatchesConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		QueryBuilder query;
		if (condition.getFields().size() == 1) {
			Path path = condition.getFields().iterator().next();
			String field = path.append(MY_MULTIFIELD).toString();
			String value = condition.getValue().toString().toLowerCase();
			query = matchQuery(field, value);
		}
		else {
			String[] fields = new String[condition.getFields().size()];
			int i = 0;
			for (Path path : condition.getFields()) {
				fields[i++] = path.append(MY_MULTIFIELD).toString();
			}
			query = multiMatchQuery(condition.getValue(), fields);
		}
		if (forSortField) {
			return query;
		}
		Path path = condition.getFields().iterator().next();
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		if (condition.isFilter().booleanValue()) {
			query = constantScoreQuery(query);
		}
		else if (condition.getBoost() != null) {
			query.boost(condition.getBoost());
		}
		return query;
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		/*
		 * We assume that Elasticsearch will let you to do nested nulti-match
		 * queries, but only if all fields have the same nested path.
		 */
		LinkedHashSet<String> nestedPaths = new LinkedHashSet<>();
		for (Path path : condition.getFields()) {
			String np = getNestedPath(path, mappingInfo);
			if (np != null) {
				nestedPaths.add(np);
			}
		}
		if (nestedPaths.size() > 1) {
			String fmt = "Cannot execute multi-match query for fields on different nest levels: %s";
			String msg = String.format(fmt, CollectionUtil.implode(nestedPaths));
			throw new InvalidConditionException(msg);
		}
	}
}
