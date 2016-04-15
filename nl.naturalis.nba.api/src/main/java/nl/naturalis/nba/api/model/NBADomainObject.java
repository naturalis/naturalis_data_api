package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.annotations.MappedProperty;
import nl.naturalis.nba.api.annotations.NotIndexed;

/**
 * Abstract base class for all domain model classes. Currently only contains a
 * property informing clients about the actual (runtime) type of the
 * {@code NBADomainObject} instance. This information gets lost when serializing
 * Java objects to JSON, but is necessary when deserializing JSON back into
 * instances of polymorphic classes.
 */
public abstract class NBADomainObject {

	/**
	 * Returns the runtime type of this instance. The value returned is the
	 * simple name of the runtime class of this instance. More specifically, it
	 * is the name of the class relative to the
	 * {@code nl.naturalis.nba.api.model} package. However, since there are
	 * currently no model classes in sub-packages of this package, this means
	 * that in practice it is always the simple name of the model class.
	 * 
	 * @return
	 */
	@MappedProperty
	@NotIndexed
	public String getRuntimeType()
	{
		return getClass().getSimpleName();
	}

}
