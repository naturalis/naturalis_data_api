package nl.naturalis.nba.common.es.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Models an Elasticsearch type mapping. See {@link MappingFactory} for more
 * details.
 * 
 * @see ComplexField
 * 
 * @author Ayco Holleman
 *
 */
@JsonPropertyOrder({ "dynamic", "properties" })
public class Mapping<T extends IDocumentObject> extends ComplexField {

	/* NBA document types are always strictly typed. */
	private final String dynamic = "strict";
	@JsonIgnore
	private final Class<T> mappedClass;

	Mapping(Class<T> mappedClass)
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
	public Class<T> getMappedClass()
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
		Mapping<?> other = (Mapping<?>) obj;
		return mappedClass == other.mappedClass;
	}

	@Override
	public int hashCode()
	{
		return mappedClass.hashCode();
	}

}
