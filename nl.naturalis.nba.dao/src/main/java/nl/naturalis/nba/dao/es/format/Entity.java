package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * An {@code EntityConfiguration} specifies how to generate one particular file
 * in a data set. For example, DwC archives may contain multiple CSV files, each
 * containing a different type of data (e.g. taxa, literature references,
 * vernacular names, etc.). These files are referred to as entities.
 * @author Ayco Holleman
 *
 */
public class Entity {

	private String name;
	private IDataSetField[] fields;
	private DocumentType<?> documentType;
	private String[] pathToEntity;
	private QuerySpec querySpec;

	Entity()
	{
	}

	Entity(String name)
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
	 * CSV record. This object is referred to as the {@link EntityObject entity
	 * object}. Data from the parent object must be regarded as enrichments, and
	 * data from child objects must somehow be flattened to end up in the CSV
	 * record. The entity object may possibly be the entire Elasticsearch
	 * document rather than any object nested within it. In this case this
	 * method will return {@code null}.
	 * 
	 * @see EntityObject
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
	 * at the {@link DataSet} level.
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
