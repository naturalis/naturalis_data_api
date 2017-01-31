package nl.naturalis.nba.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.format.DocumentFlattenerTest;
import nl.naturalis.nba.dao.format.calc.NamePublishedInCalculatorTest;
import nl.naturalis.nba.dao.translate.query.BetweenConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.EqualsIgnoreCaseConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.InValuesBuilderTest;
import nl.naturalis.nba.dao.translate.query.InValuesConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.LikeConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.MatchesConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.query.ShapeInShapeConditionTranslatorTest;
import nl.naturalis.nba.dao.util.ESClientManagerTest;
import nl.naturalis.nba.dao.util.ESUtilTest;
import nl.naturalis.nba.dao.util.SwapFileOutputStreamTest;
import nl.naturalis.nba.dao.util.SwapOutputStreamTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	RegistryTest.class,
	ESClientManagerTest.class,
	ESUtilTest.class,
	EqualsConditionTranslatorTest.class,
	EqualsIgnoreCaseConditionTranslatorTest.class,
	LikeConditionTranslatorTest.class,
	BetweenConditionTranslatorTest.class,
	MatchesConditionTranslatorTest.class,
	InValuesBuilderTest.class,
	InValuesConditionTranslatorTest.class,
	ShapeInShapeConditionTranslatorTest.class,
	NamePublishedInCalculatorTest.class,
	SwapOutputStreamTest.class,
	SwapFileOutputStreamTest.class,
	DocumentFlattenerTest.class,
	SpecimenDao_QueriesWithEqualsOperatorTest.class,
	SpecimenDao_QueriesWithLikeOperatorTest.class,
	SpecimenDao_QueriesWithLessThanOperatorTest.class,
	SpecimenDao_QueriesWithBetweenOperatorTest.class,
	SpecimenDao_QueriesWithInOperatorTest.class,
	SpecimenDao_QueriesWithMatchesOperatorTest.class,
	SpecimenDao_QueriesWithSortingSizingPagingTest.class,
	SpecimenDao_GeoQueriesTest.class,
	SpecimenDao_IsNullQueryTest.class,
	SpecimenDao_MiscellaneousTest.class
})
//@formatter:on
public class AllTests {

}
