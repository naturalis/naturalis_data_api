package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

class NameTransformer extends AbstractDocumentTransformer<Specimen, Name> {

	private final NameLoader loader;

	NameTransformer(ETLStatistics stats, NameLoader loader)
	{
		super(stats);
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return input.getId();
	}

	@Override
	protected List<Name> doTransform()
	{
		// No record-level validations, so:
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			List<Name> result = new ArrayList<>(input.getIdentifications().size());
			for (SpecimenIdentification si : input.getIdentifications()) {
				String fsn = si.getScientificName().getFullScientificName();
				if (fsn == null || fsn.trim().length() == 0) {
					/*
					 * It happens, but should it? Are empty full scientific
					 * names OK?
					 */
					warn("Missing scientific name");
					continue;
				}
				Name name = loader.findInQueue(fsn);
				if (name != null) {
					name.addSpecimenUnitID(input.getUnitID());
				}
				else {
					name = ESUtil.find(NAME, fsn);
					if (name == null) {
						name = new Name(fsn);
					}
					name.addSpecimenUnitID(input.getUnitID());
					result.add(name);
				}
			}
			return result;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

}
