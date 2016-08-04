package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.IDataSetFieldFactory;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.map.ESDataType;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingInfo;

/**
 * A factory for {@link IDataSetField} instances that escape string sequences as
 * appropriate for CSV files.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvFieldFactory implements IDataSetFieldFactory {

	@Override
	public IDataSetField createDataField(DataSetCollection dsc, String name, String[] path)
	{
		Mapping mapping = dsc.getDocumentType().getMapping();
		MappingInfo mi = new MappingInfo(mapping);
		String p = FieldConfigurator.getPath(path);
		ESField esField = mi.getField(p);
		if (esField.getType() == ESDataType.DATE)
			return new DateField(name, path);
		return new DataField(name, path);
	}

	@Override
	public IDataSetField createConstantField(DataSetCollection dsc, String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IDataSetField createdCalculatedField(DataSetCollection dsc, String name, ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
