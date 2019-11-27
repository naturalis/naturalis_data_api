package nl.naturalis.nba.etl;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.etl.crs.CrsMultiMediaTransformerTest;
import nl.naturalis.nba.etl.enrich.SpecimenMultimediaEnricherTest;


@RunWith(Suite.class)
@SuiteClasses({ 
  TransformUtilTest.class,
  CrsMultiMediaTransformerTest.class,
  SpecimenMultimediaEnricherTest.class
})

@Ignore
public class BuildTest {}
