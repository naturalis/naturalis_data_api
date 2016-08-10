package nl.naturalis.nba.api;

import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public interface ITaxonAccess {

	/**
	 * Writes a DarwinCore Archive with taxa satisfying the specified query
	 * specification to the specified output stream.
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException;

	/**
	 * Writes a DarwinCore Archive with taxa from a predefined data set to the
	 * specified output stream. To get the names of all currently defined data
	 * sets, call {@link #dwcaGetDataSetNames() dwcaGetDataSetNames}.
	 * 
	 * @param name
	 *            The name of the predefined data set
	 * @param out
	 *            The output stream to write to
	 * @throws InvalidQueryException
	 */
	void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException;

	/**
	 * Returns the names of all predefined data sets with taxon/species data.
	 * 
	 * @return
	 */
	String[] dwcaGetDataSetNames();
}
