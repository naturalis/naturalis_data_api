package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class BioportalTaxonDaoTest extends DaoIntegrationTest {

    public static final String INDEX_TYPE = "Taxon";

    private BioportalTaxonDao dao;
    private TestDocumentCreator documentCreator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalTaxonDao(client(), INDEX_NAME);
        documentCreator = new TestDocumentCreator();
    }

    @Test
    public void testExtendedSearch() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        String document1Source = documentCreator.createTaxonSource();
        client().prepareIndex(INDEX_NAME, INDEX_TYPE, "1").setSource(document1Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(1l));

        SearchResultSet<Taxon> result = dao.taxonExtendedSearch(params);

        assertEquals(1, result.getSearchResults().size());
        //TODO enable when implemented setSearchTerms
//        assertEquals(1, result.getSearchTerms().size());
//        assertEquals("Hyphomonas", result.getSearchTerms().get(0));
        assertEquals("Rhodobacteraceae", result.getSearchResults().get(0).getResult().getDefaultClassification().getFamily());
    }

}