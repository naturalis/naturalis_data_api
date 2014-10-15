package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parent class for elasticsearch integration tests.
 *
 * @author Roberto van der Linden
 */
public class DaoIntegrationTest extends ElasticsearchIntegrationTest {

    public static final String INDEX_NAME = "nda";
    protected static final String SPECIMEN_INDEX_TYPE = "Specimen";
    protected static final String TAXON_INDEX_TYPE = "Taxon";
    protected TestDocumentCreator documentCreator;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Settings indexSettings() {
        ImmutableSettings.Builder builder = ImmutableSettings.builder().loadFromClasspath("test-settings.json");
        return builder.build();
    }

    protected String getMapping(String mappingFile) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(mappingFile);

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
