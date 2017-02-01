package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.dao.translate.query.ConditionTranslatorFactory.getTranslator;
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
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;

class SortFieldsTranslator {

	private static final String ERR_NESTED_FILTER_ERROR = "When sorting on field %1$s "
			+ "you cannot nest a query condition on field %1$s within a query condition "
			+ "on field %1$s";

	private SearchSpec querySpec;
	private DocumentType<?> dt;

	SortFieldsTranslator(SearchSpec querySpec, DocumentType<?> documentType)
	{
		this.querySpec = querySpec;
		this.dt = documentType;
	}

	FieldSortBuilder[] translate() throws InvalidQueryException
	{
		List<SortField> sortFields = querySpec.getSortFields();
		FieldSortBuilder[] result = new FieldSortBuilder[sortFields.size()];
		int i = 0;
		for (SortField sf : sortFields) {
			String path = sf.getPath();
			String nestedPath;
			try {
				MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
				ESField f = mappingInfo.getField(path);
				if (!(f instanceof SimpleField)) {
					throw invalidSortField(path);
				}
				nestedPath = MappingInfo.getNestedPath(f);
			}
			catch (NoSuchFieldException e) {
				throw invalidSortField(sf.getPath());
			}
			FieldSortBuilder sb = SortBuilders.fieldSort(path);
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

	private QueryBuilder translateConditions(String sortField) throws InvalidConditionException
	{
		return null;
//		List<SearchCondition> conditions = querySpec.getConditions();
//		if (conditions == null || conditions.size() == 0) {
//			return null;
//		}
//		if (conditions.size() == 1) {
//			SearchCondition c = conditions.iterator().next();
//			if (c.getField().equals(sortField)) {
//				checkCondition(c, sortField);
//				return getTranslator(c, dt).forSortField().translate();
//			}
//			return null;
//		}
//		BoolQueryBuilder result = QueryBuilders.boolQuery();
//		boolean hasConditionWithSortField = false;
//		for (SearchCondition c : conditions) {
//			if (c.getField().equals(sortField)) {
//				hasConditionWithSortField = true;
//				checkCondition(c, sortField);
//				if (querySpec.getLogicalOperator() == OR) {
//					result.should(getTranslator(c, dt).forSortField().translate());
//				}
//				else {
//					result.must(getTranslator(c, dt).forSortField().translate());
//				}
//			}
//		}
//		return hasConditionWithSortField ? result : null;
	}

	private static void checkCondition(SearchCondition c, String path)
			throws InvalidConditionException
	{
//		if (c.getAnd() != null) {
//			for (SearchCondition condition : c.getAnd()) {
//				if (!condition.getField().equals(path)) {
//					String msg = String.format(ERR_NESTED_FILTER_ERROR, path, condition.getField());
//					throw new InvalidConditionException(msg);
//				}
//				checkCondition(condition, path);
//			}
//		}
//		if (c.getOr() != null) {
//			for (SearchCondition condition : c.getOr()) {
//				if (!condition.getField().equals(path)) {
//					String msg = String.format(ERR_NESTED_FILTER_ERROR, path, condition.getField());
//					throw new InvalidConditionException(msg);
//				}
//				checkCondition(condition, path);
//			}
//		}
	}

	private static InvalidQueryException invalidSortField(String field)
	{
		String fmt = "Invalid sort field: \"%s\"";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}

}
