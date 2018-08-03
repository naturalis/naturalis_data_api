package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxonRelationType implements INbaModelObject {

  HAS_FOOD_PLANT("has food plant"),
  IS_FOOD_PLANT_OF("is food plant of"),
  HAS_HOST("has host"),
  IS_HOST_OF("is host of"),
  HAS_HYPERPARASITE("has hyperparasite"),
  IS_HYPERPARASITE_OF("is hyperparasite of"),
  HAS_PARASITE("has parasite"),
  IS_PARASITE_OF("is parasite of"),
  HAS_SUBSTRATE("has substrate"),
  IS_SUBSTRATE_OF("is substrate of"),
  HAS_TRACE("has trace"),
  IS_TRACE_OF("is trace of"),
  HAS_BACKGROUND_SOUNDS("has background sounds"),
  IS_BACKGROUND_SOUND_OF("is background sound of"),
  IN_RELATION_WITH("in relation with");
  
  private String name;
  
  private TaxonRelationType(String name) {
    this.name = name;
  }

  @JsonCreator
  public static TaxonRelationType parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    for (TaxonRelationType relationType : TaxonRelationType.values()) {
      if (relationType.name.equalsIgnoreCase(name)) {
        return relationType;
      }
    }
    throw new IllegalArgumentException("Associated Taxon: Invalid relationType: \"" + name + "\"");
  }

  @JsonValue
  @Override
  public String toString()
  {
    return name;
  }

}
