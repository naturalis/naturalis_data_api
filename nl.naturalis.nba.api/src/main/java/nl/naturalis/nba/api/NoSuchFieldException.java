package nl.naturalis.nba.api;

import static java.lang.String.format;

/**
 * Thrown if a user-specified path does not exist or if it is a non-queryable
 * path (only leaf nodes in the document type tree are queryable).
 * 
 * @author Ayco Holleman
 *
 */
public class NoSuchFieldException extends NbaException {

  private static final long serialVersionUID = 7239043960633546032L;

  private Path path;
	private Path element;

	public NoSuchFieldException(Path path)
	{
		super(format("Field \"%s\" does not exist or cannot be queried", path));
		this.path = path;
	}

	public NoSuchFieldException(Path path, Path element)
	{
		super(format("Invalid element \"%s\" in path \"%s\"", element, path));
		this.path = path;
		this.element = element;
	}

	/**
	 * Returns the path containing the erroneous element.
	 */
	public Path getFullPath()
	{
		return path;
	}

	/**
	 * Returns the path element that caused this exception to be thrown.
	 */
	public Path getPathElement()
	{
		return element;
	}

}
