package nl.naturalis.nba.dao.format.calc;

import java.util.Map;

import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class TaxonRankCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		SpecimenCalculatorCache cache = SpecimenCalculatorCache.instance;
		SpecimenIdentification si = cache.getPreferredOrFirstIdentitifcation(entity);
		switch (si.getTaxonRank()) {
			case "var.":
				return "variety";
			case "subsp.":
				return "subspecies";
			case "f":
				return "form";
			default:
				return si.getTaxonRank();
		}
	}

}
