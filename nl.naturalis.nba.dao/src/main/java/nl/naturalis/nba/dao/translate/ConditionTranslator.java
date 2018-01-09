package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_CONTAINS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_STARTS_WITH;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_STARTS_WITH_IC;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.isFalseCondition;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.isTrueCondition;
import static nl.naturalis.nba.utils.CollectionUtil.hasElements;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates a {@link QueryCondition} into an Elasticsearch
 * {@link QueryBuilder} instance. You cannot instantiate a
 * {@code ConditionTranslator} directly. Instances are obtained using a
 * {@link ConditionTranslatorFactory}.
 * 
 * @author Ayco Holleman
 *
 */
abstract class ConditionTranslator {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(ConditionTranslator.class);

	/*
	 * Negating operators are operators that are translated just like their
	 * opposite (e.g. NOT_BETWEEN <-> BETWEEN), but then wrapped into a
	 * BoolQuery.mustNot() query. Note that NOT_EQUALS and NOT_EQUALS_IC are not
	 * included here. For them separate ConditionTranslator subclasses have been
	 * created. This is because they require special code for NULL handling, and
	 * also because not having them handled separately may result in valid but
	 * awkward Elasticsearch queries (mustNot within mustNot within mustNot
	 * queries).
	 */
	private static final EnumSet<ComparisonOperator> negatingOperators;

	static {
		negatingOperators = EnumSet.of(NOT_BETWEEN, NOT_CONTAINS, NOT_IN, NOT_MATCHES,
				NOT_STARTS_WITH, NOT_STARTS_WITH_IC);
	}

	QueryCondition condition;
	MappingInfo<?> mappingInfo;

	/*
	 * Whether or not to translate this condition for a "nested_filter" block
	 * within the "sort" section of a search request. Ordinarily conditions are
	 * translated for the "query" section of a search request. However, when
	 * sorting on a field within a nested object, you must copy all conditions
	 * on that same field to the sort clause.
	 */
	private boolean forSortField = false;

	ConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	ConditionTranslator forSortField()
	{
		this.forSortField = true;
		return this;
	}

	/**
	 * Converts the {@link QueryCondition} passed in through the
	 * {@link #ConditionTranslator(QueryCondition) constructor} to an
	 * Elasticsearch {@link QueryBuilder} instance.
	 * 
	 * @return
	 * @throws InvalidConditionException
	 */
	public QueryBuilder translate() throws InvalidConditionException
	{
		preprocess();
		QueryBuilder query = translateCondition();
		if (forSortField)
			query = postprocessForSortField(query);
		else
			query = postprocess(query);
		
		if (hasElements(condition.getAnd())) {
			query = generateAndSiblings(query);
			if (hasElements(condition.getOr())) {
				query = generateOrSiblings(query);
			}
		}
		else if (hasElements(condition.getOr())) {
			query = generateOrSiblings(query);
		}
//		else {
//		  String nestedPath = getNestedPath(condition.getField(), mappingInfo);
//		  if (nestedPath != null ) {
//		    query = nestedQuery(nestedPath, query, ScoreMode.Avg);
//		  }
//		}

		if (condition.isNegated()) {
			query = not(query);
		}
		query.boost(condition.getBoost());
		return query;
	}

	/*
	 * Implement any up-front/fail-fast checks you can think of. Subclasses
	 * should throw an InvalidConditionException if the condition is deemed
	 * invalid. Subclasses can also use this method to preprocess the condition,
	 * e.g. cast or convert the condition's value. This method is called just
	 * before translateCondition().
	 */
	abstract void preprocess() throws InvalidConditionException;

	/*
	 * Convert the Condition to a QueryBuilder as appropriate for the operator
	 * that the subclass is dealing with.
	 */
	abstract QueryBuilder translateCondition() throws InvalidConditionException;

	/*
	 * Applies processing steps to be taken after the condition has been turned
	 * into an Elasticsearch query. These steps are ordinarily
	 * operator-independent, and hence are implemented here (in the base class).
	 * However, if the need arises subclasses can override this method. This
	 * method is called right after translateCondition().
	 */
	QueryBuilder postprocess(QueryBuilder query)
	{
		if (!isTrueCondition(condition) && !isFalseCondition(condition)) {
			if (hasNegativeOperator()) {
				query = not(query);
			}
		}
		if (condition.isConstantScore()) {
			query = constantScoreQuery(query);
		}
		return query;
	}

	QueryBuilder postprocessForSortField(QueryBuilder query)
	{
		if (hasNegativeOperator()) {
			query = not(query);
		}
		return query;
	}

