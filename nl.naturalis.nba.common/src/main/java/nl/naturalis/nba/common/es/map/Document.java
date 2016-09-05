package nl.naturalis.nba.common.es.map;

import java.util.LinkedHashMap;

/**
 * Class representing a complex structure within an Elasticsearch type mapping.
 * This can be either the entire type mapping, or a nested document or a simple
 * object within an Elasticsearch mapping. The {@link Mapping} subclass is in
 * fact used to represent the entire type mapping.
 * 
 * @author Ayco Holleman
 *
 */
public class Document extends ESField {

	private final LinkedHashMap<String, ESField> properties;

	public Document()
	{
		this(null);
	}

	public Document(ESDataType type)
	{
		this.type = type;
		properties = new LinkedHashMap<>();
	}

	public LinkedHashMap<String, ESField> getProperties()
	{
		return properties;
	}

	public void addField(String name, ESField f)
	{
		properties.put(name, f);
	}

}
