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

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

/**
 * A {@code FieldConfigurator} determines which {@link IDataSetField fields} to
 * include in a {@link DataSet data set}. It does so by reading a configuration
 * file that is parsed as follows:
 * <ul>
 * <li>Lines starting with the hash character (#) are ignored.
 * <li>Empty lines are ignored.
 * <li>Other lines display key-value pairs with the equals sign (=) separating
 * key and value. For example:<br>
 * {@code lifeStage = phaseOrStage}.
 * <li>Both key and value are whitespace-trimmed before being processed.
 * <li>In general, the key specifies the name of the field as it should appear
 * in the file(s) generated for the data set (e.g. the column header in a CSV
 * file), while the value specifies the full path to a field within an
 * Elasticsearch document (e.g. {@code gatheringEvent.dateTimeBeing}). However,
 * there are a few exceptions to this rule that will be subsequently be listed.
 * <li>If the key starts with an ampersand (&amp;) it does not specify a field
 * but rather a general configuration setting. For example with the &amp;entity
 * key is used to specify the {@link Entity entity object}. (Not including the
 * &amp;entity key or not providing a value for it means the entire
 * Elasticsearch document is considered to be the entity object.) Currently, the
 * &amp;entity setting is the only recognized setting.
 * <li>If the value starts with an asterisk (*), it specifies a constant value.
 * Everything <i>following</i> the asterisk is used as the value for the field.
 * <li>If the value starts with the percentage sign (%), it means the field has
 * a calculated value. The word following the percentage sign specifies the
 * simple class name of an {@link ICalculator} implementation.
 * <li>If the value starts with a dot (.), it specifies a field within an
 * Elasticsearch document <i>relative</i> to the entity object specified by the
 * &amp;entity setting. For example, if the &amp;entity setting's value is
 * {@code gatheringEvent.gatheringPersons}, then you would refer to the
 * collector's full name by specifying {@code .fullName} rather than
 * {@code gatheringEvent.gatheringPersons.fullName}.
 * <li>Otherwise the value specifies the <i>full path</i> of a field within an
 * Elasticsearch document. Array access can be achieved by adding the array
 * index after the name of the field that represents the array. For example:<br>
 * {@code kingdom = identifications.0.defaultClassification.kingdom}
 * <li>
 * </ul>
 * <br>
 * <h3>Example configuration</h3><br>
 * <code>
 * # Configuration for printing out specimen determination data
 * &entity = identifications
 * SpecimenID = unitID
 * SourceSystem = sourceSystem.name
 * # A constant field:
 * NomenclaturalCode = *ICZN
 * ScientificName = .scientificName.fullScientificName
 * TaxonRank = .taxonRank
 * Genus = .defaultClassification.genus
 * Subgenus = .defaultClassification.subgenus
 * SpecificEpithet = .defaultClassification.specificEpithet
 * InfraspecificEpithet = .defaultClassification.infraspecificEpithet
 * # A calculated field:
 * DateIdentified = % DateIdentifiedCalculator
 * </code>
 * 
 * @see IDataSetField
 * @see IDataSetFieldFactory
 * 
 * @author Ayco Holleman
 */
public class FieldConfigurator {

	/**
	 * Constructs the full path of an Elasticsearch field from the specified
	 * path elements. Array indices in the specified string array are skipped.
	 * For example, if you pass {"identifications", "0",
	 * "defaultClassification", "kingdom"}, then
	 * "identifications.defaultClassification.kingdom" is returned.
	 * 
	 * @param pathElements
	 * @return
	 */
	public static String getPath(String[] pathElements)
	{
		StringBuilder sb = new StringBuilder(50);
		for (String element : pathElements) {
			try {
				Integer.parseInt(element);
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
		return sb.toString();
	}

	private static class ConfigurationException extends Exception {

		ConfigurationException(String message)
		{
			super(message);
		}
	}

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private IDataSetFieldFactory factory;
	private DocumentType<?> dt;

	public FieldConfigurator(DocumentType<?> dt, IDataSetFieldFactory fieldFactory)
	{
		this.dt = dt;
		this.factory = fieldFactory;
	}

	/**
	 * Returns the fields to be included in a data set as configured in the
	 * specified configuration file.
	 * 
	 * @param confFile
	 * @return
	 */
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

	/**
	 * Returns the fields to be included in a data set as configured in the
	 * specified resource. The {@code source} argument should specify the name
	 * of the resource and is only used for reporting purposes.
	 * 
	 * @param is
	 * @param source
	 * @return
	 */
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

	/**
	 * Returns the fields to be included in a data set as configured in the
	 * specified resource. The {@code source} argument should specify the name
	 * of the resource and is only used for reporting purposes.
	 * 
	 * @param is
	 * @param source
	 * @return
	 */
	public IDataSetField[] getFields(LineNumberReader lnr, String source)
	{
		ArrayList<IDataSetField> fields = new ArrayList<>(60);
		try {
			String entity = null;
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				String[] chunks = line.split("=");
				if (chunks.length != 2) {
					throw new ConfigurationException("Missing delimiter \"=\"");
				}
				String key = chunks[0].trim();
				String value = chunks[1].trim();
				if (key.charAt(0) == '&') {
					String setting = key.substring(1);
					if (setting.equals("entity")) {
						entity = value;
					}
					continue;
				}
				IDataSetField field;
				if (value.charAt(0) == '*') {
					String constant = value.substring(1);
					field = factory.createConstantField(dt, key, constant);
				}
				else if (value.charAt(0) == '%') {
					String className = value.substring(1).trim();
					ICalculator calculator = getCalculator(className);
					field = factory.createdCalculatedField(dt, key, calculator);
				}
				else {
					field = createDataField(key, value, entity);
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

	private IDataSetField createDataField(String fieldName, String path, String entity)
			throws ConfigurationException
	{
		boolean isRelativePath;
		String fullPath;
		if (path.charAt(0) == '.') {
			if (entity == null) {
				String fmt = "Relative path (\"%s\") not allowed without specifying "
						+ "entity object using the &entity setting (entity object must "
						+ "be specified before you can specify relative paths)";
				String msg = String.format(fmt, path);
				throw new ConfigurationException(msg);
			}
			isRelativePath = true;
			fullPath = entity + path;
			path = path.substring(1).trim();
		}
		else {
			isRelativePath = false;
			fullPath = path;
		}
		String[] pathElements = fullPath.split("\\.");
		checkPath(pathElements);
		checkArrayIndices(pathElements);
		pathElements = path.split("\\.");
		if (isRelativePath)
			return factory.createEntityDataField(dt, fieldName, pathElements);
		return factory.createDocumentDataField(dt, fieldName, pathElements);
	}

	/*
	 * Make sure the specified path actually specifies a field within the
	 * Elasticsearch document.
	 */
	private void checkPath(String[] pathElements) throws ConfigurationException
	{
		MappingInfo mi = new MappingInfo(dt.getMapping());
		String path = getPath(pathElements);
		try {
			ESField esField = mi.getField(path);
			if (!(esField instanceof DocumentField)) {
				String fmt = "Invalid field (type is object/nested): %s";
				String msg = String.format(fmt, path);
				throw new ConfigurationException(msg);
			}
		}
		catch (NoSuchFieldException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	/*
	 * Make sure array indices are used only if the preceding path element
	 * refers to an array.
	 */
	private void checkArrayIndices(String[] path) throws ConfigurationException
	{
		MappingInfo mi = new MappingInfo(dt.getMapping());
		StringBuilder sb = new StringBuilder(50);
		for (String element : path) {
			try {
				int index = Integer.parseInt(element);
				ESField esField = mi.getField(sb.toString());
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
