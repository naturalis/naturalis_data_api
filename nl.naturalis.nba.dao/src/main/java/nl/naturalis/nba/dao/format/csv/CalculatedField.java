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
		Object value = calculator.calculateValue(entity);
		
		// HACK: if StringEscapeUtils.escapeCsv correctly implements CSV escaping,
		// this should not be necessary. However, GBIF doesn't like it
		String s = value.toString().replace('\n', ' ');
		s = value.toString().replace('\r', ' ');
		
		return escapeCsv(s);
	}

}
