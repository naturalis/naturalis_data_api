package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.SummaryObjectUtil.copySpecimen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.TaxonomicIdentification;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.etl.ETLRuntimeException;

class SpecimenNameTransformer {

	private static final Logger logger = getLogger(SpecimenNameTransformer.class);

	private int batchSize;

	private int created;
	private int updated;

	SpecimenNameTransformer(int batchSize)
	{
		this.batchSize = batchSize;
	}

	Collection<ScientificNameGroup> transform(Collection<Specimen> specimens)
	{
		HashMap<String, ScientificNameGroup> lookupTable = createLookupTable(specimens);
		if (lookupTable == null) {
			return null;
		}
		for (Specimen specimen : specimens) {
			List<SpecimenIdentification> identifications = specimen.getIdentifications();
			if (identifications != null) {
				for (TaxonomicIdentification si : identifications) {
					String name = si.getScientificName().getScientificNameGroup();
					ScientificNameGroup nameGroup = lookupTable.get(name);
					if (!exists(specimen, nameGroup)) {
						nameGroup.addSpecimen(copySpecimen(specimen, name));
						nameGroup.setSpecimenCount(nameGroup.getSpecimens().size());
					}
				}
			}
		}
		return lookupTable.values();
	}

	int getNumCreated()
	{
		return created;
	}

	int getNumUpdated()
	{
		return updated;
	}

	private static boolean exists(Specimen specimen, ScientificNameGroup group)
	{
		if (group.getSpecimens() == null) {
			return false;
		}
		for (SummarySpecimen ss : group.getSpecimens()) {
			if (ss.getUnitID().equals(specimen.getUnitID())) {
				return true;
			}
		}
		return false;
	}

	private HashMap<String, ScientificNameGroup> createLookupTable(Collection<Specimen> specimens)
	{
		HashSet<String> names = new HashSet<>(batchSize);
		for (Specimen specimen : specimens) {
			if (specimen.getIdentifications() != null) {
				for (TaxonomicIdentification si : specimen.getIdentifications()) {
					names.add(si.getScientificName().getScientificNameGroup());
				}
			}
		}
		if (names.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Current batch does not contain a single identified specimen");
			}
			return null;
		}
		if (names.size() > 1024) {
			String msg = "Too many unique names in current batch (must not exceed 1024)";
			throw new ETLRuntimeException(msg);
		}
		if (logger.isDebugEnabled()) {
			String fmt = "Number of unique names in current batch: {}";
			logger.debug(fmt, names.size());
		}
		List<ScientificNameGroup> groups = NameImportUtil.loadNameGroupsById(names);
		int newGroups = names.size() - groups.size();
		created += newGroups;
		updated += groups.size();
		HashMap<String, ScientificNameGroup> lookupTable = new HashMap<>(names.size() + 4, 1F);
		for (ScientificNameGroup group : groups) {
			lookupTable.put(group.getName(), group);
		}
		if (logger.isDebugEnabled() && newGroups != 0) {
			logger.debug("Initializing {} new name groups", newGroups);
		}
		for (String name : names) {
			if (!lookupTable.containsKey(name)) {
				lookupTable.put(name, new ScientificNameGroup(name));
			}
		}
		return lookupTable;
	}

}
