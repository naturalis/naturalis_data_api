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
  CCBY        ("CC BY"),
  CCBYSA      ("CC BY-SA"), 
  CCBYND      ("CC BY-ND"),
  CCBYNC      ("CC BY-NC"),
  CCBYNCSA    ("CC BY-NC-SA"),
  CCBYNCND    ("CC BY-NC-ND"),
  CC0         ("CC0"),
  
  CCBY_10     ("CC BY 1.0"),
  CCBYSA_10   ("CC BY-SA 1.0"), 
  CCBYND_10   ("CC BY-ND 1.0"),
  CCBYNC_10   ("CC BY-NC 1.0"),
  CCBYNCSA_10 ("CC BY-NC-SA 1.0"),
  CCBYNCND_10 ("CC BY-NC-ND 1.0"),
  CC0_10      ("CC0 1.0"),
  
  CCBY_20     ("CC BY 2.0"),
  CCBYSA_20   ("CC BY-SA 2.0"), 
  CCBYND_20   ("CC BY-ND 2.0"),
  CCBYNC_20   ("CC BY-NC 2.0"),
  CCBYNCSA_20 ("CC BY-NC-SA 2.0"),
  CCBYNCND_20 ("CC BY-NC-ND 2.0"),
  
  CCBY_25     ("CC BY 2.5"),
  CCBYSA_25   ("CC BY-SA 2.5"), 
  CCBYND_25   ("CC BY-ND 2.5"),
  CCBYNC_25   ("CC BY-NC 2.5"),
  CCBYNCSA_25 ("CC BY-NC-SA 2.5"),
  CCBYNCND_25 ("CC BY-NC-ND 2.5"),
  
  CCBY_30     ("CC BY 3.0"),
  CCBYSA_30   ("CC BY-SA 3.0"), 
  CCBYND_30   ("CC BY-ND 3.0"),
  CCBYNC_30   ("CC BY-NC 3.0"),
  CCBYNCSA_30 ("CC BY-NC-SA 3.0"),
  CCBYNCND_30 ("CC BY-NC-ND 3.0"),
  
  CCBY_40     ("CC BY 4.0"),
  CCBYSA_40   ("CC BY-SA 4.0"), 
  CCBYND_40   ("CC BY-ND 4.0"),
  CCBYNC_40   ("CC BY-NC 4.0"),
  CCBYNCSA_40 ("CC BY-NC-SA 4.0"),
  CCBYNCND_40 ("CC BY-NC-ND 4.0"),
  
  ALL_RIGHTS_RESERVED ("All rights reserved") ;

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
