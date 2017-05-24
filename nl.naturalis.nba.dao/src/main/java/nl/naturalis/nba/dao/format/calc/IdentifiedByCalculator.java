package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

public class IdentifiedByCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		SpecimenCalculatorCache cache = SpecimenCalculatorCache.instance;
		SpecimenIdentification si = cache.getPreferredOrFirstIdentitifcation(entity);
		List<Agent> identifiers = si.getIdentifiers();
		if (identifiers == null) {
			return EMPTY_STRING;
		}
		Agent identifier = identifiers.iterator().next();
		if (identifier.getAgentText() == null) {
			return EMPTY_STRING;
		}
		return identifier.getAgentText().replaceAll("[,\\[\\]]", "");
	}
}
