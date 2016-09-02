package nl.naturalis.nba.dao.es.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import nl.naturalis.nba.dao.es.format.EntityObject;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

class CalculatedField extends AbstractCsvField {

	private final ICalculator calculator;

	CalculatedField(String name, ICalculator calculator)
	{
		super(name);
		this.calculator = calculator;
	}

	@Override
	public String getValue(EntityObject esDocumentAsMap)
	{
		Object val = calculator.calculateValue(esDocumentAsMap);
		return escapeCsv(val.toString());
	}

}
