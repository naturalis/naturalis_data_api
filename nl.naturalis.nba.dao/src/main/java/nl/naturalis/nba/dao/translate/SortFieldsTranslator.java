package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.api.SortField.SORT_FIELD_SCORE;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;

class SortFieldsTranslator {

	private QuerySpec querySpec;
	private DocumentType<?> dt;

	SortFieldsTranslator(QuerySpec querySpec, DocumentType<?> documentType)
	{
		this.querySpec = querySpec;
		this.dt = documentType;
	}

	SortBuilder<?>[] translate() throws InvalidQueryException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
		List<SortField> sortFields = querySpec.getSortFields();
		SortBuilder<?>[] result = new SortBuilder[sortFields.size()];
		int i = 0;
		for (SortField sf : sortFields) {
			Path path = sf.getPath();
			if (path.equals(SORT_FIELD_SCORE)) {
				ScoreSortBuilder ssb = SortBuilders.scoreSort();
				if (!sf.isAscending()) {
					ssb.order(DESC);
				}
				result[i++] = ssb;
			}
			else {
				FieldSortBuilder fsb = SortBuilders.fieldSort(path.toString());
				fsb.order(sf.isAscending() ? ASC : DESC);
				fsb.sortMode(sf.isAscending() ? SortMode.MIN : SortMode.MAX);
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
				if (nestedPath != null) {
					fsb.setNestedPath(nestedPath);
					QueryBuilder query = translateConditions(path);
					if (query != null) {
						fsb.setNestedFilter(query);
					}
				}
				result[i++] = fsb;
			}
		}
		return result;
	}

	/*
	 * This method generates a "nested_filter" for a FieldSortBuilder when
	 * necessary. This is necessary if: (1) the field being sorted on is in, or
	 * descends from a nested object; (2) there are also query conditions on
	 * that very same field. If this is the case, those conditions must be
	 * copied from the "query" section of the search request to the "sort"
	 * section of the search request.
	 */
	private QueryBuilder translateConditions(Path sortField) throws InvalidConditionException
	{

		List<QueryCondition> conditions = pruneAll(querySpec.getConditions(), sortField);
		if (conditions == null) {
			return null;
		}
		if (conditions.size() == 1) {
			QueryCondition c = conditions.iterator().next();
			return getTranslator(c, dt).forSortField().translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		for (QueryCondition c : conditions) {
			if (querySpec.getLogicalOperator() == OR) {
				result.should(getTranslator(c, dt).forSortField().translate());
			}
			else {
				result.must(getTranslator(c, dt).forSortField().translate());
			}
		}
		return result;
	}

	/*
	 * Prune away any conditions that are not on the specified field.
	 */
	private static List<QueryCondition> pruneAll(List<QueryCondition> conditions, Path sortField)
	{
		if (conditions == null) {
			return null;
		}
		List<QueryCondition> copies = new ArrayList<>(conditions.size());
		for (QueryCondition c : conditions) {
			c = prune(c, sortField);
			if (c != null) {
				c.setAnd(pruneAll(c.getAnd(), sortField));
				c.setOr(pruneAll(c.getOr(), sortField));
				copies.add(c);
			}
		}
		return copies.size() == 0 ? null : copies;
	}

	/*
	 * Dark magic. Stay away. This method prunes away all of the specified
	 * condition's siblings that are not conditions on the sort field. If the
	 * specified condition is itself not a condition on the sort field, we
	 * attempt to replace it with a sibling that is. This may not always be
	 * possible, in which case we return null. This basically means: we give up
	 * and the documents may not be sorted correctly. We seek out a sibling that
	 * has itself zero siblings, because this allows us to copy the original
	 * condition's siblings to its replacement. This way we retain as many as
	 * possible conditions on the sort field while recursively descending into
	 * the original condition's descendants. This is all super-hairy, but
	 * Elasticsearch is itself very vague about how all this can possibly work
	 * with complex queries with deeply nested conditions on both the sort field
	 * and other fields.
	 */
	private static QueryCondition prune(QueryCondition condition, Path sortField)
	{
		if (condition.getField().equals(sortField)) {
			return condition;
		}
		QueryCondition alternative = null;
		if (condition.getAnd() != null) {
			List<QueryCondition> and = new ArrayList<>(condition.getAnd().size());
			for (QueryCondition c : condition.getAnd()) {
				if (c.getField().equals(sortField)) {
					if (alternative == null && c.getAnd() == null && c.getOr() == null) {
						alternative = c;
					}
					else {
						and.add(c);
					}
				}
			}
			if (alternative != null) {
				// Create copy b/c we are going to change it
				alternative = new QueryCondition(alternative);
				if (and.size() != 0) {
					alternative.setAnd(and);
				}
				if (condition.getOr() != null) {
					List<QueryCondition> or = new ArrayList<>(condition.getOr().size());
					for (QueryCondition c : condition.getOr()) {
						if (c.getField().equals(sortField)) {
							or.add(c);
						}
					}
					if (or.size() != 0) {
						alternative.setOr(or);
					}
				}
			}
		}
		else if (condition.getOr() != null) {
			List<QueryCondition> or = new ArrayList<>(condition.getOr().size());
			for (QueryCondition c : condition.getOr()) {
				if (c.getField().equals(sortField)) {
					if (alternative == null && c.getAnd() == null && c.getOr() == null) {
						alternative = c;
					}
					else {
						or.add(c);
					}
				}
			}
			if (alternative != null && or.size() != 0) {
				alternative = new QueryCondition(alternative);
				alternative.setOr(or);
			}
		}
		return alternative;
	}

	private static InvalidQueryException invalidSortField(Path field)
	{
		String fmt = "Invalid sort field: \"%s\"";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}

}
