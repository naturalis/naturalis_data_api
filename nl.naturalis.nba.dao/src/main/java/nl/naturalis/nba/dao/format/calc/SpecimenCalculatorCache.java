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
		Specimen specimen = getSpecimen(entity);
		for (SpecimenIdentification si : specimen.getIdentifications()) {
			if (si.isPreferred()) {
				return si;
			}
		}
		return specimen.getIdentifications().iterator().next();
	}

	private SpecimenCalculatorCache()
	{
	}

}
