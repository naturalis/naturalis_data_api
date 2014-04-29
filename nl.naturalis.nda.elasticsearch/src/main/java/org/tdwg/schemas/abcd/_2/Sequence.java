//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.18 at 02:53:08 PM CET 
//


package org.tdwg.schemas.abcd._2;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A first proposal for this type of data. Note that this is used in the context of a collection unit and thus automatically refers to that unit.
 * 
 * <p>Java class for Sequence complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Sequence">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Database" type="{http://www.tdwg.org/schemas/abcd/2.06}StringL"/>
 *         &lt;element name="ID-in-Database" type="{http://www.tdwg.org/schemas/abcd/2.06}String"/>
 *         &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Method" type="{http://www.tdwg.org/schemas/abcd/2.06}StringL" minOccurs="0"/>
 *         &lt;element name="SequencedPart" type="{http://www.tdwg.org/schemas/abcd/2.06}StringL" minOccurs="0"/>
 *         &lt;element name="Reference" type="{http://www.tdwg.org/schemas/abcd/2.06}Reference" minOccurs="0"/>
 *         &lt;element name="SequencingAgent" type="{http://www.tdwg.org/schemas/abcd/2.06}Contact" minOccurs="0"/>
 *         &lt;element name="Length" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sequence", propOrder = {
    "database",
    "idInDatabase",
    "uri",
    "method",
    "sequencedPart",
    "reference",
    "sequencingAgent",
    "length"
})
public class Sequence {

    @XmlElement(name = "Database", required = true)
    protected StringL database;
    @XmlElement(name = "ID-in-Database", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String idInDatabase;
    @XmlElement(name = "URI")
    @XmlSchemaType(name = "anyURI")
    protected String uri;
    @XmlElement(name = "Method")
    protected StringL method;
    @XmlElement(name = "SequencedPart")
    protected StringL sequencedPart;
    @XmlElement(name = "Reference")
    protected Reference reference;
    @XmlElement(name = "SequencingAgent")
    protected Contact sequencingAgent;
    @XmlElement(name = "Length")
    protected BigInteger length;

    /**
     * Gets the value of the database property.
     * 
     * @return
     *     possible object is
     *     {@link StringL }
     *     
     */
    public StringL getDatabase() {
        return database;
    }

    /**
     * Sets the value of the database property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringL }
     *     
     */
    public void setDatabase(StringL value) {
        this.database = value;
    }

    /**
     * Gets the value of the idInDatabase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDInDatabase() {
        return idInDatabase;
    }

    /**
     * Sets the value of the idInDatabase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDInDatabase(String value) {
        this.idInDatabase = value;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURI(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link StringL }
     *     
     */
    public StringL getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringL }
     *     
     */
    public void setMethod(StringL value) {
        this.method = value;
    }

    /**
     * Gets the value of the sequencedPart property.
     * 
     * @return
     *     possible object is
     *     {@link StringL }
     *     
     */
    public StringL getSequencedPart() {
        return sequencedPart;
    }

    /**
     * Sets the value of the sequencedPart property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringL }
     *     
     */
    public void setSequencedPart(StringL value) {
        this.sequencedPart = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setReference(Reference value) {
        this.reference = value;
    }

    /**
     * Gets the value of the sequencingAgent property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getSequencingAgent() {
        return sequencingAgent;
    }

    /**
     * Sets the value of the sequencingAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setSequencingAgent(Contact value) {
        this.sequencingAgent = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLength(BigInteger value) {
        this.length = value;
    }

}
