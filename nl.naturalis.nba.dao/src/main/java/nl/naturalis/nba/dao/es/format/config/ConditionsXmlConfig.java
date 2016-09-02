//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.02 at 02:53:42 PM CEST 
//


package nl.naturalis.nba.dao.es.format.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConditionsXmlConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionsXmlConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="condition" type="{http://data.naturalis.nl/nba-dataset-config}ConditionXmlConfig" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operator" type="{http://data.naturalis.nl/nba-dataset-config}LogicalOperatorXmlConfig" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionsXmlConfig", propOrder = {
    "condition"
})
public class ConditionsXmlConfig {

    @XmlElement(required = true)
    protected List<ConditionXmlConfig> condition;
    @XmlAttribute(name = "operator")
    protected LogicalOperatorXmlConfig operator;

    /**
     * Gets the value of the condition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the condition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConditionXmlConfig }
     * 
     * 
     */
    public List<ConditionXmlConfig> getCondition() {
        if (condition == null) {
            condition = new ArrayList<ConditionXmlConfig>();
        }
        return this.condition;
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link LogicalOperatorXmlConfig }
     *     
     */
    public LogicalOperatorXmlConfig getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link LogicalOperatorXmlConfig }
     *     
     */
    public void setOperator(LogicalOperatorXmlConfig value) {
        this.operator = value;
    }

}
