package nl.naturalis.nba.dao.format.calc;

import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the taxonRank field in a DarwinCore archive for specimens.
 * Assumes the {@link EntityObject entity object} is a plain {@link Specimen}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonRankCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		SpecimenIdentification si = specimen.getIdentifications().iterator().next();
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
