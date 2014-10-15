package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import org.junit.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDaoTest.createTestTaxon;
import static org.hamcrest.Matchers.is;

public class BioportalSpecimenDao_NameResolution_Test extends AbstractBioportalSpecimenDaoTest {

    @Test
    public void testExtendedNameSearch_nameResolution() throws Exception {
        setupNameResolutionTest();

        QueryParams params = new QueryParams();
        params.add("_andOr", "OR");
        params.add("kingdom", "wrong value");
        params.add("vernacularNames.name", "henkie");

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);
        assertEquals(1, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_orderClassificationResolution_() throws Exception {
        setupNameResolutionTest();

        QueryParams paramsWithNameResolution = new QueryParams();
        paramsWithNameResolution.add("kingdom", "wrong value");
        paramsWithNameResolution.add("vernacularNames.name", "henkie");

        ResultGroupSet<Specimen, String> resultWithName = dao.specimenNameSearch(paramsWithNameResolution);
        assertEquals(1, resultWithName.getTotalSize());
    }

    private void setupNameResolutionTest() throws IOException {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(SPECIMEN_INDEX_TYPE)
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESSpecimen esSpecimen = createSpecimen();
        SpecimenIdentification specimenIdentification = new SpecimenIdentification();
        {
            DefaultClassification defaultClassification = new DefaultClassification();
            defaultClassification.setKingdom("Plantae");
            specimenIdentification.setDefaultClassification(defaultClassification);

            ScientificName scientificName = new ScientificName();
            scientificName.setGenusOrMonomial("geslacht");
            scientificName.setSpecificEpithet("speciek");
            specimenIdentification.setScientificName(scientificName);

            esSpecimen.setIdentifications(asList(specimenIdentification));
        }
        client().prepareIndex(INDEX_NAME, SPECIMEN_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(esSpecimen)).setRefresh(true).execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.getAcceptedName().setGenusOrMonomial("geslacht");
        esTaxon.getAcceptedName().setSpecificEpithet("speciek");
        esTaxon.getAcceptedName().setInfraspecificEpithet(null);
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        QueryParams params = new QueryParams();
        params.add("kingdom", "wrong value");

        ResultGroupSet<Specimen, String> resultWithoutName = dao.specimenNameSearch(params);
        assertEquals(0, resultWithoutName.getTotalSize());
    }
}

