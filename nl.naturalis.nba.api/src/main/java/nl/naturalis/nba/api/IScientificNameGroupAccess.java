package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameGroup;

/**
 * Specifies methods for accessing {@link ScientificNameGroup} documents.
 * 
 * @see ScientificNameGroup
 * @see ScientificName#getScientificNameGroup()
 * 
 * @author Ayco Holleman
 *
 */
public interface IScientificNameGroupAccess extends INbaAccess<ScientificNameGroup> {

	/**
	 * Returns {@link ScientificNameGroup} documents conforming to the provided
	 * query specification. This method hides {@link INbaAccess#query(QuerySpec)
	 * INbaAccess.query} as it takes a {@link ScientificNameGroupQuerySpec}
	 * rather than a {@link QuerySpec} argument. This reflects the fact that
	 * there a some (optional) extra features when specifying a query for
	 * {@code ScientificNameGroup} documents.
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<ScientificNameGroup> query(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException;

	/**
	 * <p>
	 * Returns {@link ScientificNameGroup} documents conforming to the provided
	 * query specification. This method works very much like the generic
	 * {@link #query(QuerySpec) query} method, but this method applies extra
	 * post-processing to the query result. If the {@code QuerySpec} object
	 * contains any specimen-specific query conditions, these are not just used
	 * to find {@code ScientificNameGroup} documents, but also to filter out
	 * specimens <i>within</i> each of the subsequently retrieved
	 * {@code ScientificNameGroup} documents. This will probably make the query
	 * result more intuitively comprehensible.
	 * </p>
	 * <p>
	 * With the generic {@code query} method, if there is a query condition on a
	 * specimen-related attribute, and a {@code ScientificNameGroup} document
	 * contains at least one specimen satisfying that condition, then the entire
	 * {@code ScientificNameGroup} document is returned, even though it may
	 * contain other specimens that do not satisfy the condition. The
	 * {@code querySpecial} method will purge those specimens from the
	 * {@code ScientificNameGroup} document.
	 * </p>
	 * <p>
	 * To following illustrates what is at issue. Say you want to retrieve all
	 * {@code ScientificNameGroup} documents where the specimen's sex is male,
	 * then if a {@code ScientificNameGroup} document contains just one specimen
	 * whose sex is male, it will be included in the query result, even if it
	 * contains other specimens whose sex is female. In other words, with the
	 * generic {@code query} method you may end up retrieving
	 * {@code ScientificNameGroup} documents that ostensibly violate the query
	 * condition, even though strictly speaking they aren't.
	 * </p>
	 * <h3>Limitations</h3>
	 * <p>
	 * The {@code querySpecial} method enforces some extra constraints on how
	 * the {@link ScientificNameGroupQuerySpec QuerySpec} object is structured.
	 * Specimen-related query conditions must be top-level query conditions or
	 * they must be nested within another specimen-related, top-level query
	 * condition. A top-level query condition is a query condition that is a
	 * direct child of the {@link ScientificNameGroupQuerySpec QuerySpec}
	 * object. In other words, a specimen-related query condition <b>must
	 * never</b> be nested within a non-specimen-related query condition.
	 * Otherwise an {@link InvalidQueryException} is thrown.
	 * </p>
	 * 
	 * @param querySpec
	 * @return
	 */
	QueryResult<ScientificNameGroup> querySpecial(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException;

}
