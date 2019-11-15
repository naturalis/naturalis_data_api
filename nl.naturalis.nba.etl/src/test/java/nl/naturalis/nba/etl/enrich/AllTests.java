package nl.naturalis.nba.etl.enrich;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import nl.naturalis.nba.etl.enrich.EnrichmentUtilTest;
import nl.naturalis.nba.etl.enrich.MultimediaTaxonomicEnricherTest;
import nl.naturalis.nba.etl.enrich.SpecimenMultimediaEnricherTest;
import nl.naturalis.nba.etl.enrich.SpecimenTaxonomicEnricherTest;


@RunWith(Suite.class)
@SuiteClasses({ 
    EnrichmentUtilTest.class,
    MultimediaTaxonomicEnricherTest.class,
    SpecimenMultimediaEnricherTest.class,
    SpecimenTaxonomicEnricherTest.class,
})

public class AllTests {}
