package nl.naturalis.nba.etl.col;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
  CoLReferenceBatchTransformerTest.class,
  CoLSynonymBatchTransformerTest.class,
  CoLTaxonImporterTest.class,
  CoLTaxonTransformerTest.class,
  CoLVernacularNameBatchTransformerTest.class,
})
public class AllTests {}
