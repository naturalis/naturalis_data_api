package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the Creative Commons Licenses.
 * 
 * More information: {@linkplain https://creativecommons.org/}
 *
 */
public enum License implements INbaModelObject {
  CCBY     ("CC BY"),
  CCBYSA   ("CC BY-SA"), 
  CCBYND   ("CC BY-ND"),
  CCBYNC   ("CC BY-NC"),
  CCBYNCSA ("CC BY-NC-SA"),
  CCBYNCND ("CC BY-NC-ND"),
  CC0      ("CC0"),
  PUBLICDOMAIN ("Public Domain Mark");

  private String name;
  
  License(String name) {
    this.name = name;
  }
  
  @JsonCreator
  public static License parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    for (License type : License.values()) {
      if (type.name.equals(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid license type: \"" + name + "\"");
  }
  
  @JsonValue
  @Override
  public String toString() {
    return name;
  }
  
}
