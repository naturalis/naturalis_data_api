package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * This ConditionTranslator is called when Condition.operator is NOT_EQUALS and
 * Condition.value is null. By not having this situation handled by the
 * {@link EqualsConditionTranslator}, we keep our code and the generated
 * Elasticsearch query easier to read.
 * 
 * @author Ayco Holleman
 *
 */
class IsNotNullConditionTranslator extends ConditionTranslator {

	IsNotNullConditionTranslator(SearchCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		QueryBuilder query = existsQuery(path.toString());
		if (forSortField) {
			return query;
		}
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		query = boolQuery().mustNot(query);
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
	}
}
