package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.IField;
import nl.naturalis.nba.dao.es.format.ITypedFieldFactory;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * A factory for {@link IField} instances that escape string sequences as
 * appropriate for CSV files.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvFieldFactory implements ITypedFieldFactory {

	@Override
	public IField createEntityDataField(DocumentType<?> dt, String name, String[] path)
	{
		MappingInfo mappingInfo = new MappingInfo(dt.getMapping());
		String p = JsonUtil.getPurePath(path);
		ESField esField;
		try {
			esField = mappingInfo.getField(p);
		}
		catch (NoSuchFieldException e) {
			// Won't happen because path has already been checked.
			throw new DaoException(e);
		}
		if (esField.getType() == ESDataType.DATE)
			return new EntityDateTimeField(name, path);
		return new EntityDataField(name, path);
	}

	@Override
	public IField createDocumentDataField(DocumentType<?> dt, String name, String[] path)
	{
		MappingInfo mappingInfo = new MappingInfo(dt.getMapping());
		String p = JsonUtil.getPurePath(path);
		ESField esField;
		try {
			esField = mappingInfo.getField(p);
		}
		catch (NoSuchFieldException e) {
			// Won't happen because path has already been checked.
			throw new DaoException(e);
		}
		if (esField.getType() == ESDataType.DATE)
			return new DocumentDateTimeField(name, path);
		return new DocumentDataField(name, path);
	}

	@Override
	public IField createConstantField(DocumentType<?> dt, String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IField createdCalculatedField(DocumentType<?> dt, String name,
			ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
