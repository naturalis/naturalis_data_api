package nl.naturalis.nba.etl.crs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import nl.naturalis.nba.etl.crs.CrsImportUtilTest;
import nl.naturalis.nba.etl.crs.CrsMultiMediaTransformerTest;
import nl.naturalis.nba.etl.crs.CrsSpecimenTransformerTest;

@RunWith(Suite.class)
@SuiteClasses({ 
    CrsImportUtilTest.class,
    CrsSpecimenTransformerTest.class,
    CrsMultiMediaTransformerTest.class,
})

public class AllTests {}
