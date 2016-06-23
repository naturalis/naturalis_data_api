package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingInfo;


public class BetweenConditionTranslatorTest {
	
	private static MappingInfo inspector;
	
	@BeforeClass
	public static void init() {
		Mapping m = new MappingFactory().getMapping(LikeTestObject.class);
		inspector = new MappingInfo(m);
	}


	/*
	 * Checks that translation fails if the Condition.value is not String.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", LIKE, new Object());
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, inspector);
		ct.translate();
	}
}
