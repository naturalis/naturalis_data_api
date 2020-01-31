package nl.naturalis.nba.etl.nsr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.etl.nsr.NsrImportUtilTest;
import nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformerTest;
import nl.naturalis.nba.etl.nsr.NsrTaxonTransformerTest;


@RunWith(Suite.class)
@SuiteClasses({
    NsrImportUtilTest.class,
    NsrMultiMediaTransformerTest.class,
    NsrTaxonTransformerTest.class,
})

public class AllTests {}
