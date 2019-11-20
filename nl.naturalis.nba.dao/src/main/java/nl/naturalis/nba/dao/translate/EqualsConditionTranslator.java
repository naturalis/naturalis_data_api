package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsConditionTranslator extends ConditionTranslator {
  
  private static final Logger logger = getLogger(EqualsConditionTranslator.class);

	EqualsConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		logger.info(">>>>>>>>>>\n{}", termQuery(path.toString(), condition.getValue()));
		return termQuery(path.toString(), condition.getValue());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
