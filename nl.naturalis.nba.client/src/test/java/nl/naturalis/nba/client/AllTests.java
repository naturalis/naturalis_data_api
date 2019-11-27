package nl.naturalis.nba.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  NbaClientTest.class,
  SpecimenClientTest.class,
  MultiMediaObjectClientTest.class,
  TaxonClientTest.class,
	GeoAreaClientTest.class,
	DownloadTest.class
})

public class AllTests {}
