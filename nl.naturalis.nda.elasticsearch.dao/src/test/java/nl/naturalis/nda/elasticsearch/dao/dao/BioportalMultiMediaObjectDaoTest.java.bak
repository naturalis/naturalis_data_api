package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalMultiMediaObjectTest.createTestMultiMediaObject;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDaoTest.createTestTaxon;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.UNIT_ID;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.*;
import static org.hamcrest.Matchers.is;

public class BioportalMultiMediaObjectDaoTest extends DaoIntegrationTest {

    private BioportalMultiMediaObjectDao bioportalMultiMediaObjectDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        BioportalTaxonDao bioportalTaxonDao = new BioportalTaxonDao(client(), INDEX_NAME, "http://test.nl/test/");
        TaxonDao taxonDao = new TaxonDao(client(), INDEX_NAME, "http://test.nl/test/");
        SpecimenDao specimenDao = new SpecimenDao(client(), INDEX_NAME, taxonDao, "http://test.nl/test/");
        bioportalMultiMediaObjectDao = new BioportalMultiMediaObjectDao(client(), INDEX_NAME,
                bioportalTaxonDao, taxonDao, specimenDao, "http://test.nl/test/");

        createIndex(INDEX_NAME);
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(MULTI_MEDIA_OBJECT_TYPE)
                .setSource(getMapping("test-multimedia-mapping.json"))
                .execute().actionGet();

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(SPECIMEN_TYPE)
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        ESMultiMediaObject multiMediaObject = createTestMultiMediaObject();
        multiMediaObject.setAssociatedTaxonReference("1234");
        multiMediaObject.setAssociatedSpecimenReference("spec1");
        multiMediaObject.setUnitID("unit1");

        IndexRequestBuilder multimediaRequestBuilder = client().prepareIndex(INDEX_NAME, MULTI_MEDIA_OBJECT_TYPE);
        multimediaRequestBuilder.setSource(objectMapper.writeValueAsString(multiMediaObject))
                .setRefresh(true).execute().actionGet();

        multiMediaObject.setUnitID("unit2");
        multimediaRequestBuilder.setSource(objectMapper.writeValueAsString(new ESMultiMediaObject()))
                .setRefresh(true).execute().actionGet();

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.setSourceSystemId("1234");
        IndexRequestBuilder taxonRequestBuilder = client().prepareIndex(INDEX_NAME, TAXON_TYPE);
        taxonRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();

        esTaxon.setSourceSystemId("5678");
        taxonRequestBuilder.setSource(objectMapper.writeValueAsString(esTaxon))
                .setRefresh(true).execute().actionGet();

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(4l));
    }

    @Test
    public void testGetTaxonMultiMediaObjectDetailWithinResultSet() throws Exception {
        QueryParams params = new QueryParams();
        params.add(UNIT_ID, "unit1");
        SearchResultSet<MultiMediaObject> multiMediaObjectSearchResultSet =
                bioportalMultiMediaObjectDao.getTaxonMultiMediaObjectDetailWithinResultSet(params);

        assertEquals(1, multiMediaObjectSearchResultSet.getSearchResults().size());
        MultiMediaObject result = multiMediaObjectSearchResultSet.getSearchResults().get(0).getResult();
        assertEquals("unit1", result.getUnitID());
        assertNotNull(result.getAssociatedTaxon());
        assertEquals("1234", result.getAssociatedTaxon().getSourceSystemId());
    }

    @Test
    public void testGetSpecimenMultiMediaObjectDetailWithinResultSet() throws Exception {
        ESSpecimen esSpecimen = new ESSpecimen();
        esSpecimen.setUnitID("spec1");

        IndexRequestBuilder specimenRequestBuilder = client().prepareIndex(INDEX_NAME, SPECIMEN_TYPE);
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        esSpecimen.setUnitID("spec2");
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add(UNIT_ID, "unit1");

        SearchResultSet<MultiMediaObject> results = bioportalMultiMediaObjectDao
                .getSpecimenMultiMediaObjectDetailWithinResultSet(params);

        assertEquals(1, results.getSearchResults().size());
        MultiMediaObject result = results.getSearchResults().get(0).getResult();
        assertEquals("unit1", result.getUnitID());
        assertNotNull(result.getAssociatedSpecimen());
        assertEquals("spec1", result.getAssociatedSpecimen().getUnitID());
    }

    @Test
    public void testGetSpecimenMultiMediaObjectDetailWithinResultSet_withTaxonFoundByScientificName() throws Exception {
        ESSpecimen esSpecimen = new ESSpecimen();
        esSpecimen.setUnitID("spec1");
        List<SpecimenIdentification> identifications = new ArrayList<>();
        SpecimenIdentification specimenIdentification = new SpecimenIdentification();
        ScientificName scientificName = new ScientificName();
        scientificName.setGenusOrMonomial("Hyphomonas");
        scientificName.setSpecificEpithet("oceanitis");
        specimenIdentification.setScientificName(scientificName);
        identifications.add(specimenIdentification);
        esSpecimen.setIdentifications(identifications);

        IndexRequestBuilder specimenRequestBuilder = client().prepareIndex(INDEX_NAME, SPECIMEN_TYPE);
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        esSpecimen.setUnitID("spec2");
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add(UNIT_ID, "unit1");

        SearchResultSet<MultiMediaObject> results = bioportalMultiMediaObjectDao
                .getSpecimenMultiMediaObjectDetailWithinResultSet(
                        params);

        assertEquals(1, results.getSearchResults().size());
        MultiMediaObject result = results.getSearchResults().get(0).getResult();
        assertEquals("unit1", result.getUnitID());
        assertNotNull(result.getAssociatedSpecimen());
        assertEquals("spec1", result.getAssociatedSpecimen().getUnitID());
        assertEquals(5, results.getSearchResults().get(0).getLinks().size());
    }

    @Test
    public void testGetSpecimenMultiMediaObjectDetailWithinResultSet_withTaxonFoundByFullScientificName() throws Exception {
        ESSpecimen esSpecimen = new ESSpecimen();
        esSpecimen.setUnitID("spec1");
        List<SpecimenIdentification> identifications = new ArrayList<>();
        SpecimenIdentification specimenIdentification = new SpecimenIdentification();
        ScientificName scientificName = new ScientificName();
        scientificName.setFullScientificName("Hyphomonas oceanitis Weiner et al. 1985");
        specimenIdentification.setScientificName(scientificName);
        identifications.add(specimenIdentification);
        esSpecimen.setIdentifications(identifications);

        IndexRequestBuilder specimenRequestBuilder = client().prepareIndex(INDEX_NAME, SPECIMEN_TYPE);
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        esSpecimen.setUnitID("spec2");
        specimenRequestBuilder.setSource(objectMapper.writeValueAsString(esSpecimen))
                .setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add(UNIT_ID, "unit1");

        SearchResultSet<MultiMediaObject> results = bioportalMultiMediaObjectDao
                .getSpecimenMultiMediaObjectDetailWithinResultSet(
                        params);

        assertEquals(1, results.getSearchResults().size());
        MultiMediaObject result = results.getSearchResults().get(0).getResult();
        assertEquals("unit1", result.getUnitID());
        assertNotNull(result.getAssociatedSpecimen());
        assertEquals("spec1", result.getAssociatedSpecimen().getUnitID());
        assertEquals(3, results.getSearchResults().get(0).getLinks().size());
    }
}
