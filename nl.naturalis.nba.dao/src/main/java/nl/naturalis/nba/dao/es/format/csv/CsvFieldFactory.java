package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.IDataSetFieldFactory;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;


public class CsvFieldFactory implements IDataSetFieldFactory {

	@Override
	public IDataSetField createDataField(String name, String[] path)
	{
		return new DataField(name, path);
	}

	@Override
	public IDataSetField createConstantField(String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IDataSetField createdCalculatedField(String name, ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
