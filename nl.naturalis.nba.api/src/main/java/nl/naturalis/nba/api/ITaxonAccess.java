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
public interface ITaxonAccess {

	/**
	 * Returns taxa conforming to the provided query specification.
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	Taxon[] query(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * A bandwidth-conscious alternative to the {@link #query(QuerySpec) query}
	 * method. This method only returns the values of the fields specified
	 * through {@link QuerySpec#setFields(java.util.List) QuerySpec.setFields}
	 * or {@link QuerySpec#addFields(String...) QuerySpec.addFields}. See
	 * {@link ISpecimenAccess#queryValues(QuerySpec)
	 * ISpecimenAccess.queryValues} for an example.
	 * 
	 * @param spec
	 * @return
	 * @throws InvalidQueryException
	 */
	Object[][] queryValues(QuerySpec spec) throws InvalidQueryException;

	/**
	 * A bandwidth-conscious and fast-responding alternative to the
	 * {@link #query(QuerySpec) query} method. This method produces the same
	 * type of data as the other ({@link #queryValues(QuerySpec) queryValues}
	 * method, but writes them directly to the specified output stream. This
	 * method requires more client-side programming but responds immediately and
	 * places no limit on the amount of documents being processed per call (see
	 * {@link QuerySpec#setSize(int)}).
	 * 
	 * @param spec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void queryValues(QuerySpec spec, OutputStream out) throws InvalidQueryException;

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
