package nl.naturalis.nba.dao.format.calc;

import java.util.Map;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class SynonymIdCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		ScientificName synonym = (ScientificName) entity.getEntity();
		long hash = hash(specimen.getSourceSystemId());
		hash = (hash * 31) + hash(synonym.getFullScientificName());
		hash = (hash * 31) + hash(synonym.getTaxonomicStatus());
		return Long.toHexString(hash).toUpperCase();
	}

	private static int hash(Object obj)
	{
		return obj == null ? 0 : obj.hashCode();
	}

}
