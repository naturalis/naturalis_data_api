package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.dao.es.format.SettingsParser.SETTING_START_CHAR;
import static org.domainobject.util.ArrayUtil.implode;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.EntityConfigurator.EntityConfigurationException;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;
import static nl.naturalis.nba.common.json.JsonUtil.*;

class FieldsParser {

	static final char CONSTANT_FIELD_START_CHAR = '*';
	static final char CALCULATED_FIELD_START_CHAR = '%';

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private LineNumberReader lnr;
	private DocumentType<?> documentType;
	private IDataSetFieldFactory fieldFactory;
	private EntityConfiguration conf;

	FieldsParser(LineNumberReader lnr, DocumentType<?> documentType,
			IDataSetFieldFactory fieldFactory, EntityConfiguration conf)
	{
		this.lnr = lnr;
		this.documentType = documentType;
		this.fieldFactory = fieldFactory;
		this.conf = conf;
	}

	void parse() throws EntityConfigurationException
	{
		ArrayList<IDataSetField> fields = new ArrayList<>(60);
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
					field = fieldFactory.createConstantField(documentType, key, constant);
				}
				else if (val.charAt(0) == CALCULATED_FIELD_START_CHAR) {
					String className = val.substring(1).trim();
					ICalculator calculator = getCalculator(className);
					field = fieldFactory.createdCalculatedField(documentType, key, calculator);
				}
				else {
					field = createDataField(key, val);
				}
				fields.add(field);
			}
		}
		catch (IOException e) {
			throw new DaoException(e);
		}
		conf.setFields(fields.toArray(new IDataSetField[fields.size()]));
	}

	private IDataSetField createDataField(String fieldName, String path)
			throws EntityConfigurationException
	{
		checkPath(path);
		if (path.charAt(0) == '.')
			return fieldFactory.createEntityDataField(documentType, fieldName, split(path));
		return fieldFactory.createDocumentDataField(documentType, fieldName, split(path));
	}

	private void checkPath(String path) throws EntityConfigurationException
	{
		if (path.charAt(0) == '.') {
			if (conf.getPathToEntity() == null) {
				String fmt = "Relative path (%s) not allowed without specifying "
						+ "an entity object. Forgot to include %sentity setting?";
				String msg = String.format(fmt, path, SETTING_START_CHAR);
				throw new EntityConfigurationException(msg);
			}
			String entityPath = implode(conf.getPathToEntity(), ".");
			path = entityPath + path;
		}
		checkArrayIndices(path);
		// Remove array indices from path
		path = getPurePath(path);
		MappingInfo mi = new MappingInfo(documentType.getMapping());
		try {
			ESField esField = mi.getField(path);
			if (!(esField instanceof DocumentField)) {
				String fmt = "Invalid field (type is object/nested): %s";
				String msg = String.format(fmt, path);
				throw new EntityConfigurationException(msg);
			}
		}
		catch (NoSuchFieldException e) {
			throw new EntityConfigurationException(e.getMessage());
		}
	}

	/*
	 * Make sure array indices are used only if the preceding path element
	 * refers to a nested array.
	 */
	private void checkArrayIndices(String path) throws EntityConfigurationException
	{
		MappingInfo mi = new MappingInfo(documentType.getMapping());
		StringBuilder sb = new StringBuilder(50);
		for (String element : split(path)) {
			try {
				int index = Integer.parseInt(element);
				ESField esField = mi.getField(sb.toString());
				if (!esField.isMultiValued()) {
					String fmt = "Illegal array index (%s) following single-valued field: %s";
					String msg = String.format(fmt, index, sb.toString());
					throw new EntityConfigurationException(msg);
				}
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
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
		String className = CALC_PACKAGE + '.' + name;
		try {
			Class<?> cls = Class.forName(className);
			return (ICalculator) cls.newInstance();
		}
		catch (ClassNotFoundException e) {
			throw new EntityConfigurationException("No such calculator: \"" + name + "\"");
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new DaoException(e);
		}
	}

}
