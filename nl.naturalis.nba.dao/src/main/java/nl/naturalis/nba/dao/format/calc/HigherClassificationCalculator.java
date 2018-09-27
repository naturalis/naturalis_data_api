package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the higherClassification field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class HigherClassificationCalculator implements ICalculator {

  @Override
	public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		SpecimenIdentification si = specimen.getIdentifications().iterator().next();
		DefaultClassification dc = si.getDefaultClassification();
		if (si.getDefaultClassification() == null) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder(100);
		if (specimen.getSourceSystem() == SourceSystem.XC) {
			append(sb, "Animalia");
		}
		else {
      append(sb, dc.getKingdom());
    }
    append(sb, dc.getClassName());
    append(sb, dc.getOrder());
    append(sb, dc.getFamily());
		return sb.toString();
	}

	private static void append(StringBuilder sb, String value)
	{
		if (value != null) {
			if (sb.length() != 0) {
				sb.append('|');
			}
			sb.append(value);
		}
	}
	
}
