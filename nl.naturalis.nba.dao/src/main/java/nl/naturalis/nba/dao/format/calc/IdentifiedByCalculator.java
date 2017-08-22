package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the identifiedBy field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class IdentifiedByCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		SpecimenIdentification si = specimen.getIdentifications().iterator().next();
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
