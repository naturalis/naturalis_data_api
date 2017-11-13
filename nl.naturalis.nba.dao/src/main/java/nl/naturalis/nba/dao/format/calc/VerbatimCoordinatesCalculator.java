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
 * A calculator for the verbatimCoordinates field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimCoordinatesCalculator implements ICalculator {

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
		if (coords == null || coords.size() == 1) {
			return EMPTY_STRING;
		}
		StringBuilder lats = new StringBuilder(80);
		StringBuilder lons = new StringBuilder(80);
		int i = 0;
		for (GatheringSiteCoordinates coord : coords) {
			if (i++ != 0) {
				lats.append(',');
				lons.append(',');
			}
			lats.append(coord.getLatitudeDecimal());
			lons.append(coord.getLongitudeDecimal());
		}
		lats.append('|').append(lons);
		return lats.toString();
	}

}
