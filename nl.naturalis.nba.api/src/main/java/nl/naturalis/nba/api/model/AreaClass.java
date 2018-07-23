package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing an area classification. It Allows the recording of classification 
 * categories for the class of the gathering named area (local or national subdivision 
 * levels, geomorphological units, protected areas, etc.)
 * 
 * More information: {@linkplain https://terms.tdwg.org/wiki/abcd2:Gathering-NamedArea-AreaClass}
 *
 */
public enum AreaClass implements INbaModelObject {
  
  CONTINENT("continent"),
  COUNTY("county"),
  HIGHERGEOGRAPHY("higherGeography"), 
  ISLANDGROUP("islandGroup"),
  MUNICIPALITY("municipality"),
  WATERBODY("waterBody");

  private String name;
  
  private AreaClass(String name) {
    this.name = name;
  }

  @JsonCreator
  public static AreaClass parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    for (AreaClass areaClass : AreaClass.values()) {
      if (areaClass.name.equalsIgnoreCase(name)) {
        return areaClass;
      }
    }
    throw new IllegalArgumentException("Invalid areaClass: \"" + name + "\"");
  }

  @JsonValue
  @Override
  public String toString()
  {
    return name;
  }

}
