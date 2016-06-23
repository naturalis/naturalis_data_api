package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class LikeConditionTranslatorTest {

	private static MappingInfo inspector;

	@BeforeClass
	public static void init()
	{
		Mapping m = new MappingFactory().getMapping(LikeTestObject.class);
		inspector = new MappingInfo(m);
	}

	/*
	 * Checks that translation fails if the Condition.value is not String.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition c = new Condition("firstName", LIKE, new Object());
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that translation fails if field being queried is not a string
	 * field.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02() throws InvalidConditionException
	{
		Condition c = new Condition("age", LIKE, "foo");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at
	 * least 3 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_03() throws InvalidConditionException
	{
		Condition c = new Condition("firstName", LIKE, "12");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, inspector);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at most
	 * 10 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_04() throws InvalidConditionException
	{
		Condition c = new Condition("firstName", LIKE, "12345678901234567890");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, inspector);
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
		Condition c = new Condition("address", LIKE, "foo");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, inspector);
		ct.translate();
	}

}
