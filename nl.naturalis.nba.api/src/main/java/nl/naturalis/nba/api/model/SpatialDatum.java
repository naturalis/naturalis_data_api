package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the reference system upon which the geographic coordinates
 * given in latitudeDecimal and longituDedecimal as based. 
 * 
 * For more information:
 * ABCD: {@linkplain https://terms.tdwg.org/wiki/abcd2:Gathering-SpatialDatum}, or
 * DWC: {@linkplain https://terms.tdwg.org/wiki/dwc:geodeticDatum}
 *
 */
public enum SpatialDatum implements INbaModelObject {

  WGS84, NAD83, NAD27;

  private final String name = name();

  @JsonCreator
  public static SpatialDatum parse(@JsonProperty("name") String name) {
    if (name == null) {
      return null;
    }
    for (SpatialDatum spatialDatum : SpatialDatum.values()) {
      if (spatialDatum.name.equals(name)) {
        return spatialDatum;
      }
    }
    throw new IllegalArgumentException("Invalid spatial datum: " + name);
  }

  @JsonValue
  @Override
  public String toString() {
    return name;
  }

}
