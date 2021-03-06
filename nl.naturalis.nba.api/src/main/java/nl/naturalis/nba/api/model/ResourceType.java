package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the nature or genre of a resource.
 * 
 * More information: {@linkplain https://terms.tdwg.org/wiki/Audubon_Core_Term_List#dc:type}
 *
 */
public enum ResourceType implements INbaModelObject {
    
  COLLECTION ("Collection"), 
  DATASET ("Dataset"), 
  EVENT ("Event"), 
  IMAGE ("Image"), 
  INTERACTIVE_RESOURCE ("InteractiveResource"), 
  MOVING_IMAGE ("MovingImage"), 
  PHYSICAL_OBJECT ("PhysicalObject"), 
  SERVICE ("Service"), 
  SOFTWARE ("Software"), 
  SOUND ("Sound"), 
  STILL_IMAGE ("StillImage"), 
  TEXT ("Text");

  private String name;
  
  private ResourceType(String name) {
    this.name = name;
  }

  @JsonCreator
  public static ResourceType parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    for (ResourceType type : ResourceType.values()) {
      if (type.name.equalsIgnoreCase(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid type: \"" + name + "\"");
  }

  @JsonValue
  @Override
  public String toString()
  {
    return name;
  }

}
