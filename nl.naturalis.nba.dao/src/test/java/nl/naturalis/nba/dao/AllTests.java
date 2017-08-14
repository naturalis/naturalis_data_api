package nl.naturalis.nba.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.format.DocumentFlattenerTest;
import nl.naturalis.nba.dao.format.calc.NamePublishedInCalculatorTest;
import nl.naturalis.nba.dao.translate.BetweenConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.ConditionTranslatorFactoryTest;
import nl.naturalis.nba.dao.translate.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.EqualsIgnoreCaseConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.ExploderTest;
import nl.naturalis.nba.dao.translate.InConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.LikeConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.MatchesConditionTranslatorTest;
import nl.naturalis.nba.dao.translate.ShapeInShapeConditionTranslatorTest;
import nl.naturalis.nba.dao.util.SwapFileOutputStreamTest;
import nl.naturalis.nba.dao.util.SwapOutputStreamTest;
import nl.naturalis.nba.dao.util.es.ESDateInputTest;
import nl.naturalis.nba.dao.util.es.ESUtilTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	RegistryTest.class,
	ESClientManagerTest.class,
	ESUtilTest.class,
	ESDateInputTest.class,
	NamePublishedInCalculatorTest.class,
	SwapOutputStreamTest.class,
	SwapFileOutputStreamTest.class,
	DocumentFlattenerTest.class,
	ConditionTranslatorFactoryTest.class,
	BetweenConditionTranslatorTest.class,
	EqualsConditionTranslatorTest.class,
	EqualsIgnoreCaseConditionTranslatorTest.class,
	ExploderTest.class,
	InConditionTranslatorTest.class,
	LikeConditionTranslatorTest.class,
	MatchesConditionTranslatorTest.class,
	ShapeInShapeConditionTranslatorTest.class,
	SpecimenDaoTest_Between.class,
	SpecimenDaoTest_Equals.class,
	SpecimenDaoTest_GeoQueries.class,
	SpecimenDaoTest_In.class,
	SpecimenDaoTest_IsNotNull.class,
	SpecimenDaoTest_IsNull.class,
	SpecimenDaoTest_LessThan.class,
	SpecimenDaoTest_Like.class,
	SpecimenDaoTest_Matches.class,
	SpecimenDaoTest_Miscellaneous.class,
	SpecimenDaoTest_SortingSizingPaging.class,
	SpecimenDaoTest_DwcaTest.class
})
//@formatter:on
public class AllTests {

}
