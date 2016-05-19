package nl.naturalis.nba.dao.es;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.dao.es.query.ConditionTranslatorTest;
import nl.naturalis.nba.dao.es.util.ClientFactoryTest;
import nl.naturalis.nba.elasticsearch.map.MappingInspectorTest;

@RunWith(Suite.class)
@SuiteClasses({ ClientFactoryTest.class, ConditionTranslatorTest.class, SpecimenDAOTest.class,
		MappingInspectorTest.class, RegistryTest.class })
public class AllTests {

}
