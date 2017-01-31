package nl.naturalis.nba.common.es.map;

import static java.lang.String.*;

import nl.naturalis.nba.api.Path;

public class NoSuchFieldException extends Exception {

	private Path path;
	private String field;

	public NoSuchFieldException(Path path, String field)
	{
		super(format("Field \"%s\" in %s does not exist or cannot be queried", field, path));
		this.path = path;
		this.field = field;
	}

	/**
	 * Returns the path containing the erroneous field.
	 */
	public Path getFullPath()
	{
		return path;
	}

	/**
	 * Returns the erroneous field.
	 */
	public String getField()
	{
		return field;
	}

}
