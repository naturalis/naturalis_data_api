package nl.naturalis.nda.elasticsearch.dao.util;

/**
 * Contains a field name with it's corresponding boost value.
 *
 * @author Roberto van der Linden
 */
public class FieldMapping {

    private final String fieldName;
    private final Float boostValue;
    private final String value;

    public FieldMapping(String fieldName, Float boostValue, String value) {
        this.fieldName = fieldName;
        this.boostValue = boostValue;
        this.value = value;
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
}
