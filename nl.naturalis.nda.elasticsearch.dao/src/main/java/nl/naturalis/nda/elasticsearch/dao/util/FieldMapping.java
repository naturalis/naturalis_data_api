package nl.naturalis.nda.elasticsearch.dao.util;

/**
 * Contains a field name with it's corresponding value, boost factor and indication if it is a nested field.
 *
 * @author Roberto van der Linden
 */
public class FieldMapping {

    private final String fieldName;
    private final Float boostValue;
    private final String value;
    private final String nestedPath;
    private final Boolean hasNGramField;

    public FieldMapping(String fieldName, Float boostValue, String value, String nestedPath, Boolean hasNGram) {
        this.fieldName = fieldName;
        this.boostValue = boostValue;
        this.value = value;
        this.nestedPath = nestedPath;
        this.hasNGramField = hasNGram;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Float getBoostValue() {
        return boostValue;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get the nested path of the property.
     */
    public String getNestedPath() {
        return nestedPath;
    }

    public Boolean hasNGram() {
        return hasNGramField;
    }
}
