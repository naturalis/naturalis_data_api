package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;

class SortFieldsTranslator {

	private static final String ERR_01 = "Sorting on %1$s not allowed in combination "
			+ "with condition on %1$s and a sibling condition on %2$s";

	private QuerySpec querySpec;
	private DocumentType<?> dt;

	SortFieldsTranslator(QuerySpec querySpec, DocumentType<?> documentType)
	{
		this.querySpec = querySpec;
		this.dt = documentType;
	}

	FieldSortBuilder[] translate() throws InvalidQueryException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
		List<SortField> sortFields = querySpec.getSortFields();
		FieldSortBuilder[] result = new FieldSortBuilder[sortFields.size()];
		int i = 0;
		for (SortField sf : sortFields) {
			Path path = sf.getPath();
			String nestedPath;
			try {
				ESField f = mappingInfo.getField(path);
				if (!(f instanceof SimpleField)) {
					throw invalidSortField(path);
				}
				nestedPath = MappingInfo.getNestedPath(f);
			}
			catch (NoSuchFieldException e) {
				throw invalidSortField(sf.getPath());
			}
			FieldSortBuilder sb = SortBuilders.fieldSort(path.toString());
			sb.order(sf.isAscending() ? ASC : DESC);
			sb.sortMode(sf.isAscending() ? SortMode.MIN : SortMode.MAX);
			if (nestedPath != null) {
				sb.setNestedPath(nestedPath);
				QueryBuilder query = translateConditions(path);
				if (query != null) {
					sb.setNestedFilter(query);
				}
			}
			result[i++] = sb;
		}
		return result;
	}

	/*
	 * This method generates a "nested_filter" for a FieldSortBuilder when
	 * necessary. This is necessary if: (1) the field being sorted on is in, or
	 * descends from a nested object; (2) there are also conditions on that very
	 * same field. If this is the case, those conditions must be copied from the
	 * "query" section of the search request to the "sort" section of the search
	 * request.
	 */
	private QueryBuilder translateConditions(Path sortField) throws InvalidConditionException
	{
		List<QueryCondition> conditions = querySpec.getConditions();
		if (conditions == null || conditions.size() == 0) {
			return null;
		}
		if (conditions.size() == 1) {
			QueryCondition c = conditions.iterator().next();
			if (c.getField().equals(sortField)) {
				checkCondition(c, sortField);
				return getTranslator(c, dt).forSortField().translate();
			}
			return null;
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		/*
		 * Do we have any conditions on the same field that we want to sort on?
		 */
		boolean hasConditionWithSortField = false;
		for (QueryCondition c : conditions) {
			if (c.getField().equals(sortField)) {
				hasConditionWithSortField = true;
				checkCondition(c, sortField);
				if (querySpec.getLogicalOperator() == OR) {
					result.should(getTranslator(c, dt).forSortField().translate());
				}
				else {
					result.must(getTranslator(c, dt).forSortField().translate());
				}
			}
		}
		return hasConditionWithSortField ? result : null;
	}

	/*
	 * This method ensures that if we have a condition on the same field as the
	 * sort field, then the condition's siblings and descendants must all be
	 * conditions on that very same field. NB this restriction only applies when
	 * generating a FieldSortBuilder for a field within a nested object.
	 */
	private static void checkCondition(QueryCondition condition, Path sortField)
			throws InvalidConditionException
	{
		if (condition.getAnd() != null) {
			for (QueryCondition c : condition.getAnd()) {
				if (!c.getField().equals(sortField)) {
					throw new InvalidConditionException(c, ERR_01, sortField, c.getField());
				}
				checkCondition(c, sortField);
			}
		}
		if (condition.getOr() != null) {
			for (QueryCondition c : condition.getOr()) {
				if (!c.getField().equals(sortField)) {
					throw new InvalidConditionException(c, ERR_01, sortField, c.getField());
				}
				checkCondition(c, sortField);
			}
		}
	}

	private static InvalidQueryException invalidSortField(Path field)
	{
		String fmt = "Invalid sort field: \"%s\"";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}

}
