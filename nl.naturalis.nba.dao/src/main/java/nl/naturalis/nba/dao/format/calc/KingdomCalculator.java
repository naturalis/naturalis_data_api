package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the kingdom field in a DarwinCore archive for specimens.
 * Assumes the {@link EntityObject entity object} is a plain {@link Specimen}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class KingdomCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
  {
  }

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		SpecimenIdentification si = specimen.getIdentifications().iterator().next();
		if (si.getDefaultClassification() == null) {
			return EMPTY_STRING;
		}
		String kingdom = si.getDefaultClassification().getKingdom();
		return kingdom == null ? EMPTY_STRING : kingdom;
	}

}
