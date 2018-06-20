package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import static nl.naturalis.nba.api.annotations.Analyzer.KEYWORD;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.naturalis.nba.api.annotations.Analyzers;

/**
 * Class representing an atomised place name. Contains name and category of an 
 * administrative or geoecological area.
 * More information: {@linkplain https://terms.tdwg.org/wiki/abcd2:Gathering-NamedArea}
 *
 */
public class NamedArea implements INbaModelObject {

  @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
  private String areaName;
  
  @Analyzers({ KEYWORD, CASE_INSENSITIVE })
  private AreaClass areaClass;
  
  @JsonCreator
  public NamedArea(@JsonProperty("areaClass") AreaClass areaClass, @JsonProperty("areaName") String areaName) {
    if (areaClass == null) {
      throw new IllegalArgumentException("AreaClass in NamedArea cannot be null");
    }
    if (areaName == null) {
      throw new IllegalArgumentException("AreaName in NamedArea cannot be null");
    }
    this.areaClass = areaClass;
    this.areaName = areaName;
  }
  
  public String getAreaName() {
    return areaName;
  }
  
  public String getAreaClass() {
    return areaClass.name();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NamedArea) {
      NamedArea other = (NamedArea) obj;
      if (this.areaName.equals(other.getAreaName()) && this.getAreaClass().equals(other.getAreaClass())) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return areaName.hashCode() + 7 * areaClass.toString().hashCode();
  }

}
