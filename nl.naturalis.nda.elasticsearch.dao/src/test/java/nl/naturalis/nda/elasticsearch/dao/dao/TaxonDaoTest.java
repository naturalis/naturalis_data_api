package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDaoTest.createTestTaxon;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.TAXON_TYPE;
import static org.hamcrest.Matchers.is;

public class TaxonDaoTest extends DaoIntegrationTest {

    private TaxonDao taxonDao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        taxonDao = new TaxonDao(client(), INDEX_NAME);
    }

    @Test
    public final void testTaxonSearch_singleValue() throws IOException {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        IndexRequestBuilder indexRequestBuilder = client().prepareIndex(INDEX_NAME, TAXON_TYPE);
        indexRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        esTaxon.getAcceptedName().setGenusOrMonomial("otherValue");
        indexRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("_andOr", "AND");
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");

        SearchResultSet<Taxon> taxonDetail = taxonDao.getTaxonDetail(params);

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        List<SearchResult<Taxon>> searchResults = taxonDetail.getSearchResults();
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertEquals("Hyphomonas", searchResults.get(0).getResult().getAcceptedName().getGenusOrMonomial());
    }

    @Test
    public final void testTaxonSearch_multiValue() throws IOException {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.getAcceptedName().setInfraspecificEpithet("someValue");
        IndexRequestBuilder indexRequestBuilder = client().prepareIndex(INDEX_NAME, TAXON_TYPE);
        indexRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        esTaxon.getAcceptedName().setGenusOrMonomial("otherValue");
        indexRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("_andOr", "AND");
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");
        params.add("acceptedName.infraspecificEpithet", "someValue");

        SearchResultSet<Taxon> taxonDetail = taxonDao.getTaxonDetail(params);

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        List<SearchResult<Taxon>> searchResults = taxonDetail.getSearchResults();
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertEquals("Hyphomonas", searchResults.get(0).getResult().getAcceptedName().getGenusOrMonomial());
    }

    @Test
    public final void testOffSet() throws IOException {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.getAcceptedName().setInfraspecificEpithet("someValue");

        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        esTaxon.getAcceptedName().setInfraspecificEpithet("otherValue");
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "2").setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");
        params.add("acceptedName.infraspecificEpithet", "otherValue");
        params.add("_offSet", "1");

        SearchResultSet<Taxon> taxonDetail = taxonDao.getTaxonDetail(params);

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        List<SearchResult<Taxon>> searchResults = taxonDetail.getSearchResults();
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertEquals("otherValue", searchResults.get(0).getResult().getAcceptedName().getInfraspecificEpithet());
    }
}
