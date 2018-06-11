package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;
import static nl.naturalis.nba.api.annotations.Analyzer.KEYWORD;
import java.util.Objects;
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
    this.name = Objects.requireNonNull(name, "Associated taxon name cannot be empty or null");
    this.relationType = Objects.requireNonNull(relationType, "Associated taxon relationType cannot be empty or null");
  }
  
  public String getName() {
    return name;
  }
  
  public String getRelationType() {
    return relationType.toString();
  }
  
}
