//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.07 at 03:21:18 PM CEST 
//


package nl.naturalis.nba.dao.es.format.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataSourceXmlConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataSourceXmlConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="document" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="java-class" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="query-spec" type="{http://data.naturalis.nl/nba-dataset-config}QuerySpecXmlConfig" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataSourceXmlConfig", propOrder = {
    "document",
    "javaClass",
    "path",
    "querySpec"
})
public class DataSourceXmlConfig {

    protected String document;
    @XmlElement(name = "java-class")
    protected String javaClass;
    protected String path;
    @XmlElement(name = "query-spec")
    protected QuerySpecXmlConfig querySpec;

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocument(String value) {
        this.document = value;
    }

    /**
     * Gets the value of the javaClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJavaClass() {
        return javaClass;
    }

    /**
     * Sets the value of the javaClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJavaClass(String value) {
        this.javaClass = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the querySpec property.
     * 
     * @return
     *     possible object is
     *     {@link QuerySpecXmlConfig }
     *     
     */
    public QuerySpecXmlConfig getQuerySpec() {
        return querySpec;
    }

    /**
     * Sets the value of the querySpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuerySpecXmlConfig }
     *     
     */
    public void setQuerySpec(QuerySpecXmlConfig value) {
        this.querySpec = value;
    }

}
