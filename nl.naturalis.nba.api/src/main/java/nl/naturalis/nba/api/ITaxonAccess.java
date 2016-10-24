package nl.naturalis.nba.api;

import java.io.OutputStream;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies methods for accessing taxon and species related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ITaxonAccess extends INbaAccess<Taxon> {


	/**
	 * Writes a DarwinCore Archive with taxa satisfying the specified query
	 * specification to the specified output stream.
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;

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
	void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException;

	/**
	 * Returns the names of all predefined data sets with taxon/species data.
	 * 
	 * @return
	 */
	String[] dwcaGetDataSetNames();
}
