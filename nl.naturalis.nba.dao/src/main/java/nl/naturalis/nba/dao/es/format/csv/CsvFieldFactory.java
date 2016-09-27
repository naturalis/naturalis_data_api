package nl.naturalis.nba.dao.es.format.csv;

import java.net.URI;
import java.util.LinkedHashMap;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.es.format.DataSource;
import nl.naturalis.nba.dao.es.format.FieldConfigurationException;
import nl.naturalis.nba.dao.es.format.ICalculator;
import nl.naturalis.nba.dao.es.format.IField;
import nl.naturalis.nba.dao.es.format.IFieldFactory;

/**
 * A factory for {@link IField} instances that escape string sequences as
 * appropriate for CSV files.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvFieldFactory implements IFieldFactory {

	@Override
	public IField createEntityDataField(String name, URI term, Path path, DataSource dataSource)
			throws FieldConfigurationException
	{
		MappingInfo mappingInfo = new MappingInfo(dataSource.getMapping());
		Path fullPath = dataSource.getPath().append(path.getPurePath());
		ESField esField = null;
		try {
			esField = mappingInfo.getField(fullPath);
		}
		catch (NoSuchFieldException e) { /* Won't happen */ }
		if (esField.getType() == ESDataType.DATE) {
			return new EntityDateTimeField(name, term, path);
		}
		return new EntityDataField(name, term, path);
	}

	@Override
	public IField createDocumentDataField(String name, URI term, Path path, DataSource dataSource)
			throws FieldConfigurationException
	{
		MappingInfo mappingInfo = new MappingInfo(dataSource.getMapping());
		ESField esField = null;
		try {
			esField = mappingInfo.getField(path.getPurePath());
		}
		catch (NoSuchFieldException e) { /* Won't happen */ }
		if (esField.getType() == ESDataType.DATE) {
			return new DocumentDateTimeField(name, term, path);
		}
		return new DocumentDataField(name, term, path);
	}

	@Override
	public IField createConstantField(String name, URI term, String constant)
			throws FieldConfigurationException
	{
		return new ConstantField(name, term, constant);
	}

	@Override
	public IField createdCalculatedField(String name, URI term, ICalculator calculator,
			LinkedHashMap<String, String> args) throws FieldConfigurationException
	{
		return new CalculatedField(name, term, calculator);
	}

}
