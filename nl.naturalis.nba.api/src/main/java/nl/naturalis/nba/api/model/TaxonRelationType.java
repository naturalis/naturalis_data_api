package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxonRelationType implements INbaModelObject {

  HAS_FOOD_PLANT("has food plant"),
  HAS_HOST("has host"),
  HAS_HYPERPARASITE("has hyperparasite"),
  HAS_PARASITE("has parasite"),
  HAS_SUBSTRATE("has substrate"),
  IS_SUBSTRATE_OF("is substrate of"),
  HAS_TRACE("has trace"),
  IS_TRACE_OF("is trace of"),
  HAS_BACKGROUND_SOUNDS("has background sounds"),
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
    throw new IllegalArgumentException("Invalid relationType: \"" + name + "\" (Associated Taxon))");
  }

  @JsonValue
  @Override
  public String toString()
  {
    return name;
  }

}
