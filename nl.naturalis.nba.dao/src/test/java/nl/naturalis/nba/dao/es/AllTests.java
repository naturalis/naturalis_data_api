package nl.naturalis.nba.dao.es;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.es.format.DocumentFlattenerTest;
import nl.naturalis.nba.dao.es.format.calc.NamePublishedInCalculatorTest;
import nl.naturalis.nba.dao.es.query.BetweenConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.InConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.InValuesBuilderTest;
import nl.naturalis.nba.dao.es.query.LikeConditionTranslatorTest;
import nl.naturalis.nba.dao.es.util.ESClientManagerTest;
import nl.naturalis.nba.dao.es.util.ESUtilTest;
import nl.naturalis.nba.dao.es.util.SwapFileOutputStreamTest;
import nl.naturalis.nba.dao.es.util.SwapOutputStreamTest;

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
	InConditionTranslatorTest.class,
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
