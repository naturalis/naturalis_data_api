package nl.naturalis.nba.dao.format.csv;

import java.net.URI;

import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FieldWriteException;
import nl.naturalis.nba.dao.format.ICalculator;

class CalculatedField extends AbstractField {

	private final ICalculator calculator;

	CalculatedField(String name, URI term, ICalculator calculator)
	{
		super(name, term);
		this.calculator = calculator;
	}

	@Override
	public String getValue(EntityObject entity) throws FieldWriteException
	{
		Object value = calculator.calculateValue(entity);
		return value.toString();
	}

}
