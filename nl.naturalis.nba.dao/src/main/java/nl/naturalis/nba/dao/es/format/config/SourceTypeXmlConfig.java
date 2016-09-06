//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.05 at 01:53:53 PM CEST 
//


package nl.naturalis.nba.dao.es.format.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SourceTypeXmlConfig.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SourceTypeXmlConfig">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="document"/>
 *     &lt;enumeration value="java-class"/>
 *     &lt;enumeration value="generic"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SourceTypeXmlConfig")
@XmlEnum
public enum SourceTypeXmlConfig {

    @XmlEnumValue("document")
    DOCUMENT("document"),
    @XmlEnumValue("java-class")
    JAVA_CLASS("java-class"),
    @XmlEnumValue("generic")
    GENERIC("generic");
    private final String value;

    SourceTypeXmlConfig(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SourceTypeXmlConfig fromValue(String v) {
        for (SourceTypeXmlConfig c: SourceTypeXmlConfig.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
