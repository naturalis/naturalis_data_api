package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.search.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.TAXON_TYPE;
import static org.hamcrest.Matchers.is;

public class BioportalTaxonDaoTest extends DaoIntegrationTest {

    private BioportalTaxonDao taxonDao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        taxonDao = new BioportalTaxonDao(client(), INDEX_NAME);
    }

    @Test
    public void testExtendedSearch() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = BioportalTaxonDaoTest.createTestTaxon();
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(1l));

        ResultGroupSet<Taxon, String> result = taxonDao.taxonSearch(params);

        assertEquals(1, result.getResultGroups().size());
        SearchResult<Taxon> result1 = result.getResultGroups().get(0).getSearchResults().get(0);
        assertEquals("Rhodobacteraceae", result1.getResult().getDefaultClassification().getFamily());
        assertEquals(1, result1.getLinks().size());

        assertThat(result.getQueryParameters().getParam("acceptedName.genusOrMonomial"), is("Hyphomonas"));

        assertThat(result1.getMatchInfo().size(), is(1));
        assertThat(result1.getMatchInfo().get(0).getValueHighlighted(), is("<span class=\"search_hit\">Hyphomonas</span>"));
    }

    @Test
    public void testExtendedSearch_someFieldsThatAreUsedInNameResolution() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = BioportalTaxonDaoTest.createTestTaxon();
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();


        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(1l));

        QueryParams params = new QueryParams();

        params.add("_andOr", "OR");
        params.add("_maxResults", "50");
        params.add("vernacularNames.name", "henkie");
        assertEquals(1, taxonDao.search(params, null, null, true).getResultGroups().size());

        params.remove("vernacularNames.name");
        params.add("synonyms.genusOrMonomial", "genusOrMonomialSynonyms");
        assertEquals(1, taxonDao.search(params, null, null, true).getResultGroups().size());

