package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the longitude field in a DarwinCore archive for specimens.
 * Assumes the {@link EntityObject entity object} is a plain {@link Specimen}
 * document.
 * 
 * @author Ayco Holleman
 *
 */
public class LongitudeCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		if (specimen.getGatheringEvent() == null) {
			return EMPTY_STRING;
		}
		List<GatheringSiteCoordinates> coords = specimen.getGatheringEvent().getSiteCoordinates();
		if (coords == null || coords.size() == 0) {
			return EMPTY_STRING;
		}
		Double lon = coords.iterator().next().getLongitudeDecimal();
		return lon == null ? EMPTY_STRING : lon;
	}

}
