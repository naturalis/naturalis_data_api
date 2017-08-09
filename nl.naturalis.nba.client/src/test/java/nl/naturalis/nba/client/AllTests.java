package nl.naturalis.nba.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	GeoAreaClientTest.class,
	NbaClientTest.class,
	SpecimenClientTest.class,
	TaxonClientTest.class
})
//@formatter:on
public class AllTests {

}
