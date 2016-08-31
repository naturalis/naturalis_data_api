package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * An {@code EntityConfiguration} specifies how to generate one particular file
 * in a data set. For example, DwC archives may contain multiple CSV files, each
 * containing a different type of data (e.g. taxa, literature references,
 * vernacular names, etc.). These files are referred to as entities.
 * 
 * <h3>Configuration file</h3><br>
 * Data set entities are configured using a configuration file which is parsed
 * as follows:
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
 * but a general configuration setting. The &#64;document setting specifies the
 * Elasticsearch document type providing the data for the data set. The
 * &#64;entity setting specifies the {@link Entity entity object} within the
 * Elasticsearch document. Not including the &#64;entity setting or not
 * providing a value for it means the entire Elasticsearch document is the
 * entity object. There may be whitespace between the &#64; character and the
 * key.
 * <li>If the value starts with an asterisk (*), it specifies a constant value.
 * Everything <i>following</i> the asterisk is used as the value for the field.
 * <li>If the value starts with the percentage sign (%), it means the field has
 * a calculated value. The word following the percentage sign specifies the
 * simple class name of an {@link ICalculator} implementation.
 * <li>If the value starts with a dot (.), it specifies a field within an
 * Elasticsearch document <i>relative</i> to the entity object specified by the
 * &amp;entity setting. For example, if the &amp;entity setting's value is
 * {@code gatheringEvent.gatheringPersons} (meaning you want to print out
 * specimen collector information), then you would refer to the collector's full
 * name by specifying {@code .fullName} rather than
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
 * @author Ayco Holleman
 *
 */
public class EntityConfiguration {

	private String name;
	private IDataSetField[] fields;
	private DocumentType<?> documentType;
	private String[] pathToEntity;
	private QuerySpec querySpec;

	EntityConfiguration()
	{
	}

	EntityConfiguration(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the fields to be included in the file.
	 */
	public IDataSetField[] getFields()
	{
		return fields;
	}

	void setFields(IDataSetField[] fields)
	{
		this.fields = fields;
	}

	public DocumentType<?> getDocumentType()
	{
		return documentType;
	}

	void setDocumentType(DocumentType<?> documentType)
	{
		this.documentType = documentType;
	}

	/**
	 * Returns the path to the object within an Elasticsearch
	 * {@link DocumentType} that is the basic unit for this
	 * {@code DataSetEntity}. When writing CSV files, for example, this is the
	 * object that gets turned into a CSV record. If the Elasticsearch document
	 * contains an array of these objects, each one of them becomes a separate
	 * CSV record. This object is referred to as the {@link Entity entity
	 * object}. Data from the parent object must be regarded as enrichments, and
	 * data from child objects must somehow be flattened to end up in the CSV
	 * record. The entity object may possibly be the entire Elasticsearch
	 * document rather than any object nested within it. In this case this
	 * method will return {@code null}.
	 * 
	 * @see Entity
	 */
	public String[] getPathToEntity()
	{
		return pathToEntity;
	}

	void setPathToEntity(String[] path)
	{
		this.pathToEntity = path;
	}

	/**
	 * Returns the Elasticsearch query that provides the data for the file. This
	 * allows for the possibility that different files within the same data set
	 * get their data from different Elasticsearch queries. This possibility is
	 * currently not made use of, however. Elasticsearch queries are specified
	 * at the {@link DataSetConfiguration} level.
	 */
	public QuerySpec getQuerySpec()
	{
		return querySpec;
	}

	void setQuerySpec(QuerySpec querySpec)
	{
		this.querySpec = querySpec;
	}

}
