package nl.naturalis.nba.api;

public class NoSuchDataSetException extends Exception {

	public NoSuchDataSetException(String dataSet)
	{
		super(String.format("No such data set: \"%s\"", dataSet));
	}

}
