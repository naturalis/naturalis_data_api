package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class BioportalSpecimenDaoTest extends DaoIntegrationTest {

    private BioportalSpecimenDao dao;
    private TestDocumentCreator documentCreator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalSpecimenDao(client(), INDEX_NAME);
        documentCreator = new TestDocumentCreator();
    }

    @Test
    public void testExtendedNameSearch_AND_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("unitID", "L  0191413");
        params.add("gatheringEvent.gatheringPersons.fullName", name);
        params.add("_andOr", "AND");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenExtendedNameSearch(params);

        assertEquals(1, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_OR_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("unitID", "L  0191413");
        params.add("gatheringEvent.gatheringPersons.fullName", name);
        params.add("_andOr", "OR");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenExtendedNameSearch(params);

        assertEquals(2, result.getTotalSize());
    }


}

