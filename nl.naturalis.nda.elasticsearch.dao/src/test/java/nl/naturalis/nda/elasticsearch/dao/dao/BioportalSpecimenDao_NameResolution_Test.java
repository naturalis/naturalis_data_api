package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;

public class BioportalSpecimenDao_NameResolution_Test extends AbstractBioportalSpecimenDaoTest {

    @Test
    public void testExtendedNameSearch_nameResolution() throws Exception {
        QueryParams params = setupNameResolutionTest();

        QueryParams paramsWithNameResolution = new QueryParams();
        params.add("kingdom", "wrong value");
        params.add("vernacularNames.name", "henkie");

        ResultGroupSet<Specimen, String> resultWithName = dao.specimenNameSearch(paramsWithNameResolution);
        assertEquals(1, resultWithName.getTotalSize());
    }

    private QueryParams setupNameResolutionTest() throws IOException {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(SPECIMEN_TYPE)
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESSpecimen esSpecimen = createSpecimen();

        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(objectMapper.writeValueAsString(esSpecimen)).setRefresh(true).execute().actionGet();
        String taxonSource = documentCreator.createTaxonSource("Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(taxonSource).setRefresh(true).execute().actionGet();

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        QueryParams params = new QueryParams();
        params.add("kingdom", "wrong value");

        ResultGroupSet<Specimen, String> resultWithoutName = dao.specimenNameSearch(params);
        assertEquals(0, resultWithoutName.getTotalSize());
        return params;
    }
}

