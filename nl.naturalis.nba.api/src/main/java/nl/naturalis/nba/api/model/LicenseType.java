package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LicenseType  implements INbaModelObject {

  COPYRIGHT("Copyright");
  
  private String name;
  
  private LicenseType(String name) {
    this.name = name;
  }
  
  @JsonCreator
  public static LicenseType parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    if (COPYRIGHT.name.equalsIgnoreCase(name)) {
      return COPYRIGHT;
    }
    throw new IllegalArgumentException("Invalid license type: \"" + name + "\"");
  }
  
  @JsonValue
  @Override
  public String toString() 
  {
    return name;
  }
}
