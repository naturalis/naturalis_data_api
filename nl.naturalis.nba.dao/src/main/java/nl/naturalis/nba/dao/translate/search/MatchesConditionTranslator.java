package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

import java.util.LinkedHashSet;

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
		if (condition.getFields().size() == 1) {
			Path path = condition.getFields().iterator().next();
			String field = path.append(MY_MULTIFIELD).toString();
			String value = condition.getValue().toString();
			return matchQuery(field, value);
		}
		String[] fields = new String[condition.getFields().size()];
		int i = 0;
		for (Path path : condition.getFields()) {
			fields[i++] = path.append(MY_MULTIFIELD).toString();
		}
		return multiMatchQuery(condition.getValue(), fields);
	}

	@Override
	void preprocess() throws InvalidConditionException
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
