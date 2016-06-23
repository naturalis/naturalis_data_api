package nl.naturalis.nba.dao.es.map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for all nodes within a mapping. The {@link Mapping}
 * object itself, any nested {@link Document documents} within it, all
 * {@link DocumentField fields} and all {@link MultiField multi-fields}
 * underneath a field are instances of an {@link ESField}.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class ESField {

	@JsonIgnore
	protected String name;
	protected ESDataType type;
	@JsonIgnore
	protected ESField parent;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ESDataType getType()
	{
		return type;
	}

	public void setType(ESDataType type)
	{
		this.type = type;
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
