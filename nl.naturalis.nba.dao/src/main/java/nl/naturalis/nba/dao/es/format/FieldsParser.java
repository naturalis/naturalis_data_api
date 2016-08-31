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
 * Parses an entity configuration file. Determines which {@link IDataSetField
 * fields} to include in a {@link DataSetConfiguration data set}. It does so by reading a
 * configuration file that is parsed as follows:
 * <ul>
 * <li>Lines starting with the hash character (#) are ignored.
 * <li>Empty lines are ignored.
 * <li>Other lines display key-value pairs with the equals sign (=) separating
 * key and value. For example:<br>
 * {@code lifeStage = phaseOrStage}.
 * <li>Both key and value are whitespace-trimmed before being processed.
 * <li>In general, the key specifies the name of the field as it should appear
 * in the file(s) generated for the data set (e.g. the column header in a CSV
 * file or the name of an XML element), while the value specifies the full path
 * to a field within an Elasticsearch document (for example
 * {@code gatheringEvent.dateTimeBegin}). However, there are a few exceptions to
 * this rule that will be listed subsequently.
 * <li>If the key starts with the &#64; character, it does not specify a field
 * but a general entity configuration setting. The &#64;document setting
 * specifies the Elasticsearch document type providing the data for the data
 * set. The &#64;entity setting specifies the {@link Entity entity object}
 * within the Elasticsearch document. Not including the &#64;entity setting or
 * not providing a value for it means the entire Elasticsearch document is the
 * entity object.
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
 * {@code kingdom = identifications.0.defaultClassification.kingdom}. See also
 * {@link Path}.
 * <li>
 * </ul>
 * <br>
 * <h3>Example configuration</h3><br>
 * <code>
 * # General configuration for printing out specimen determination data
 * &#64; document = Specimen
 * &#64; entity = identifications
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
