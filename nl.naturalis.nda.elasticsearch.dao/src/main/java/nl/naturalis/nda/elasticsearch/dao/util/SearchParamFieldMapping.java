//
package nl.naturalis.nda.elasticsearch.dao.util;

import nl.naturalis.nba.api.search.QueryParams;

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
    public static final String NESTED_SUFFIX = ".nested.path";
    public static final String NGRAM_SUFFIX = ".ngram";

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
        EXCLUDED_PARAMS.add("_geoShape");
        EXCLUDED_PARAMS.add("_groupMaxResults");
        EXCLUDED_PARAMS.add("_groupSort");
        EXCLUDED_PARAMS.add("_groupSortDirection");
        EXCLUDED_PARAMS.add("_groupOffset");
        EXCLUDED_PARAMS.add("_SESSION_ID");
        EXCLUDED_PARAMS.add("_session_id");
        EXCLUDED_PARAMS.add("_jsonError");
        EXCLUDED_PARAMS.add("_showMap");
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
     * Calls {@link #getMappingsForFields(QueryParams, java.util.Properties)} with the specimen properties for all
     * given values and aggregates the results.
     */
    public List<FieldMapping> getSpecimenMappingForFields(QueryParams queryParams) {
        return getMappingsForFields(queryParams, specimenProperties);
    }

    /**
     * Calls {@link #getMappingsForFields(QueryParams, java.util.Properties)} with the taxon properties for all
     * given values and aggregates the results.
     */
    public List<FieldMapping> getTaxonMappingForFields(QueryParams queryParams) {
        return getMappingsForFields(queryParams, taxonProperties);
    }

    /**
     * Calls {@link #getMappingsForFields(QueryParams, java.util.Properties)} with the multimedia properties for all
     * given values and aggregates the results.
     */
    public List<FieldMapping> getMultimediaMappingForFields(QueryParams queryParams) {
        return getMappingsForFields(queryParams, multimediaProperties);
    }

    private List<FieldMapping> getMappingsForFields(QueryParams queryParams, Properties properties) {
        List<FieldMapping> fieldMappings = new ArrayList<>();
        for (String paramKey : queryParams.keySet()) {
            if (!EXCLUDED_PARAMS.contains(paramKey)) {
                fieldMappings.addAll(getMappingForField(paramKey, queryParams.getParam(paramKey), properties));
            }
        }
        return fieldMappings;
    }

    /**
     * Get the boost value for the given field.
     *
     * @param field the field to find the boost value for
     * @return the nested value if property found, null otherwise
     */
    private String getNestedPathValueForField(String field, Properties properties) {
        String property = properties.getProperty(field + NESTED_SUFFIX);
        if (property != null && !property.trim().isEmpty()) {
            return property;
        }

        return null;
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
            String nestedPath = getNestedPathValueForField(esField, properties);
            Boolean hasNGramField = getNGramValueForField(esField, properties);
            boolean fromAlias = false;
            String aliasName = null;
            if (!esField.equalsIgnoreCase(field)) {
                fromAlias = true;
                aliasName = field;
            }
            mappings.add(new FieldMapping(esField, boostValue, value, nestedPath, hasNGramField, fromAlias, aliasName));
        }

        return mappings;
    }

    private Boolean getNGramValueForField(String field, Properties properties) {
        String property = properties.getProperty(field + NGRAM_SUFFIX);
        if (property != null && !property.isEmpty()) {
            return Boolean.valueOf(property);
        }

        return null;
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
