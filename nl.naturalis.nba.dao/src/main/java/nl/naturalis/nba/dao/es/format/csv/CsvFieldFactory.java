package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.FieldConfigurator;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.IDataSetFieldFactory;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.map.ESDataType;
import nl.naturalis.nba.dao.es.map.ESField;
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
	public IDataSetField createDataField(DocumentType<?> dt, String name, String[] path)
	{
		MappingInfo mi = new MappingInfo(dt.getMapping());
		String p = FieldConfigurator.getPath(path);
		ESField esField = mi.getField(p);
		if (esField.getType() == ESDataType.DATE)
			return new DateField(name, path);
		return new DataField(name, path);
	}

	@Override
	public IDataSetField createConstantField(DocumentType<?> dt, String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IDataSetField createdCalculatedField(DocumentType<?> dt, String name, ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
