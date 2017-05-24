package nl.naturalis.nba.dao.format.calc;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.EntityObject;

class SpecimenCalculatorCache {

	static final SpecimenCalculatorCache instance = new SpecimenCalculatorCache();

	private EntityObject lastEntity;
	private Specimen lastSpecimen;

	Specimen getSpecimen(EntityObject entity)
	{
		if (entity == lastEntity) {
			return lastSpecimen;
		}
		lastEntity = entity;
		lastSpecimen = JsonUtil.convert(entity.getData(), Specimen.class);
		return lastSpecimen;
	}

	SpecimenIdentification getPreferredOrFirstIdentitifcation(EntityObject entity)
	{
		/*
		 * Assume identification are already sorted (preferred first). This is
		 * indeed done during import.
		 */
		Specimen specimen = getSpecimen(entity);
		return specimen.getIdentifications().iterator().next();
	}

	private SpecimenCalculatorCache()
	{
	}

}
