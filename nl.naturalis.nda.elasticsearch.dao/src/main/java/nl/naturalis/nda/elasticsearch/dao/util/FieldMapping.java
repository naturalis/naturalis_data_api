package nl.naturalis.nda.elasticsearch.dao.util;

/**
 * Contains a field name with it's corresponding boost value.
 *
 * @author Roberto van der Linden
 */
public class FieldMapping {

    private final String fieldName;
    private final Float boostValue;

    public FieldMapping(String fieldName, Float boostValue) {
        this.fieldName = fieldName;
        this.boostValue = boostValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Float getBoostValue() {
        return boostValue;
    }
}
