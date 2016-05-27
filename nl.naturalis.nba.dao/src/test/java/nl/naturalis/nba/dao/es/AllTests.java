package nl.naturalis.nba.dao.es;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.es.query.EqualsConditionTranslatorTest;
import nl.naturalis.nba.dao.es.query.LikeConditionTranslatorTest;
import nl.naturalis.nba.dao.es.util.ClientFactoryTest;
import nl.naturalis.nba.elasticsearch.map.MappingInspectorTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	ClientFactoryTest.class,
	EqualsConditionTranslatorTest.class,
	LikeConditionTranslatorTest.class ,
	SpecimenDAOTest.class,
	MappingInspectorTest.class,
	RegistryTest.class
})
//@formatter:on
public class AllTests {

}
