package nl.naturalis.nba.dao.es.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.util.Map;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

class CalculatedField extends AbstractCsvField {

	private final ICalculator calculator;

	CalculatedField(String name, ICalculator calculator)
	{
		super(name);
		this.calculator = calculator;
	}

	@Override
	public String getValue(Map<String, Object> esDocumentAsMap)
	{
		Object val = calculator.calculateValue(esDocumentAsMap);
		return escapeCsv(val.toString());
	}

}