//        params.add("synonyms.specificEpithet", "");
//        params.add("synonyms.infraspecificEpithet", "");
    }

    @Test
    public void testCreateTaxonDetailSearchResultSet_firstResult_noPrevious() throws Exception {
        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");

        ResultGroupSet<Taxon,String> resultGroupSet = new ResultGroupSet<>();
        ResultGroup<Taxon, String> group = new ResultGroup<>();
        group.addSearchResult(createTaxonWithScientificName("Hyphomonas", "oceanitis", null));
        group.addSearchResult(createTaxonWithScientificName("Other", "diep in de zee", ""));
        group.addSearchResult(createTaxonWithScientificName("Other 2", "geen idee", ""));
        resultGroupSet.addGroup(group);

        SearchResultSet<Taxon> taxonDetailSearchResultSet = taxonDao.createTaxonDetailSearchResultSet(params,
                                                                                                      resultGroupSet);

        List<SearchResult<Taxon>> searchResults = taxonDetailSearchResultSet.getSearchResults();
        assertEquals(1, searchResults.size());
        assertEquals("Hyphomonas", searchResults.get(0).getResult().getAcceptedName().getGenusOrMonomial());
        assertEquals(1, searchResults.get(0).getLinks().size());
        assertEquals("_next", searchResults.get(0).getLinks().get(0).getRel());
    }

    @Test
    public void testCreateTaxonDetailSearchResultSet_lastResult_noNext() throws Exception {
        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");

        ResultGroupSet<Taxon,String> resultGroupSet = new ResultGroupSet<>();
        ResultGroup<Taxon, String> group = new ResultGroup<>();
        group.addSearchResult(createTaxonWithScientificName("Other", "diep in de zee", ""));
        group.addSearchResult(createTaxonWithScientificName("Other 2", "geen idee", ""));
        group.addSearchResult(createTaxonWithScientificName("Hyphomonas", "oceanitis", null));
        resultGroupSet.addGroup(group);

        SearchResultSet<Taxon> taxonDetailSearchResultSet = taxonDao.createTaxonDetailSearchResultSet(params,
                resultGroupSet);

        List<SearchResult<Taxon>> searchResults = taxonDetailSearchResultSet.getSearchResults();
        assertEquals(1, searchResults.size());
        assertEquals("Hyphomonas", searchResults.get(0).getResult().getAcceptedName().getGenusOrMonomial());
        assertEquals(1, searchResults.get(0).getLinks().size());
        assertEquals("_previous", searchResults.get(0).getLinks().get(0).getRel());
    }

    @Test
    public void testCreateTaxonDetailSearchResultSet_middleResult() throws Exception {
        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");

        ResultGroupSet<Taxon,String> resultGroupSet = new ResultGroupSet<>();
        ResultGroup<Taxon, String> group = new ResultGroup<>();
        group.addSearchResult(createTaxonWithScientificName("Other", "diep in de zee", "first"));
        group.addSearchResult(createTaxonWithScientificName("Hyphomonas", "oceanitis", null));
        group.addSearchResult(createTaxonWithScientificName("Other 2", "geen idee", "last"));
        resultGroupSet.addGroup(group);

        SearchResultSet<Taxon> taxonDetailSearchResultSet = taxonDao.createTaxonDetailSearchResultSet(params,
                resultGroupSet);

        List<SearchResult<Taxon>> searchResults = taxonDetailSearchResultSet.getSearchResults();
        assertEquals(1, searchResults.size());
        assertEquals("Hyphomonas", searchResults.get(0).getResult().getAcceptedName().getGenusOrMonomial());
        List<Link> links = searchResults.get(0).getLinks();
        assertEquals(2, links.size());
        assertEquals("_previous", links.get(0).getRel());
        assertTrue(links.get(0).getHref().contains("first"));
        assertEquals("_next", links.get(1).getRel());
        assertTrue(links.get(1).getHref().contains("last"));
    }

    @Test
    public void testTaxonSearchGrouping() throws Exception {
        ScientificName acceptedName = new ScientificName();
        acceptedName.setGenusOrMonomial("Hyphomonas");
        acceptedName.setSpecificEpithet("oceanitis");
        acceptedName.setInfraspecificEpithet("test");

        ESTaxon testTaxon = createTestTaxon();
        ESTaxon testTaxon1 = createTestTaxon();
        ESTaxon testTaxon2 = createTestTaxon();
        ESTaxon testTaxon3 = createTestTaxon();
        testTaxon2.setAcceptedName(acceptedName);
        testTaxon3.setAcceptedName(acceptedName);

        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(objectMapper.writeValueAsString(testTaxon))
                .setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "2").setSource(objectMapper.writeValueAsString(testTaxon1))
                .setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "3").setSource(objectMapper.writeValueAsString(testTaxon2))
                .setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "4").setSource(objectMapper.writeValueAsString(testTaxon3))
                .setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("acceptedName.genusOrMonomial", "Hyphomonas");
        params.add("acceptedName.specificEpithet", "oceanitis");

        ResultGroupSet<Taxon, String> taxonDetailWithinResultSet = taxonDao.taxonSearch(params);

        assertTrue(taxonDetailWithinResultSet.getResultGroups().size() == 2);
    }


    //================================================ Helper methods ==================================================

    private Taxon createTaxonWithScientificName(String genusOrMonomial, String specificEpithet,
                                                String infraspecificEpithet) {
        Taxon taxon = new Taxon();
        ScientificName scientificName = new ScientificName();
        scientificName.setFullScientificName(genusOrMonomial + " " + specificEpithet + " " + infraspecificEpithet);
        scientificName.setGenusOrMonomial(genusOrMonomial);
        scientificName.setSpecificEpithet(specificEpithet);
        scientificName.setInfraspecificEpithet(infraspecificEpithet);
        taxon.setAcceptedName(scientificName);
        return taxon;
    }

    public static ESTaxon createTestTaxon() {
        ESTaxon esTaxon = new ESTaxon();

        SourceSystem sourceSystem = new SourceSystem();
        sourceSystem.setCode("COL");
        sourceSystem.setName("Catalogue Of Life");
        esTaxon.setSourceSystem(sourceSystem);
        esTaxon.setSourceSystemId("4259353");

        ScientificName acceptedName = new ScientificName();
        acceptedName.setFullScientificName("Hyphomonas oceanitis Weiner et al. 1985");
        acceptedName.setGenusOrMonomial("Hyphomonas");
        acceptedName.setSpecificEpithet("oceanitis");
        acceptedName.setAuthorshipVerbatim("Weiner et al. 1985");
        esTaxon.setAcceptedName(acceptedName);

        DefaultClassification defaultClassification = new DefaultClassification();
        defaultClassification.setKingdom("Bacteria");
        defaultClassification.setPhylum("Proteobacteria");
        defaultClassification.setClassName("Alphaproteobacteria");
        defaultClassification.setOrder("Rhodobacterales");
        defaultClassification.setFamily("Rhodobacteraceae");
        defaultClassification.setGenus("Hyphomonas");
        defaultClassification.setSpecificEpithet("oceanitis");
        esTaxon.setDefaultClassification(defaultClassification);

        List<Monomial> systemClassification = new ArrayList<>();
        systemClassification.add(new Monomial("kingdom", "Bacteria"));
        systemClassification.add(new Monomial("phylum", "Proteobacteria"));
        systemClassification.add(new Monomial("class", "Alphaproteobacteria"));
        systemClassification.add(new Monomial("order", "Rhodobacterales"));
        systemClassification.add(new Monomial("family", "Rhodobacteraceae"));
        systemClassification.add(new Monomial("genus", "Hyphomonas"));
        systemClassification.add(new Monomial("specificEpithet", "oceanitis"));
        esTaxon.setSystemClassification(systemClassification);

        ScientificName synonym = new ScientificName();
        synonym.setGenusOrMonomial("genusOrMonomialSynonyms");
        synonym.setSpecificEpithet("specificEpithetSynonyms");
        esTaxon.setSynonyms(Collections.singletonList(synonym));

        VernacularName vernacularName = new VernacularName();
        vernacularName.setName("henkie");
        esTaxon.setVernacularNames(Collections.singletonList(vernacularName));

        return esTaxon;
    }
}