package nl.naturalis.nba.dao.format.calc;

import java.util.Map;
import java.util.List;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * The PreviousIdentificationsCalculator extracts and concatenates the value(s) from the field
 * identifications.scientificName.fullScientificName for all non-preferred (!) identifications a
 * specimen document ("preferred" : false).
 *
 */
public class PreviousIdentificationsCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args)
      throws CalculatorInitializationException {
    if (docType != Specimen.class) {
      throw new CalculatorInitializationException(
          "PreviousIdentificationsCalculator can only be used with specimen documents");
    }
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    Specimen specimen = (Specimen) entity.getDocument();
    String previousIdentifications = "";

    List<SpecimenIdentification> identifications = specimen.getIdentifications();
    if (identifications == null) {
      return "";
    }

    for (SpecimenIdentification identification : identifications) {
      if (identification.isPreferred() == false) {
        ScientificName scientificName = identification.getScientificName();
        if (scientificName != null && scientificName.getFullScientificName() != null) {
          if (previousIdentifications.length() > 0)
            previousIdentifications += " | ";
          previousIdentifications += scientificName.getFullScientificName();
        }
      }
    }
    return previousIdentifications;
  }

}
