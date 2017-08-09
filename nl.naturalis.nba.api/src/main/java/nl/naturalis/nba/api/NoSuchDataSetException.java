package nl.naturalis.nba.api;

/**
 * Thrown by methods that produce predefined, named data sets (like
 * {@link ITaxonAccess#dwcaGetDataSet(String, java.io.OutputStream)
 * dwcaGetDataSet}) when there is no data set with the specified name.
 * 
 * @author Ayco Holleman
 *
 */
public class NoSuchDataSetException extends NbaException {

	public NoSuchDataSetException(String dataSet)
	{
		super(String.format("No such data set: \"%s\"", dataSet));
	}

}
