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
    private final boolean nested;

    public FieldMapping(String fieldName, Float boostValue, String value, boolean nested) {
        this.fieldName = fieldName;
        this.boostValue = boostValue;
        this.value = value;
        this.nested = nested;
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

    public boolean isNested() {
        return nested;
    }

    /**
     * Get the nested path of the property.
     */
    public String getNestedPath(){
        int lastPathIndex = fieldName.lastIndexOf(".");
        return fieldName.substring(0, lastPathIndex);
    }

}
