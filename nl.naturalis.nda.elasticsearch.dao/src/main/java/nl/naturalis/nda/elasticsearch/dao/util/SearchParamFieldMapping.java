package nl.naturalis.nda.elasticsearch.dao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Provides functionality to get a mapping for specific field or NBA alias.
 *
 * @author Roberto van der Linden
 */
public class SearchParamFieldMapping {

    public static final String BOOST_SUFFIX = ".boost";
    public static final String NESTED_SUFFIX = ".nested";
    private static final List<String> EXCLUDED_PARAMS = new ArrayList<>();
    static {
        EXCLUDED_PARAMS.add("_search");
        EXCLUDED_PARAMS.add("_fields");
        EXCLUDED_PARAMS.add("_andOr");
        EXCLUDED_PARAMS.add("_source");
        EXCLUDED_PARAMS.add("_sort");
        EXCLUDED_PARAMS.add("_sortDirection");
        EXCLUDED_PARAMS.add("_maxResults");
        EXCLUDED_PARAMS.add("_offset");
    }

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
    public List<FieldMapping> getSpecimenMappingForField(String field, String value) {
        return getMappingForField(field, value, specimenProperties);
    }

    /**
     * Calls {@link #getSpecimenMappingForField(String, String)} for all given values and aggregates the results.
     */
    public List<FieldMapping> getSpecimenMappingForFields(QueryParams queryParams) {
        List<FieldMapping> fieldMappings = new ArrayList<>();
        for (String paramKey : queryParams.keySet()) {
            if (!EXCLUDED_PARAMS.contains(paramKey)) {
                fieldMappings.addAll(getMappingForField(paramKey, queryParams.getParam(paramKey), specimenProperties));
            }
        }
        return fieldMappings;
    }

    /**
     * Get the mapping for a taxon field or alias.
     *
     * @param field a taxon field or alias
     * @return a list of all found mappings, including their boost value
     */
    public List<FieldMapping> getTaxonMappingForField(String field, String value) {
        return getMappingForField(field, value, taxonProperties);
    }

    /**
     * Get the mapping for a multimedia field or alias.
     *
     * @param field a multimedia field or alias
     * @return a list of all found mappings, including their boost value
     */
    public List<FieldMapping> getMultimediaMappingForField(String field, String value) {
        return getMappingForField(field, value, multimediaProperties);
    }

    /**
     * Get the boost value for the given field.
     *
     * @param field the field to find the boost value for
     * @return the nested value if property found, null otherwise
     */
    private boolean getNestedValueForField(String field, Properties properties) {
        String property = properties.getProperty(field + NESTED_SUFFIX);
        if (property != null && !property.isEmpty()) {
            return Boolean.valueOf(property);
        }

        return false;
    }

    /**
     * Get the nested value for the given field.
     *
     * @param field the field to find the boost value for
     * @return the boost value if property found, null otherwise
     */
    private Float getBoostValueForField(String field, Properties properties) {
        String property = properties.getProperty(field + BOOST_SUFFIX);
        if (property != null && !property.isEmpty()) {
            return Float.valueOf(property);
        }

        return null;
    }

    private List<FieldMapping> getMappingForField(String field, String value, Properties properties) {
        List<FieldMapping> mappings = new ArrayList<>();

        List<String> esFields = extractPropertyForField(field, properties);

        for (String esField : esFields) {
            Float boostValue = getBoostValueForField(esField, properties);
            boolean nested = getNestedValueForField(esField, properties);
            mappings.add(new FieldMapping(esField, boostValue, value, nested));
        }

        return mappings;
    }

    private List<String> extractPropertyForField(String field, Properties properties) {
        String property = properties.getProperty(field);
        if (property != null && !property.isEmpty()) {
            String[] splitted = property.split(",");
            return Arrays.asList(splitted);
        }

        // No properties found, return the given field
        return Arrays.asList(field);
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
