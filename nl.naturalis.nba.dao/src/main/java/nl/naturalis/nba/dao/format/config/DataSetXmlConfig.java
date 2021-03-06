//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.06 at 03:36:10 PM CET 
//


package nl.naturalis.nba.dao.format.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataSetXmlConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataSetXmlConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shared-data-source" type="{http://data.naturalis.nl/nba-dataset-config}DataSourceXmlConfig" minOccurs="0"/>
 *         &lt;element name="entity" type="{http://data.naturalis.nl/nba-dataset-config}EntityXmlConfig" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "dataset-config")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sharedDataSource",
    "entity"
})
public class DataSetXmlConfig {

    @XmlElement(name = "shared-data-source")
    protected DataSourceXmlConfig sharedDataSource;
    @XmlElement(required = true)
    protected List<EntityXmlConfig> entity;

    /**
     * Gets the value of the sharedDataSource property.
     * 
     * @return
     *     possible object is
     *     {@link DataSourceXmlConfig }
     *     
     */
    public DataSourceXmlConfig getSharedDataSource() {
        return sharedDataSource;
    }

    /**
     * Sets the value of the sharedDataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSourceXmlConfig }
     *     
     */
    public void setSharedDataSource(DataSourceXmlConfig value) {
        this.sharedDataSource = value;
    }

    /**
     * Gets the value of the entity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityXmlConfig }
     * 
     * 
     */
    public List<EntityXmlConfig> getEntity() {
        if (entity == null) {
            entity = new ArrayList<EntityXmlConfig>();
        }
        return this.entity;
    }

}
