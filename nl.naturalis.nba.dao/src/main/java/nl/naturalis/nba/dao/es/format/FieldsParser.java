package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.dao.es.format.SettingsParser.SETTING_START_CHAR;
import static org.domainobject.util.ArrayUtil.implode;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * Parses those lines in an entity configuration file (wrapped into a
 * {@link LineNumberReader} that specify fields (rather than general
 * configuration settings).
 * 
 * @author Ayco Holleman
 *
 */
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
		Path p = new Path(path);
		p.validate(documentType);
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
