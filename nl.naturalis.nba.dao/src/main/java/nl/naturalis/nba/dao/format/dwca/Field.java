package nl.naturalis.nba.dao.format.dwca;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;field&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
class Field {

  @XmlAttribute(name = "index")
  private String index;

  @XmlAttribute(name = "term")
  private String term;

  @XmlAttribute(name = "isCoreId")
  private Boolean isCoreId;

  Field() {}

  Field(int index, URI term) {
    this.index = String.valueOf(index);
    this.term = term.toString();
  }

  String getIndex() {
    return index;
  }

  void setIndex(String index) {
    this.index = index;
  }

  String getTerm() {
    return term;
  }

  void setTerm(String term) {
    this.term = term;
  }

  public Boolean isCoreId() {
    return isCoreId;
  }
  
  public void setIsCoreId(Boolean isCoreId) {
    this.isCoreId = isCoreId;
  }

}