  private BoolQueryBuilder generateAndSiblings(QueryBuilder firstSibling)
      throws InvalidConditionException {

    // Collect all conditions that use a nested path
    HashMap<String, ArrayList<QueryCondition>> mapNestedPaths = new HashMap<>();

    // First condition
    String nestedPathFirst = getNestedPath(condition.getField(), mappingInfo);
    mapNestedPaths.putIfAbsent(nestedPathFirst, new ArrayList<QueryCondition>());
    mapNestedPaths.get(nestedPathFirst).add(condition);

    // Next condition(s)
    for (QueryCondition c : condition.getAnd()) {
      if (hasElements(c.getAnd()) || hasElements(c.getOr())) {
        mapNestedPaths.putIfAbsent(null, new ArrayList<QueryCondition>());
        mapNestedPaths.get(null).add(c);

      } else {
        String nestedPathNext = getNestedPath(c.getField(), mappingInfo);
        mapNestedPaths.putIfAbsent(nestedPathNext, new ArrayList<QueryCondition>());
        mapNestedPaths.get(nestedPathNext).add(c);
      }
    }

    // Build the query from the conditions stored in the map
    BoolQueryBuilder bq = boolQuery();

    for (Entry<String, ArrayList<QueryCondition>> entry : mapNestedPaths.entrySet()) {
      String nestedPath = entry.getKey();
      if (nestedPath == null) {
        for (QueryCondition qc : entry.getValue()) {
          if (qc == condition) {
            bq.must(firstSibling);
          } 
          else {
            bq.must(getTranslator(qc, mappingInfo).translate());
          }
        }
      }
      else {
        BoolQueryBuilder innerBool = boolQuery();
        for (QueryCondition qc : entry.getValue()) {
          if (qc == condition) {
            innerBool.must(firstSibling);
          }
          else {
            innerBool.must(getTranslator(qc, mappingInfo).translate());
          }
        }
        NestedQueryBuilder nestedQuery = nestedQuery(nestedPath, innerBool, ScoreMode.Avg);
        bq.must(nestedQuery);        
      }
    }
    return bq;
  }

	private BoolQueryBuilder generateOrSiblings(QueryBuilder firstSibling)
			throws InvalidConditionException
	{
    // Collect all conditions that use a nested path
    HashMap<String, ArrayList<QueryCondition>> mapNestedPaths = new HashMap<>();

    // First condition
    String nestedPathFirst = getNestedPath(condition.getField(), mappingInfo);
    mapNestedPaths.putIfAbsent(nestedPathFirst, new ArrayList<QueryCondition>());
    mapNestedPaths.get(nestedPathFirst).add(condition);

    // Next condition(s)
    for (QueryCondition c : condition.getOr()) {
      if (hasElements(c.getAnd()) || hasElements(c.getOr())) {
        mapNestedPaths.putIfAbsent(null, new ArrayList<QueryCondition>());
        mapNestedPaths.get(null).add(c);

      } else {
        String nestedPathNext = getNestedPath(c.getField(), mappingInfo);
        mapNestedPaths.putIfAbsent(nestedPathNext, new ArrayList<QueryCondition>());
        mapNestedPaths.get(nestedPathNext).add(c);
      }
    }

    // Build the query from the conditions stored in the map
    BoolQueryBuilder bq = boolQuery();

    for (Entry<String, ArrayList<QueryCondition>> entry : mapNestedPaths.entrySet()) {
      String nestedPath = entry.getKey();
      if (nestedPath == null) {
        for (QueryCondition qc : entry.getValue()) {
          if (qc == condition) {
            bq.should(firstSibling);
          } 
          else {
            bq.should(getTranslator(qc, mappingInfo).translate());
          }
        }
      }
      else {
        for (QueryCondition qc : entry.getValue()) {
          if (qc == condition) {
            if (hasElements(condition.getAnd())) {
              bq.should(firstSibling);              
            }
            else {
              NestedQueryBuilder nestedQuery = nestedQuery(nestedPath, firstSibling, ScoreMode.Avg);
              bq.should(nestedQuery);
            }
          }
          else {
            BoolQueryBuilder innerBool = boolQuery();
            innerBool.should(getTranslator(qc, mappingInfo).translate());
            NestedQueryBuilder nestedQuery = nestedQuery(nestedPath, innerBool, ScoreMode.Avg);
            bq.should(nestedQuery);        
          }
        }
      }
    }
    return bq;
	}

	/*
	 * Whether or not the condition translated by this translator instance uses
	 * a negating operator.
	 */
	boolean hasNegativeOperator()
	{
		return negatingOperators.contains(condition.getOperator());
	}

	private static QueryBuilder not(QueryBuilder query)
	{
		return boolQuery().mustNot(query);
	}

	private ConditionTranslator getTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
			throws InvalidConditionException
	{
		ConditionTranslator ct = ConditionTranslatorFactory.getTranslator(condition, mappingInfo);
		ct.forSortField = forSortField;
		return ct;
	}

}
