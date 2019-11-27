package nl.naturalis.nba.etl.brahms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
  BrahmsImportUtilTest.class,
  BrahmsSpecimenTransformerTest.class,
  BrahmsMultiMediaTransformerTest.class,
  simpleBrahmsTest.class
})
public class AllTests {}
