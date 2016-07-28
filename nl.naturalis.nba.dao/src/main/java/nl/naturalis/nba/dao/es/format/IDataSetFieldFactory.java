package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

public interface IDataSetFieldFactory {

	IDataSetField createDataField(String name, String[] path);

	IDataSetField createConstantField(String name, String constant);

	IDataSetField createdCalculatedField(String name, ICalculator calculator);

}
