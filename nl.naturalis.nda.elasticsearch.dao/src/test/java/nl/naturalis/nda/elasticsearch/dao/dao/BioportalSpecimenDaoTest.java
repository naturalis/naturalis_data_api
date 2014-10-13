package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.Matchers.is;

public class BioportalSpecimenDaoTest extends ElasticsearchIntegrationTest {

    private static final String INDEX_NAME = "nda";

    private BioportalSpecimenDao dao;
    private TestDocumentCreator documentCreator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalSpecimenDao(client(), INDEX_NAME);
        documentCreator = new TestDocumentCreator();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    public Settings indexSettings() {
        ImmutableSettings.Builder builder = ImmutableSettings.builder().loadFromClasspath("test-settings.json");
        return builder.build();
    }

    @Test
    public void simpleSearch() throws Exception {
        createIndex(INDEX_NAME);

        client().admin().indices().preparePutMapping(INDEX_NAME).setType("Specimen")
                .setSource(getMapping())
                .execute().actionGet();

        String name = "Meijer, W.";
        String document1Source = documentCreator.createSource("L  0191413", name);
        String document2Source = documentCreator.createSource("L  01914100", name);
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

    private String getMapping() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("test-specimen-mapping.json");

        StringBuilder mappingBuilder = new StringBuilder();

        BufferedReader br = null;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            String line;

            while ((line = br.readLine()) != null) {
                mappingBuilder.append(line);
            }
        } catch (IOException e) {
            logger.error("Error while reading mapping file.");
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return mappingBuilder.toString();
    }
}

