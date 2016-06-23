package nl.naturalis.nba.dao.es.map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class representing an Elasticsearch type mapping.
 * 
 * @see Document
 * 
 * @author Ayco Holleman
 *
 */
public class Mapping extends Document {

	/* NBA types are always strictly typed. */
	private final String dynamic = "strict";
	@JsonIgnore
	private final Class<?> mappedClass;

	public Mapping(Class<?> mappedClass)
	{
		this.mappedClass = mappedClass;
	}

	public String getDynamic()
	{
		return dynamic;
	}

	public Class<?> getMappedClass()
	{
		return mappedClass;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != Mapping.class) {
			return false;
		}
		Mapping other = (Mapping) obj;
		return mappedClass == other.mappedClass;
	}

	@Override
	public int hashCode()
	{
		return mappedClass.hashCode();
	}

}
