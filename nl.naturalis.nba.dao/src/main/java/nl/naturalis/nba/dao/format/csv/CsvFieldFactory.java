package nl.naturalis.nba.dao.format.csv;

import java.net.URI;

import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.format.DataSource;
import nl.naturalis.nba.dao.format.FieldConfigurationException;
import nl.naturalis.nba.dao.format.ICalculator;
import nl.naturalis.nba.dao.format.IField;
import nl.naturalis.nba.dao.format.IFieldFactory;

/**
 * A factory for {@link IField} instances that escape string sequences as
 * appropriate for CSV files.
 * 
 * @author Ayco Holleman
 *
 */
/*
 * TODO: Since CSV escaping has been moved from the IField implementations (like
 * EntityField and DocumentField) to CSVRecordWriter, these IField
 * implementations now have a more generic nature; there is nothing specifically
 * CSV-ish about them. They should be moved to the parent package, or some other
 * package.
 */
public class CsvFieldFactory implements IFieldFactory {

	@Override
	public IField createEntityDataField(String name, URI term, Boolean isCoreId, Path path, DataSource dataSource)
			throws FieldConfigurationException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(dataSource.getMapping());
		Path fullPath = dataSource.getPath().append(path.getPurePath());
		ESField esField = null;
		try {
			esField = mappingInfo.getField(fullPath);
		}
		catch (NoSuchFieldException e) { /* Won't happen */ }
		if (esField.getType() == ESDataType.DATE) {
			return new EntityDateField(name, term, isCoreId, path);
		}
		return new EntityField(name, term, isCoreId, path);
	}

	@Override
	public IField createDocumentDataField(String name, URI term, Boolean isCoreId, Path path, DataSource dataSource)
			throws FieldConfigurationException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(dataSource.getMapping());
		ESField esField = null;
		try {
			esField = mappingInfo.getField(path.getPurePath());
		}
		catch (NoSuchFieldException e) { /* Won't happen */ }
		if (esField.getType() == ESDataType.DATE) {
			return new DocumentDateField(name, term, isCoreId, path);
		}
		return new DocumentField(name, term, isCoreId, path);
	}

	@Override
	public IField createConstantField(String name, URI term, Boolean isCoreId, String constant)
			throws FieldConfigurationException
	{
		return new ConstantField(name, term, isCoreId, constant);
	}

	@Override
	public IField createdCalculatedField(String name, URI term, Boolean isCoreId, ICalculator calculator)
			throws FieldConfigurationException
	{
		return new CalculatedField(name, term, isCoreId, calculator);
	}

}
