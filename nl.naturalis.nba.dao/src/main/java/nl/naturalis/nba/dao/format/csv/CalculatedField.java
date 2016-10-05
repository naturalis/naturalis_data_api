package nl.naturalis.nba.dao.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

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
		Object val = calculator.calculateValue(entity);
		return escapeCsv(val.toString());
	}

}
