package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import java.util.Objects;
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
  
  @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
  private AreaClass areaClass;
  
  @JsonCreator
  public NamedArea(@JsonProperty("areaClass") AreaClass areaClass, @JsonProperty("areaName") String areaName) {
    this.areaClass = Objects.requireNonNull(areaClass, "areaClass may not be null");
    this.areaName = areaName;
  }
  
  public String getAreaName() {
    return areaName;
  }
  
  public String getAreaClass() {
    return areaClass.name();
  }

}
