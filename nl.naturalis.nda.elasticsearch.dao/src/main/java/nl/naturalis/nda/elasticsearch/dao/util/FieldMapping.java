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
    private boolean fromAlias;
    private String aliasName;

    public FieldMapping(String fieldName, Float boostValue, String value, String nestedPath, Boolean hasNGram, boolean fromAlias, String aliasName) {
        this.fieldName = fieldName;
        this.boostValue = boostValue;
        this.value = value;
        this.nestedPath = nestedPath;
        this.hasNGramField = hasNGram;
        this.fromAlias = fromAlias;
        this.aliasName = aliasName;
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

    public boolean isFromAlias() {
        return fromAlias;
    }

    public void setFromAlias(boolean fromAlias) {
        this.fromAlias = fromAlias;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMapping that = (FieldMapping) o;

        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
