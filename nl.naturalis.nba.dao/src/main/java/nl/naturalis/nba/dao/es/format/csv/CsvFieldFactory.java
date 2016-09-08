package nl.naturalis.nba.dao.es.format.csv;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.DataSource;
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
public class CsvFieldFactory implements IFieldFactory {

	@Override
	public IField createEntityDataField(String name, Path path, DataSource dataSource)
	{
		MappingInfo mappingInfo = new MappingInfo(dataSource.getMapping());
		ESField esField;
		try {
			esField = mappingInfo.getField(path.getPurePathString());
		}
		catch (NoSuchFieldException e) {
			// Won't happen because path has already been checked.
			throw new DaoException(e);
		}
		if (esField.getType() == ESDataType.DATE) {
			return new EntityDateTimeField(name, path);
		}
		return new EntityDataField(name, path);
	}

	@Override
	public IField createDocumentDataField(String name, Path path, Mapping mapping)
	{
		MappingInfo mappingInfo = new MappingInfo(mapping);
		ESField esField;
		try {
			esField = mappingInfo.getField(path.getPurePathString());
		}
		catch (NoSuchFieldException e) {
			// Won't happen because path has already been checked.
			throw new DaoException(e);
		}
		if (esField.getType() == ESDataType.DATE) {
			return new DocumentDateTimeField(name, path);
		}
		return new DocumentDataField(name, path);
	}

	@Override
	public IField createConstantField(String name, String constant)
	{
		return new ConstantField(name, constant);
	}

	@Override
	public IField createdCalculatedField(String name, ICalculator calculator)
	{
		return new CalculatedField(name, calculator);
	}

}
