package nl.naturalis.nba.dao.es.util;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import nl.naturalis.nba.api.query.Criterion;
import nl.naturalis.nba.api.query.InvalidCriterionException;

public class CriterionResolver {

	private final Criterion criterion;

	public CriterionResolver(Criterion criterion)
	{
		this.criterion = criterion;
	}

	public BoolQueryBuilder resolve() throws InvalidCriterionException
	{
		BoolQueryBuilder query = boolQuery();
		resolve(query);
		return query;
	}

	public void resolve(BoolQueryBuilder query) throws InvalidCriterionException
	{
		List<Criterion> siblings = null;
		boolean isOr = false;
		if (criterion.getOr() != null) {
			if (criterion.getAnd() != null) {
				String msg = "You cannot specify both AND and OR sibling criteria";
				throw new InvalidCriterionException(msg);
			}
			siblings = criterion.getOr();
			isOr = true;
		}
		else if (criterion.getAnd() != null) {
			siblings = criterion.getAnd();
		}
		if (siblings == null) {
			resolveThis(query);
		}
		else if (isOr) {
			BoolQueryBuilder nested = QueryBuilders.boolQuery();
		}
	}

	private void resolveThis(BoolQueryBuilder query) throws InvalidCriterionException
	{
		switch (criterion.getOperator()) {
			case EQUALS:
				TermQueryBuilder termQuery = termQuery(criterion.getField(), criterion.getValue());
				query.must(termQuery);
				break;
			case GT:
				break;
			case GTE:
				break;
			case LT:
				break;
			case LTE:
				break;
			case NOT_EQUALS:
				break;
			default:
				break;

		}
	}

}
