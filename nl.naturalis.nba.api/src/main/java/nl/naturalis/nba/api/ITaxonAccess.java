package nl.naturalis.nba.api;

import java.io.OutputStream;

import nl.naturalis.nba.api.model.Taxon;

/**
 * Specifies methods for accessing taxon and species related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ITaxonAccess extends INbaAccess<Taxon> {

	/**
	 * <p>
	 * Writes a DarwinCore Archive with taxa satisfying the specified query
	 * specification to the specified output stream.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET and POST request with
	 * the following end point:
	 * </p>
	 * <p>
	 * <code>
	 * https://api.biodiversitydata.nl/v3/taxon/dwca/query
	 * </code>
	 * </p>
	 * <p>
	 * See {@link QuerySpec} for an explanation of how to encode the
	 * {@code QuerySpec} object in the request.
	 * </p>
	 * 
	 * @param querySpec
	 * @param out
	 * @throws InvalidQueryException
	 */
	void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException;

	/**
	 * <p>
	 * Writes a DarwinCore Archive with taxa from a predefined data set to the
	 * specified output stream. To get the names of all currently defined data
	 * sets, call {@link #dwcaGetDataSetNames() dwcaGetDataSetNames}.
	 * </p>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * https://api.biodiversitydata.nl/v3/taxon/dwca/getDataSet/{name}
	 * </code>
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <p>
	 * <code>
	 * https://api.biodiversitydata.nl/v3/taxon/dwca/getDataSet/nsr
	 * </code>
	 * </p>
	 * 
	 * @param name
	 *            The name of the predefined data set
	 * @param out
	 *            The output stream to write to
	 * @throws InvalidQueryException
	 */
	void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException;

	/**
	 * <p>
	 * Returns the names of all predefined data sets with species data.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * https://api.biodiversitydata.nl/v3/taxon/dwca/getDataSetNames
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	String[] dwcaGetDataSetNames();

	/**
	 * <p>
	 * Groups taxa by their scientific name. Although this method will
	 * optionally also retrieve the specimens associated with a scientific name,
	 * any query conditions and sort fields specified through the
	 * {@link QuerySpec} must reference {@link Taxon} fields only.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * https://api.biodiversitydata.nl/v3/taxon/groupByScientificName
	 * </code>
	 * </p>
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	GroupByScientificNameQueryResult groupByScientificName(GroupByScientificNameQuerySpec querySpec)
			throws InvalidQueryException;

}
