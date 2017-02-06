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
import nl.naturalis.nba.dao.test.TestPerson;

@SuppressWarnings("static-method")
public class LikeConditionTranslatorTest {

	private static MappingInfo<LikeTestObject> likeTestObjectMappingInfo;
	private static MappingInfo<TestPerson> testPersonMappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<LikeTestObject> m = MappingFactory.getMapping(LikeTestObject.class);
		likeTestObjectMappingInfo = new MappingInfo<>(m);
	}

	public void testWithNonNestedField_01()
	{

	}

	/*
	 * Checks that translation fails if the Condition.value is not String.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testWithNonStringValue_01() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, new Object());
		ConditionTranslator ct = getTranslator(c, likeTestObjectMappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if field being queried is not a string
	 * field.
	 */
	@Test(expected = IllegalOperatorException.class)
	public void testWithNonStringField_01() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("age", LIKE, "foo");
		ConditionTranslator ct = getTranslator(c, likeTestObjectMappingInfo);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at
	 * least 3 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testWithMinNGramSize_01() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, "ab");
		ConditionTranslator ct = getTranslator(c, likeTestObjectMappingInfo);
		ct.translate();
	}

	/*
	 * Checks that value field in Condition class is enforced to contain at most
	 * 15 characters.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testWithMaxNGramSize_01() throws InvalidConditionException
	{
		QueryCondition c = new QueryCondition("firstName", LIKE, "12345678901234567890");
		ConditionTranslator ct = getTranslator(c, likeTestObjectMappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the field is not analyzed using the LIKE
	 * enabling analyzer.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testWithLikeOperatorNotAllowed() throws InvalidConditionException
	{
		// address field has no @Analyzers annotation
		QueryCondition c = new QueryCondition("address", LIKE, "foo");
		ConditionTranslator ct = getTranslator(c, likeTestObjectMappingInfo);
		ct.translate();
	}

}
