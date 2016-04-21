package nl.naturalis.nba.dao.es.util;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;

import nl.naturalis.nba.api.query.Criterion;
import nl.naturalis.nba.api.query.InvalidCriterionException;

public class CriterionResolver {

	private final Criterion criterion;

	public CriterionResolver(Criterion criterion)
	{
		this.criterion = criterion;
	}

	public BoolQueryBuilder resolve(BoolQueryBuilder query) throws InvalidCriterionException
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
		else if(criterion.getAnd()!= null) {
			siblings = criterion.getAnd();
		}
		if(siblings == null) {
			
		}
		return null;
	}

	private BoolQueryBuilder resolveThis(BoolQueryBuilder query)
	{
		switch (criterion.getOperator()) {
			case EQUALS:

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
		return null;
	}

}
