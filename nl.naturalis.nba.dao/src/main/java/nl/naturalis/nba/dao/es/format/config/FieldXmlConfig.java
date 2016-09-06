//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.05 at 01:53:53 PM CEST 
//


package nl.naturalis.nba.dao.es.format.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FieldXmlConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FieldXmlConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="path" type="{http://data.naturalis.nl/nba-dataset-config}PathXmlConfig"/>
 *           &lt;element name="constant" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="calculator" type="{http://data.naturalis.nl/nba-dataset-config}CalculatorXmlConfig"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FieldXmlConfig", propOrder = {
    "name",
    "path",
    "constant",
    "calculator"
})
public class FieldXmlConfig {

    @XmlElement(required = true)
    protected String name;
    protected PathXmlConfig path;
    protected String constant;
    protected CalculatorXmlConfig calculator;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link PathXmlConfig }
     *     
     */
    public PathXmlConfig getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathXmlConfig }
     *     
     */
    public void setPath(PathXmlConfig value) {
        this.path = value;
    }

    /**
     * Gets the value of the constant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConstant() {
        return constant;
    }

    /**
     * Sets the value of the constant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConstant(String value) {
        this.constant = value;
    }

    /**
     * Gets the value of the calculator property.
     * 
     * @return
     *     possible object is
     *     {@link CalculatorXmlConfig }
     *     
     */
    public CalculatorXmlConfig getCalculator() {
        return calculator;
    }

    /**
     * Sets the value of the calculator property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalculatorXmlConfig }
     *     
     */
    public void setCalculator(CalculatorXmlConfig value) {
        this.calculator = value;
    }

}
