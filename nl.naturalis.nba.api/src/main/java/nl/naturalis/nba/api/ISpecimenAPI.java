package nl.naturalis.nba.api;

import nl.naturalis.nba.api.model.Specimen;

/**
 * Specifies methods for accessing specimen-related data.
 * 
 * @author Ayco Holleman
 *
 */
public interface ISpecimenAPI {

	/**
	 * Returns the {@link Specimen} with the specified id, or {@code null} if
	 * there is no specified id.
	 * 
	 * @param id
	 *            The NBA system ID of the specimen
	 * @return
	 */
	Specimen findById(String id);

	/**
	 * Retrieves a {@link Specimen} by its UnitID. Since the UnitID is not
	 * strictly specified to be unique across all of the NBA's data sources, a
	 * theoretical chance exists that multiple specimens are retrieved.
	 * Therefore this method returns an array of specimens. If no specimen with
	 * the specified UnitID exists, an empty list is returned.
	 * 
	 * @param unitID
	 *            The UnitID of the specimen occurence
	 * @return
	 */
	Specimen[] findByUnitID(String unitID);

}
