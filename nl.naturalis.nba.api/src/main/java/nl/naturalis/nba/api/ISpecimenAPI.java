package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies methods for accessing specimen-related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ISpecimenAPI {

	/**
	 * Returns the {@link Specimen} with the specified system id, or
	 * {@code null} if there is no such specimen.
	 * 
	 * @param id
	 *            The NBA system ID of the specimen
	 * @return
	 */
	Specimen find(String id);

	/**
	 * Retrieves a {@link Specimen} by its UnitID. Since the UnitID is not
	 * strictly specified to be unique across all of the NBA's data sources, a
	 * theoretical chance exists that multiple specimens are retrieved for a
	 * given UnitID. Therefore this method returns an array of specimens. If no
	 * specimen with the specified UnitID exists, a zero-length array is
	 * returned.
	 * 
	 * @param unitID
	 *            The UnitID of the specimen occurence
	 * @return
	 */
	Specimen[] findByUnitID(String unitID);

	/**
	 * Returns whether or not the specified string is a valid UnitID
	 * (i&#46;e&#46; is the UnitID of at least one specimen record).
	 * 
	 * @param unitID
	 * @return
	 */
	boolean exists(String unitID);

	/**
	 * Returns specimens according to the provided query specification.
	 * 
	 * @param querySpec
	 * @return
	 */
	Specimen[] query(QuerySpec querySpec) throws InvalidQueryException;
	

}
