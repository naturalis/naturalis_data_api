package nl.naturalis.nba.dao.es.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

/**
 * @author Ayco Holleman
 */
public class FieldConfigurator {

	private static class ConfigurationException extends Exception {

		ConfigurationException(String message)
		{
			super(message);
		}
	}

	private static final String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private IDataSetFieldFactory fieldFactory;
	private MappingInfo mappingInfo;

	public FieldConfigurator(Mapping mapping, IDataSetFieldFactory fieldFactory)
	{
		this.mappingInfo = new MappingInfo(mapping);
		this.fieldFactory = fieldFactory;
	}

	public IDataSetField[] getFields(File confFile)
	{
		try {
			FileReader fr = new FileReader(confFile);
			LineNumberReader lnr = new LineNumberReader(fr);
			return getFields(lnr, confFile.getAbsolutePath());
		}
		catch (FileNotFoundException e) {
			throw new DaoException("File not found: " + confFile.getAbsolutePath());
		}
	}

	public IDataSetField[] getFields(InputStream is, String source)
	{
		try {
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			LineNumberReader lnr = new LineNumberReader(isr);
			return getFields(lnr, source);
		}
		catch (UnsupportedEncodingException e) {
			throw new DaoException(e);
		}
	}

	public IDataSetField[] getFields(LineNumberReader lnr, String source)
	{
		ArrayList<IDataSetField> fields = new ArrayList<>(60);
		try {
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw new ConfigurationException("Missing delimiter");
				}
				String key = chunks[0].trim();
				String val = chunks[1].trim();
				IDataSetField field;
				if (val.charAt(0) == '*') {
					field = fieldFactory.createConstantField(key, val.substring(1));
				}
				else if (val.charAt(0) == '%') {
					ICalculator calculator = getCalculator(val.substring(1).trim());
					field = fieldFactory.createdCalculatedField(key, calculator);
				}
				else {
					field = createDataField(key, val);
				}
				fields.add(field);
			}
		}
		catch (ConfigurationException e) {
			throw error(source, lnr, e);
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
		finally {
			IOUtil.close(lnr);
		}
		return fields.toArray(new IDataSetField[fields.size()]);
	}

	private IDataSetField createDataField(String key, String val) throws ConfigurationException
	{
		String[] path = val.split("\\.");
		checkPath(path);
		checkArrayIndices(path);
		return fieldFactory.createDataField(key, path);
	}

	private void checkPath(String[] path) throws ConfigurationException
	{
		StringBuilder sb = new StringBuilder(50);
		for (String element : path) {
			try {
				Integer.parseInt(element);
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
		try {
			ESField esField = mappingInfo.getField(sb.toString());
			if (!(esField instanceof DocumentField)) {
				String fmt = "Invalid field (type is object/nested): %s";
				String msg = String.format(fmt, sb.toString());
				throw new ConfigurationException(msg);
			}
		}
		catch (NoSuchFieldException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	private void checkArrayIndices(String[] path) throws ConfigurationException
	{
		StringBuilder sb = new StringBuilder(50);
		for (String element : path) {
			try {
				int index = Integer.parseInt(element);
				ESField esField = mappingInfo.getField(sb.toString());
				if (!esField.isMultiValued()) {
					String fmt = "Illegal array index (%s) following single-valued field: %s";
					String msg = String.format(fmt, index, sb.toString());
					throw new ConfigurationException(msg);
				}
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
	}

	private static ICalculator getCalculator(String name) throws ConfigurationException
	{
		String className = CALC_PACKAGE + '.' + name;
		try {
			Class<?> cls = Class.forName(className);
			return (ICalculator) cls.newInstance();
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationException("No such calculator: \"" + name + "\"");
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new DaoException(e);
		}
	}

	private static DaoException error(String source, LineNumberReader lnr, ConfigurationException e)
	{
		int line = lnr.getLineNumber();
		String fmt = "%s (%s, line %s)";
		String msg = String.format(fmt, e.getMessage(), source, line);
		return new DaoException(msg);
	}

}
