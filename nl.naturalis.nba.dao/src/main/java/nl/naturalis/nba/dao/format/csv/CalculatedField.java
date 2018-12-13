package nl.naturalis.nba.dao.format.csv;

import java.net.URI;

import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FieldWriteException;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A {@code CalculatedField} uses an {@link ICalculator} instance to produce a
 * value for a CSV field.
 * 
 * @author Ayco Holleman
 *
 */
class CalculatedField extends AbstractField {

	private ICalculator calculator;

	CalculatedField(String name, URI term, Boolean isCoreId, ICalculator calculator)
	{
		super(name, term, isCoreId);
		this.calculator = calculator;
	}

	@Override
	public String getValue(EntityObject entity) throws FieldWriteException
	{
		Object value = calculator.calculateValue(entity);
		return value.toString();
	}

}
