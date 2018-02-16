package nl.naturalis.nba.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
  NbaClientTest.class,
  SpecimenClientTest.class,
  MultiMediaObjectClientTest.class,
  TaxonClientTest.class,
	GeoAreaClientTest.class
})
//@formatter:on
public class AllTests {

}
