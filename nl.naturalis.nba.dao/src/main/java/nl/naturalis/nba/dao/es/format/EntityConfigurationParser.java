package nl.naturalis.nba.dao.es.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;

import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * Parses an entity configuration file. Determines which {@link IDataSetField fields} to include in a {@link DataSet
 * data set}. It does so by reading a configuration file that is parsed as
 * follows:
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
 * Elasticsearch document (e.g. {@code gatheringEvent.dateTimeBegin}). However,
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
 * {@code kingdom = identifications.0.defaultClassification.kingdom}. See also
 * {@link Path}.
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
public class EntityConfigurationParser {

	private IDataSetFieldFactory factory;
	private File confFile;

	public EntityConfigurationParser(File configFile, IDataSetFieldFactory fieldFactory)
	{
		this.confFile = configFile;
		this.factory = fieldFactory;
	}

	public EntityConfiguration configure(DocumentType<?> dt)
	{
		EntityConfiguration cnf = new EntityConfiguration();
		LineNumberReader lnr = null;
		try {
			new SettingsParser(getLineNumberReader(), dt, cnf).parse();
			new FieldsParser(getLineNumberReader(), dt, factory, cnf).parse();
		}
		catch (EntityConfigurationException e) {
			throw error(lnr, e);
		}
		finally {
			IOUtil.close(lnr);
		}
		return cnf;
	}

	private LineNumberReader getLineNumberReader()
	{
		try {
			return new LineNumberReader(new FileReader(confFile));
		}
		catch (FileNotFoundException e) {
			throw new DaoException("File not found: " + confFile.getAbsolutePath());
		}
	}

	private DaoException error(LineNumberReader lnr, EntityConfigurationException e)
	{
		String file = confFile.getAbsolutePath();
		int line = lnr.getLineNumber();
		String fmt = "%s (%s, line %s)";
		String msg = String.format(fmt, e.getMessage(), file, line);
		return new DaoException(msg);
	}

}
