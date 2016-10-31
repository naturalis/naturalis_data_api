package nl.naturalis.nba.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.format.DocumentFlattenerTest;
import nl.naturalis.nba.dao.format.calc.NamePublishedInCalculatorTest;
import nl.naturalis.nba.dao.query.BetweenConditionTranslatorTest;
import nl.naturalis.nba.dao.query.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.query.InValuesConditionTranslatorTest;
import nl.naturalis.nba.dao.query.InValuesBuilderTest;
import nl.naturalis.nba.dao.query.LikeConditionTranslatorTest;
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
	LikeConditionTranslatorTest.class ,
	BetweenConditionTranslatorTest.class ,
	InValuesBuilderTest.class,
	InValuesConditionTranslatorTest.class,
	NamePublishedInCalculatorTest.class,
	SwapOutputStreamTest.class,
	SwapFileOutputStreamTest.class,
	DocumentFlattenerTest.class,
	SpecimenDaoNullChecksTest.class,
	SpecimenDaoWithBetweenConditionTest.class,
	SpecimenDaoWithInConditionTest.class,
	SpecimenDaoTest.class
})
//@formatter:on
public class AllTests {

}
