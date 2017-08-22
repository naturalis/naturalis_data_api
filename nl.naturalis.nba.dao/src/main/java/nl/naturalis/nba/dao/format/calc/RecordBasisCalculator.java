package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the recordBasis field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class RecordBasisCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity) throws CalculationException
	{
		Specimen specimen = (Specimen) entity.getDocument();
		if (specimen.getRecordBasis() == null) {
			return EMPTY_STRING;
		}
		if (specimen.getRecordBasis().contains("photo(copy) of herbarium sheet")
				|| specimen.getRecordBasis().contains("Illustration")
				|| specimen.getRecordBasis().contains("Photographs, negatives")
				|| specimen.getRecordBasis().contains("DNA sample from sheet")
				|| specimen.getRecordBasis().contains("Slides")
				|| specimen.getRecordBasis().contains("Observation")) {
			return EMPTY_STRING;
		}
		if (specimen.getRecordBasis() != null) {
			return "PreservedSpecimen";
		}
		return EMPTY_STRING;
	}
}
