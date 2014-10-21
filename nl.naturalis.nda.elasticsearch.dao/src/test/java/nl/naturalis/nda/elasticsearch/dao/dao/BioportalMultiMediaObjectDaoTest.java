package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.junit.Before;
import org.junit.Test;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalMultiMediaObjectTest.createTestMultiMediaObject;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDaoTest.createTestTaxon;
import static org.hamcrest.Matchers.is;

public class BioportalMultiMediaObjectDaoTest extends DaoIntegrationTest {

    private BioportalMultiMediaObjectDao bioportalMultiMediaObjectDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        BioportalTaxonDao bioportalTaxonDao = new BioportalTaxonDao(client(), INDEX_NAME);
        TaxonDao taxonDao = new TaxonDao(client(), INDEX_NAME);
        bioportalMultiMediaObjectDao = new BioportalMultiMediaObjectDao(client(), INDEX_NAME, bioportalTaxonDao, taxonDao);
    }

    @Test
    public void testGetTaxonMultiMediaObjectDetailWithinResultSet() throws Exception {
        createIndex(INDEX_NAME);
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(MULTI_MEDIA_OBJECT_INDEX_TYPE)
                .setSource(getMapping("test-multimedia-mapping.json"))
                .execute().actionGet();

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESMultiMediaObject multiMediaObject = createTestMultiMediaObject();
        multiMediaObject.setAssociatedTaxonReference("1234");
        multiMediaObject.setUnitID("unit1");

        client().prepareIndex(INDEX_NAME, MULTI_MEDIA_OBJECT_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(multiMediaObject))
                .setRefresh(true).execute().actionGet();

        multiMediaObject.setUnitID("unit2");
        client().prepareIndex(INDEX_NAME, MULTI_MEDIA_OBJECT_INDEX_TYPE, "2")
                .setSource(objectMapper.writeValueAsString(new ESMultiMediaObject()))
                .setRefresh(true).execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.setSourceSystemId("1234");
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();

        esTaxon.setSourceSystemId("5678");
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "2")
                .setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(4l));

        QueryParams params = new QueryParams();
        params.add("unitID", "unit1");
        SearchResultSet<MultiMediaObject> multiMediaObjectSearchResultSet = bioportalMultiMediaObjectDao.getTaxonMultiMediaObjectDetailWithinResultSet(params);

        assertEquals(1, multiMediaObjectSearchResultSet.getSearchResults().size());
        MultiMediaObject result = multiMediaObjectSearchResultSet.getSearchResults().get(0).getResult();
        assertEquals("unit1", result.getUnitID());
        assertNotNull(result.getAssociatedTaxon());
        assertEquals("1234", result.getAssociatedTaxon().getSourceSystemId());
    }
}
