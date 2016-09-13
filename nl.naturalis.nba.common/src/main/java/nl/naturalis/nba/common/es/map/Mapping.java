package nl.naturalis.nba.common.es.map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Models an Elasticsearch type mapping.
 * 
 * @see ComplexField
 * 
 * @author Ayco Holleman
 *
 */
public class Mapping extends ComplexField {

	/* NBA types are always strictly typed. */
	private final String dynamic = "strict";
	@JsonIgnore
	private final Class<?> mappedClass;

	Mapping(Class<?> mappedClass)
	{
		this.mappedClass = mappedClass;
	}

	/**
	 * Returns the value of the type mapping's &#34;dynamic&#34; property. Since
	 * all NBA document types are strictly typed, this method will always return
	 * "strict".
	 */
	public String getDynamic()
	{
		return dynamic;
	}

	/**
	 * Returns Java class from which the Elasticsearch mapping was generated.
	 * generated.
	 */
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
