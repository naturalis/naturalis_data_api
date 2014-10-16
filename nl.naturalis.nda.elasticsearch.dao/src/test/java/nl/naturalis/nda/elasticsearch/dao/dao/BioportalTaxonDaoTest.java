package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;

public class BioportalTaxonDaoTest extends DaoIntegrationTest {

    private BioportalTaxonDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalTaxonDao(client(), INDEX_NAME);
    }

    @Test
    public void testExtendedSearch() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = BioportalTaxonDaoTest.createTestTaxon();
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "1").setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

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

    @Test
    public void testExtendedSearch_someFieldsThatAreUsedInNameResolution() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ESTaxon esTaxon = BioportalTaxonDaoTest.createTestTaxon();
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "1").setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();


        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(1l));

        QueryParams params = new QueryParams();

        params.add("_andOr", "OR");
        params.add("_maxResults", "50");
        params.add("vernacularNames.name", "henkie");
        assertEquals(1, dao.taxonExtendedSearch(params).getSearchResults().size());

        params.remove("vernacularNames.name");
        params.add("synonyms.genusOrMonomial", "genusOrMonomialSynonyms");
        assertEquals(1, dao.taxonExtendedSearch(params).getSearchResults().size());

//        params.add("synonyms.specificEpithet", "");
//        params.add("synonyms.infraspecificEpithet", "");
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