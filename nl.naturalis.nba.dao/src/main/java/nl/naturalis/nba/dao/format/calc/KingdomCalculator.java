package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class KingdomCalculator implements ICalculator {

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
		String kingdom = si.getDefaultClassification().getKingdom();
		if (specimen.getSourceSystem() == SourceSystem.BRAHMS) {
			if (kingdom == null || !kingdom.toLowerCase().contains("fungi")) {
				return "Plantae";
			}
			return "Fungi";
		}
		return kingdom == null ? EMPTY_STRING : kingdom;
	}

}
