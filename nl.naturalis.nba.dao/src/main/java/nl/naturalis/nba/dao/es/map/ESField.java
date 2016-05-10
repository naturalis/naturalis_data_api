package nl.naturalis.nba.dao.es.map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for any kind of node in a mapping. A {@link Mapping}, a
 * {@link Document}, a {@link DocumentField} and any {@link IndexableField}
 * within a {@link DocumentField} are all instances of an {@link ESField}.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ESField {

	protected final ESDataType type;

	@JsonIgnore
	private String name;
	@JsonIgnore
	private ESField parent;

	public ESField(ESDataType type)
	{
		this.type = type;
	}

	public ESDataType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ESField getParent()
	{
		return parent;
	}

	public void setParent(ESField parent)
	{
		this.parent = parent;
	}

}
