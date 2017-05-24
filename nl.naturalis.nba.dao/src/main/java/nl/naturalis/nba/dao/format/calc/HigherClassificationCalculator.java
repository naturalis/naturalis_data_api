package nl.naturalis.nba.dao.format.calc;

import java.util.Map;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class HigherClassificationCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		SpecimenCalculatorCache cache = SpecimenCalculatorCache.instance;
		Specimen specimen = cache.getSpecimen(entity);
		SpecimenIdentification si = cache.getPreferredOrFirstIdentitifcation(entity);
		DefaultClassification dc = si.getDefaultClassification();
		StringBuilder sb = new StringBuilder(100);
		if (specimen.getSourceSystem() == SourceSystem.CRS) {
			append(sb, dc.getKingdom());
			append(sb, dc.getClassName());
			append(sb, dc.getOrder());
			append(sb, dc.getFamily());
		}
		else {
			String kingdom = dc.getKingdom();
			if (kingdom != null && kingdom.toLowerCase().contains("fungi")) {
				append(sb, "fungi");
			}
			else {
				append(sb, "Plantae");
			}
			append(sb, dc.getClassName());
			append(sb, dc.getOrder());
			append(sb, dc.getFamily());
		}
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
