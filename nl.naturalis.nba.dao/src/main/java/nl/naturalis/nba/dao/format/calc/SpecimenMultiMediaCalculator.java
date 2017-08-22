package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the associatedMedia field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenMultiMediaCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		List<ServiceAccessPoint> saps = specimen.getAssociatedMultiMediaUris();
		if (saps == null) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder(80 * saps.size());
		int i = 0;
		for (ServiceAccessPoint sap : saps) {
			if (i++ != 0) {
				sb.append('|');
			}
			sb.append(sap.getAccessUri());
		}
		return sb.toString();
	}

}
