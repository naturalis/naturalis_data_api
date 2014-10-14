package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import org.junit.*;

import static org.hamcrest.Matchers.is;

public class BioportalSpecimenDaoTest extends DaoIntegrationTest {

    private static final String SPECIMEN_TYPE = "Specimen";
    private static final String TAXON_TYPE = "Taxon";
    private BioportalSpecimenDao dao;
    private TestDocumentCreator documentCreator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalSpecimenDao(client(), INDEX_NAME, new BioportalTaxonDao(client(), INDEX_NAME));
        documentCreator = new TestDocumentCreator();
    }

    @Test
    public void testExtendedNameSearch_nested_AND_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name, "Plantae", "Xylopia", "ferruginea", null);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name, "Fake", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("kingdom", "Plantae");
        params.add("identifications.scientificName.genusOrMonomial", "Xylopia");
        params.add("_andOr", "AND");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);

        assertEquals(1, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_nested_OR_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name, "Plantae", "Xylopia", "ferruginea", null);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name, "Fake", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("kingdom", "Plantae");
        params.add("identifications.scientificName.genusOrMonomial", "Xylopia");
        params.add("_andOr", "OR");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);

        assertEquals(2, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_nonNested_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name, "Plantae", "Xylopia", "ferruginea", null);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name, "Fake", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("gatheringEvent.dateTimeBegin", "-299725200000");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);

        assertEquals(2, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_combined_query() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSpecimenSource("L  0191413", name, "Plantae", "Xylopia", "ferruginea", null);
        String document2Source = documentCreator.createSpecimenSource("L  01914100", name, "Fake", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(document1Source).setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, "Specimen", "2").setSource(document2Source).setRefresh(true).execute().actionGet();

        QueryParams params = new QueryParams();
        params.add("gatheringEvent.dateTimeBegin", "-299725200000");
        params.add("kingdom", "Plantae");
        params.add("_andOr", "AND");

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);

        assertEquals(1, result.getTotalSize());
    }

    @Test
    public void testExtendedNameSearch_nameResolution() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType(SPECIMEN_TYPE)
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        String specimenSource = documentCreator.createSpecimenSource("L  0191413", "Meijer, W.", "Plantae", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, SPECIMEN_TYPE, "1").setSource(specimenSource).setRefresh(true).execute().actionGet();
        String taxonSource = documentCreator.createTaxonSource("Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, TAXON_TYPE, "1").setSource(taxonSource).setRefresh(true).execute().actionGet();

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(2l));

        QueryParams params = new QueryParams();
        params.add("kingdom", "wrong value");

        ResultGroupSet<Specimen, String> resultWithoutName = dao.specimenNameSearch(params);
        assertEquals(0, resultWithoutName.getTotalSize());

        QueryParams paramsWithNameResolution = new QueryParams();
        params.add("kingdom", "wrong value");
        params.add("vernacularNames.name", "henkie");

        ResultGroupSet<Specimen, String> resultWithName = dao.specimenNameSearch(paramsWithNameResolution);
        assertEquals(1, resultWithName.getTotalSize());
    }

    @Test
    public void testGeoShapeQuery() throws Exception {
        createIndex(INDEX_NAME);
        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping("test-specimen-mapping.json"))
                .execute().actionGet();

        String specimenSource = documentCreator.createSpecimenSource("L  0191413", "Meijer, W.", "Plantae", "Xylopia", "ferruginea", null);
        client().prepareIndex(INDEX_NAME, "Specimen", "1").setSource(specimenSource).setRefresh(true).execute()
                .actionGet();

        String geoShapeString = "{\"type\" : \"MultiPolygon\",\"coordinates\" : [[[[14,12], [14,13], [15,13], [15,12], [14,12]]]]}";

        QueryParams params = new QueryParams();
        params.add("_geoShape", geoShapeString);

        assertThat(client().prepareCount(INDEX_NAME).execute().actionGet().getCount(), is(1l));

        ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);

        assertEquals(1, result.getTotalSize());
    }
}

