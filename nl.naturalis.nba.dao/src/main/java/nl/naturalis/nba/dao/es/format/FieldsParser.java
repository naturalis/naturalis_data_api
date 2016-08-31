package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.dao.es.format.SettingsParser.SETTING_START_CHAR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * Parses an entity configuration file and extracts the fields from it.
 * 
 * @author Ayco Holleman
 */
class FieldsParser {

	static final char CONSTANT_FIELD_START_CHAR = '*';
	static final char CALCULATED_FIELD_START_CHAR = '%';
	static final char RELATIVE_PATH_START_CHAR = '.';

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private File entityConfigFile;

	FieldsParser(File entityConfigFile)
	{
		this.entityConfigFile = entityConfigFile;
	}

	void parse(EntityConfiguration dse, IDataSetFieldFactory fieldFactory)
			throws EntityConfigurationException
	{
		LineNumberReader lnr = getLineNumberReader();
		DocumentType<?> dt = dse.getDocumentType();
		ArrayList<IDataSetField> fields = new ArrayList<>(32);
		try {
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (!definesField(line))
					continue;
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw new EntityConfigurationException("Missing delimiter \"=\"");
				}
				String key = chunks[0].trim();
				String val = chunks[1].trim();
				IDataSetField field;
				if (val.charAt(0) == CONSTANT_FIELD_START_CHAR) {
					String constant = val.substring(1);
					field = fieldFactory.createConstantField(dt, key, constant);
				}
				else if (val.charAt(0) == CALCULATED_FIELD_START_CHAR) {
					String className = val.substring(1).trim();
					ICalculator calculator = getCalculator(className);
					field = fieldFactory.createdCalculatedField(dt, key, calculator);
				}
				else {
					if (val.charAt(0) == RELATIVE_PATH_START_CHAR) {
						checkRelativePath(dse, dt, val);
						String path = val.substring(1);
						field = fieldFactory.createEntityDataField(dt, key, split(path));
					}
					else {
						new Path(val).validate(dt);
						field = fieldFactory.createDocumentDataField(dt, key, split(val));
					}
				}
				fields.add(field);
			}
		}
		catch (IOException e) {
			throw new EntityConfigurationException(e);
		}
		finally {
			IOUtil.close(lnr);
		}
		dse.setFields(fields.toArray(new IDataSetField[fields.size()]));
	}

	private LineNumberReader getLineNumberReader() throws EntityConfigurationException
	{
		try {
			return new LineNumberReader(new FileReader(entityConfigFile));
		}
		catch (FileNotFoundException e) {
			String fmt = "Entity configuration file not found: %s";
			String msg = String.format(fmt, entityConfigFile.getAbsolutePath());
			throw new EntityConfigurationException(msg);
		}
	}

	private static void checkRelativePath(EntityConfiguration dse, DocumentType<?> dt, String path)
			throws EntityConfigurationException
	{
		if (dse.getPathToEntity() == null) {
			String fmt = "Relative path (%s) not allowed without specifying "
					+ "an entity object. Forgot to include the %sentity setting?";
			String msg = String.format(fmt, path, SETTING_START_CHAR);
			throw new EntityConfigurationException(msg);
		}
		String entityPath = new Path(dse.getPathToEntity()).getPath();
		new Path(entityPath + path).validate(dt);
	}

	private static boolean definesField(String line)
	{
		if (line.length() == 0)
			return false;
		return line.charAt(0) != '#' && line.charAt(0) != SETTING_START_CHAR;
	}

	private static String[] split(String path)
	{
		return path.split("\\.");
	}

	private static ICalculator getCalculator(String name) throws EntityConfigurationException
	{
		Class<?> cls;
		try {
			cls = Class.forName(CALC_PACKAGE + '.' + name);
		}
		catch (ClassNotFoundException e) {
			try {
				cls = Class.forName(name);
			}
			catch (ClassNotFoundException e2) {
				throw new EntityConfigurationException("No such calculator: \"" + name + "\"");
			}
		}
		try {
			return (ICalculator) cls.newInstance();
		}
		catch (ClassCastException e) {
			throw new EntityConfigurationException(name + " is not an ICalculator implementation");
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new EntityConfigurationException(e);
		}
	}

}
