package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;

public class LikeConditionTranslatorTest {

	/*
	 * Checks that translation fails if the Condition.value is not String.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition c = new Condition("firstName", LIKE, new Object());
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
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
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
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
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
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
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the field is not analyzed using the LIKE
	 * enabling analyzer.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_05() throws InvalidConditionException
	{
		Condition c = new Condition("address", LIKE, "foo");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the field is not analyzed using the LIKE
	 * enabling analyzer.
	 */
	@Test
	public void testTranslate_06() throws InvalidConditionException
	{
		// address field has no @Analyzers annotation
		Condition c = new Condition("firstName", LIKE, "Smith");
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(c, LikeTestObject.class);
		QueryBuilder query = ct.translate();
	}

}
