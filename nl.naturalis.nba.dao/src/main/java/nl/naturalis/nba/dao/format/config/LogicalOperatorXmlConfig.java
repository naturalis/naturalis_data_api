//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.07 at 02:27:34 PM CEST 
//


package nl.naturalis.nba.dao.format.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LogicalOperatorXmlConfig.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LogicalOperatorXmlConfig">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AND"/>
 *     &lt;enumeration value="OR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LogicalOperatorXmlConfig")
@XmlEnum
public enum LogicalOperatorXmlConfig {

    AND,
    OR;

    public String value() {
        return name();
    }

    public static LogicalOperatorXmlConfig fromValue(String v) {
        return valueOf(v);
    }

}
