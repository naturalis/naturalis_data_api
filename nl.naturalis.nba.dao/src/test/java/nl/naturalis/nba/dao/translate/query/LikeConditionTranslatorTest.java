package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.dao.translate.query.ConditionTranslatorFactory.getTranslator;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.IllegalOperatorException;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.translate.query.ConditionTranslator;

@SuppressWarnings("static-method")
public class LikeConditionTranslatorTest {

	private static MappingInfo<LikeTestObject> inspector;

	@BeforeClass
	public static void init()
	{
		Mapping<LikeTestObject> m = MappingFactory.getMapping(LikeTestObject.class);
		inspector = new MappingInfo<>(m);
	}

	/*
	 * Checks that translation fails if the Condition.value is not String.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, new Object());
		ConditionTranslator ct = getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that translation fails if field being queried is not a string
	 * field.
	 */
	@Test(expected = IllegalOperatorException.class)
	public void testTranslate_02() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("age", LIKE, "foo");
		ConditionTranslator ct = getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at
	 * least 3 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_03() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, "12");
		ConditionTranslator ct = getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at most
	 * 10 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_04() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, "12345678901234567890");
		ConditionTranslator ct = getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the field is not analyzed using the LIKE
	 * enabling analyzer.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_05() throws InvalidConditionException
	{
		// address field has no @Analyzers annotation
		QueryCondition c = new QueryCondition("address", LIKE, "foo");
		ConditionTranslator ct = getTranslator(c, inspector);
		ct.translate();
	}

}
