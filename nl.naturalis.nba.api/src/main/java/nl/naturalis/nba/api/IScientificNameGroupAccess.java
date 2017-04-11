package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.ScientificNameGroup;

/**
 * Specifies methods for accessing {@link ScientificNameGroup} documents.
 * 
 * @author Ayco Holleman
 *
 */
public interface IScientificNameGroupAccess extends INbaAccess<ScientificNameGroup> {

	/**
	 * Returns {@link ScientificNameGroup} documents conforming to the provided
	 * query specification. This method hides {@link INbaAccess#query(QuerySpec)
	 * INbaAccess.query}. It takes a {@link ScientificNameGroupQuerySpec} rather than a
	 * {@link QuerySpec} argument, reflecting the fact that there a some
	 * (optional) extra features when specifying a query for
	 * {@code ScientificNameGroup} documents,
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<ScientificNameGroup> query(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException;

	/**
	 * Returns specimens conforming to the provided query specifications. The
	 * specimens are grouped by their scientific name. This method works very
	 * much like a generic {@link INbaAccess#query(QuerySpec) query} against the
	 * {@link ScientificNameGroup} index, but this method applies some extra
	 * post-processing to the query result.Both methods return
	 * {@code ScientificNameGroup} documents rather than {@link Specimen}
	 * documents. Multiple specimens may have the same scientific name, so one
	 * {@code ScientificNameGroup} document may contain multiple specimens (all
	 * specimens sharing the same scientific name are listed in one
	 * {@code ScientificNameGroup} document). With the generic {@code query}
	 * method, if a query is done on some specimen-related attribute, and a
	 * {@code ScientificNameGroup} document contains at least one specimen
	 * satisfying the query, then the entire {@code ScientificNameGroup}
	 * document is returned, even though it may contain other specimens that do
	 * not satisfy the query. The {@code getSpeciesWithSpecimens} method will
	 * purge those specimens from the {@code ScientificNameGroup} document.
	 * 
	 * @param querySpec
	 * @return
	 */
	QueryResult<ScientificNameGroup> getSpeciesWithSpecimens(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException;

}
