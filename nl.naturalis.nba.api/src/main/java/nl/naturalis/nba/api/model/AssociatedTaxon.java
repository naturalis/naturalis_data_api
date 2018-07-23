package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.KEYWORD;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.naturalis.nba.api.annotations.Analyzers;

public class AssociatedTaxon implements INbaModelObject {

  @Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
  private String name;
  
  @Analyzers({ KEYWORD, CASE_INSENSITIVE })
  private TaxonRelationType relationType;
  
  @JsonCreator
  public AssociatedTaxon(@JsonProperty("name") String name, @JsonProperty("relationType") TaxonRelationType relationType) {
    if (name == null) {
      throw new IllegalArgumentException("Name of AssociatedTaxon cannot be null");
    }
    this.name = name;
    if (relationType == null) {
      throw new IllegalArgumentException("TaxonRelationType of AssociatedTaxon cannot be null");
    }
    this.relationType = relationType;
  }
  
  public String getName() {
    return name;
  }
  
  public String getRelationType() {
    return relationType.toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AssociatedTaxon) {
      AssociatedTaxon other = (AssociatedTaxon) obj;
      if (this.name.equals(other.getName()) && this.relationType.toString().equals(other.getRelationType().toString())) {
        return true;        
      }
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return name.hashCode() + 7 * relationType.toString().hashCode();
  }
  
}
