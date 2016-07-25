package nl.naturalis.nba.dao.es.csv;

import java.util.Map;

import nl.naturalis.nba.dao.es.calc.ICalculator;

public class CalculatedColumn extends AbstractColumn {

	private ICalculator calculator;

	public CalculatedColumn(String header, ICalculator calculator)
	{
		super(header);
		this.calculator = calculator;
	}

	@Override
	public String getValue(Map<String, Object> esDocumentAsMap)
	{
		Object val = calculator.calculateValue(esDocumentAsMap);
		return val == null ? "" : val.toString();
	}

}
