package nl.naturalis.nba.api;

import java.util.List;

import nl.naturalis.nba.api.model.Specimen;

public interface ISpecimenDAO {

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
	 * specified to be unique across all of the NBA's data sources, a list of
	 * specimens is returned. If no specimen with the specified UnitID exists,
	 * an empty list is returned.
	 * 
	 * @param unitID
	 *            The UnitID of the specimen occurence
	 * @return
	 */
	List<Specimen> findByUnitID(String unitID);

}
