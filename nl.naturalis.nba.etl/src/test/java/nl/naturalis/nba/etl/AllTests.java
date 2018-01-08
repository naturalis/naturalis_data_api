package nl.naturalis.nba.etl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import nl.naturalis.nba.etl.brahms.BrahmsImportUtilTest;
import nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformerTest;
import nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformerTest;
import nl.naturalis.nba.etl.col.CoLReferenceBatchTransformerTest;
import nl.naturalis.nba.etl.col.CoLSynonymBatchTransformerTest;
import nl.naturalis.nba.etl.col.CoLTaxonTransformerTest;
import nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformerTest;
import nl.naturalis.nba.etl.crs.CrsImportUtilTest;
import nl.naturalis.nba.etl.crs.CrsMultiMediaTransformerTest;
import nl.naturalis.nba.etl.crs.CrsSpecimenTransformerTest;
import nl.naturalis.nba.etl.geo.GeoTransformerTest;
import nl.naturalis.nba.etl.nsr.NsrImportUtilTest;
import nl.naturalis.nba.etl.nsr.NsrMultiMediaTransformerTest;
import nl.naturalis.nba.etl.nsr.NsrTaxonTransformerTest;


@RunWith(Suite.class)
@SuiteClasses({ 
    
    ETLUtilTest.class,
    BrahmsImportUtilTest.class,
    BrahmsSpecimenTransformerTest.class,
    BrahmsMultiMediaTransformerTest.class,
    CrsSpecimenTransformerTest.class,
    CrsImportUtilTest.class,
    CrsSpecimenTransformerTest.class,
    CrsMultiMediaTransformerTest.class,
    CoLTaxonTransformerTest.class,
    CoLSynonymBatchTransformerTest.class,
    CoLVernacularNameBatchTransformerTest.class,
    CoLReferenceBatchTransformerTest.class,
    NsrTaxonTransformerTest.class,
    NsrImportUtilTest.class,
    NsrMultiMediaTransformerTest.class,
    GeoTransformerTest.class,
    TransformUtilTest.class
})
public class AllTests {

}
