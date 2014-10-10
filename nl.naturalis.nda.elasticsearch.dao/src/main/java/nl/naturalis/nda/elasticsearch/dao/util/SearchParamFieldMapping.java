package nl.naturalis.nda.elasticsearch.dao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Provides functionality to get a mapping for specific field or NBA alias.
 *
 * @author Roberto van der Linden
 */
public class SearchParamFieldMapping {

    public static final String BOOST_SUFFIX = ".boost";
    private static final SearchParamFieldMapping INSTANCE = new SearchParamFieldMapping();

    private Properties multimediaProperties;
    private Properties taxonProperties;
    private Properties specimenProperties;

    private SearchParamFieldMapping() {
        multimediaProperties = createPropertiesFromFile("drupal-field-mapping-multimedia.properties");
        taxonProperties = createPropertiesFromFile("drupal-field-mapping-taxon.properties");
        specimenProperties = createPropertiesFromFile("drupal-field-mapping-specimen.properties");
    }

    public static SearchParamFieldMapping getInstance() {
        return INSTANCE;
    }

    /**
     * Get the mapping for a specimen field or alias.
     *
     * @param field a specimen field or alias
     * @return a list of all found mappings, including their boost value
     */
    public List<FieldMapping> getSpecimenMappingForField(String field) {
        return getMappingForField(field, specimenProperties);
    }

    /**
     * Get the mapping for a taxon field or alias.
     *
     * @param field a taxon field or alias
     * @return a list of all found mappings, including their boost value
     */
    public List<FieldMapping> getTaxonMappingForField(String field) {
        return getMappingForField(field, taxonProperties);
    }

    /**
     * Get the mapping for a multimedia field or alias.
     *
     * @param field a multimedia field or alias
     * @return a list of all found mappings, including their boost value
     */
    public List<FieldMapping> getMultimediaMappingForField(String field) {
        return getMappingForField(field, multimediaProperties);
    }

    /**
     * Get the boost value for the given field.
     *
     * @param field the field to find the boost value for
     * @return the boost value if property found, null otherwise
     */
    public Float getBoostValueForField(String field) {
        String property = specimenProperties.getProperty(field + BOOST_SUFFIX);
        if (property != null && !property.isEmpty()) {
            return Float.valueOf(property);
        }

        return null;
    }

    private List<FieldMapping> getMappingForField(String field, Properties properties) {
        List<FieldMapping> mappings = new ArrayList<>();

        String property = properties.getProperty(field);
        if (property != null && !property.isEmpty()) {
            String[] esFields = property.split(",");
            for (String esField : esFields) {
                mappings.add(new FieldMapping(esField, getBoostValueForField(esField)));
            }
        }

        return mappings;
    }

    private Properties createPropertiesFromFile(String fileName) {
        Properties properties = new Properties();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            properties.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }


}
