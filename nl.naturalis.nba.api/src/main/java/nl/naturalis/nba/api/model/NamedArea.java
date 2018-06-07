package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import nl.naturalis.nba.api.annotations.Analyzers;

public class NamedArea implements INbaModelObject {

  @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
  private String areaName;
  @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
  private String areaClass;
  
  public String getAreaName() {
    return areaName;
  }
  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }
  public String getAreaClass() {
    return areaClass;
  }
  public void setAreaClass(String areaClass) {
    this.areaClass = areaClass;
  }

}
