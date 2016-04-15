package nl.naturalis.nba.api;

import java.util.List;

import nl.naturalis.nba.api.model.Specimen;

public interface ISpecimenDAO {
	
	Specimen findById(String id);
	
	List<Specimen> findByUnitID(String unitID);

}
