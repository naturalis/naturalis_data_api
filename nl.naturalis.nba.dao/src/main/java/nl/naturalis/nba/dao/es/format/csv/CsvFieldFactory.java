package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.DocumentType;
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
	public IDataSetField createEntityDataField(DocumentType<?> dt, String name, String[] path)
	{
		MappingInfo mappingInfo = new MappingInfo(dt.getMapping());
		String p = JsonUtil.getPurePath(path);
		ESField esField = mappingInfo.getField(p);
		if (esField.getType() == ESDataType.DATE)
			return new EntityDateTimeField(name, path);
		return new EntityDataField(name, path);
	}

	@Override
	public IDataSetField createDocumentDataField(DocumentType<?> dt, String name, String[] path)
	{
		MappingInfo mappingInfo = new MappingInfo(dt.getMapping());
		String p = JsonUtil.getPurePath(path);
		ESField esField = mappingInfo.getField(p);
		if (esField.getType() == ESDataType.DATE)
			return new DocumentDateTimeField(name, path);
		return new DocumentDataField(name, path);
	}

	@Override
	public IDataSetField createConstantField(DocumentType<?> dt, String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IDataSetField createdCalculatedField(DocumentType<?> dt, String name,
			ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
