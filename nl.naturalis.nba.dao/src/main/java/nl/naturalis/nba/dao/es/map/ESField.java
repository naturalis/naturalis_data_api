package nl.naturalis.nba.dao.es.map;

import java.util.Collection;

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
	@JsonIgnore
	protected boolean multiValued;

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

	/**
	 * Whether or not the field is multi-valued. While in Elasticsearch every
	 * field is potentially multi-valued, we can determine in advance whether it
	 * can actually ever have more than one value. If the Java field
	 * corresponding to the document field is an array or a {@link Collection},
	 * the document field may contain more than one value. Otherwise it
	 * definitely is single-valued.
	 * 
	 * @return
	 */
	public boolean isMultiValued()
	{
		return multiValued;
	}

	public void setMultiValued(boolean multiValued)
	{
		this.multiValued = multiValued;
	}

}
