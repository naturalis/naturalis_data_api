//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.05 at 01:53:53 PM CEST 
//


package nl.naturalis.nba.dao.es.format.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataSetsXmlConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataSetsXmlConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://data.naturalis.nl/nba-dataset-config}SourceXmlConfig" minOccurs="0"/>
 *         &lt;element name="entities" type="{http://data.naturalis.nl/nba-dataset-config}EntityXmlConfig" maxOccurs="unbounded"/>
 *         &lt;element name="datasets" type="{http://data.naturalis.nl/nba-dataset-config}DataSetXmlConfig" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataSetsXmlConfig", propOrder = {
    "source",
    "entities",
    "datasets"
})
public class DataSetsXmlConfig {

    protected SourceXmlConfig source;
    @XmlElement(required = true)
    protected List<EntityXmlConfig> entities;
    @XmlElement(required = true)
    protected List<DataSetXmlConfig> datasets;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link SourceXmlConfig }
     *     
     */
    public SourceXmlConfig getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceXmlConfig }
     *     
     */
    public void setSource(SourceXmlConfig value) {
        this.source = value;
    }

    /**
     * Gets the value of the entities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityXmlConfig }
     * 
     * 
     */
    public List<EntityXmlConfig> getEntities() {
        if (entities == null) {
            entities = new ArrayList<EntityXmlConfig>();
        }
        return this.entities;
    }

    /**
     * Gets the value of the datasets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataSetXmlConfig }
     * 
     * 
     */
    public List<DataSetXmlConfig> getDatasets() {
        if (datasets == null) {
            datasets = new ArrayList<DataSetXmlConfig>();
        }
        return this.datasets;
    }

}
