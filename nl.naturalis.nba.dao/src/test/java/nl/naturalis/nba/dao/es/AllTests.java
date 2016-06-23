package nl.naturalis.nba.dao.es;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.es.map.MappingFactoryTest;
import nl.naturalis.nba.dao.es.map.MappingInspectorTest;
import nl.naturalis.nba.dao.es.query.BetweenConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.LikeConditionTranslatorTest;
import nl.naturalis.nba.dao.es.util.ESClientManagerTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	RegistryTest.class,
	ESClientManagerTest.class,
	MappingFactoryTest.class,
	MappingInspectorTest.class,
	EqualsConditionTranslatorTest.class,
	LikeConditionTranslatorTest.class ,
	BetweenConditionTranslatorTest.class ,
	SpecimenDAOEqualsQueryTest.class,
	SpecimenDAOTest.class
})
//@formatter:on
public class AllTests {

}
