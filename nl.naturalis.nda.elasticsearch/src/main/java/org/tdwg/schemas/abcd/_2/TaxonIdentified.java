//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.18 at 02:53:08 PM CET 
//


package org.tdwg.schemas.abcd._2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Scientific or informal name as a result of an identification.
 * 
 * <p>Java class for TaxonIdentified complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaxonIdentified">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HigherTaxa" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HigherTaxon" type="{http://www.tdwg.org/schemas/abcd/2.06}HigherTaxon" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ScientificName" type="{http://www.tdwg.org/schemas/abcd/2.06}ScientificNameIdentified" minOccurs="0"/>
 *           &lt;element name="InformalNameString" type="{http://www.tdwg.org/schemas/abcd/2.06}StringL" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="NameComments" type="{http://www.tdwg.org/schemas/abcd/2.06}StringL" minOccurs="0"/>
 *         &lt;element name="Code" type="{http://www.tdwg.org/schemas/abcd/2.06}CodeOfNomenclatureEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxonIdentified", propOrder = {
    "higherTaxa",
    "scientificName",
    "informalNameString",
    "nameComments",
    "code"
})
public class TaxonIdentified {

    @XmlElement(name = "HigherTaxa")
    protected TaxonIdentified.HigherTaxa higherTaxa;
    @XmlElement(name = "ScientificName")
    protected ScientificNameIdentified scientificName;
    @XmlElement(name = "InformalNameString")
    protected StringL informalNameString;
    @XmlElement(name = "NameComments")
    protected StringL nameComments;
    @XmlElement(name = "Code")
    protected CodeOfNomenclatureEnum code;

    /**
     * Gets the value of the higherTaxa property.
     * 
     * @return
     *     possible object is
     *     {@link TaxonIdentified.HigherTaxa }
     *     
     */
    public TaxonIdentified.HigherTaxa getHigherTaxa() {
        return higherTaxa;
    }

    /**
     * Sets the value of the higherTaxa property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxonIdentified.HigherTaxa }
     *     
     */
    public void setHigherTaxa(TaxonIdentified.HigherTaxa value) {
        this.higherTaxa = value;
    }

    /**
     * Gets the value of the scientificName property.
     * 
     * @return
     *     possible object is
     *     {@link ScientificNameIdentified }
     *     
     */
    public ScientificNameIdentified getScientificName() {
        return scientificName;
    }

    /**
     * Sets the value of the scientificName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScientificNameIdentified }
     *     
     */
    public void setScientificName(ScientificNameIdentified value) {
        this.scientificName = value;
    }

    /**
     * Gets the value of the informalNameString property.
     * 
     * @return
     *     possible object is
     *     {@link StringL }
     *     
     */
    public StringL getInformalNameString() {
        return informalNameString;
    }

    /**
     * Sets the value of the informalNameString property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringL }
     *     
     */
    public void setInformalNameString(StringL value) {
        this.informalNameString = value;
    }

    /**
     * Gets the value of the nameComments property.
     * 
     * @return
     *     possible object is
     *     {@link StringL }
     *     
     */
    public StringL getNameComments() {
        return nameComments;
    }

    /**
     * Sets the value of the nameComments property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringL }
     *     
     */
    public void setNameComments(StringL value) {
        this.nameComments = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link CodeOfNomenclatureEnum }
     *     
     */
    public CodeOfNomenclatureEnum getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeOfNomenclatureEnum }
     *     
     */
    public void setCode(CodeOfNomenclatureEnum value) {
        this.code = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="HigherTaxon" type="{http://www.tdwg.org/schemas/abcd/2.06}HigherTaxon" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "higherTaxon"
    })
    public static class HigherTaxa {

        @XmlElement(name = "HigherTaxon", required = true)
        protected List<HigherTaxon> higherTaxon;

        /**
         * Gets the value of the higherTaxon property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the higherTaxon property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHigherTaxon().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HigherTaxon }
         * 
         * 
         */
        public List<HigherTaxon> getHigherTaxon() {
            if (higherTaxon == null) {
                higherTaxon = new ArrayList<HigherTaxon>();
            }
            return this.higherTaxon;
        }

    }

}
