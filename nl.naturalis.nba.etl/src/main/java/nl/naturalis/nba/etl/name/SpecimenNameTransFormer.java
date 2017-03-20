package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.name.NameImportUtil.copySpecimen;
import static nl.naturalis.nba.etl.name.NameImportUtil.loadNameGroupsById;
import static nl.naturalis.nba.etl.name.NameImportUtil.loadNameGroupsByName;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;

class SpecimenNameTransformer {

	private static final boolean LOAD_NAME_GROUPS_BY_ID = false;

	private static final Logger logger = getLogger(SpecimenNameTransformer.class);

	private HashMap<String, ScientificNameGroup> nameCache;
	private int batchSize;

	private int created;
	private int updated;

	SpecimenNameTransformer(int batchSize)
	{
		if (batchSize > 1024) {
			throw new IllegalArgumentException("Batch size must not exceed 1024");
			// Because the maximum number of terms in a terms/ids query is 1024
		}
		this.batchSize = batchSize;
		this.nameCache = new HashMap<>(batchSize + 8, 1F);
	}

	Collection<ScientificNameGroup> transform(Collection<Specimen> specimens)
	{
		initializeNameGroups(specimens);
		for (Specimen specimen : specimens) {
			transform(specimen);
		}
		return nameCache.values();
	}

	int getNumCreated()
	{
		return created;
	}

	int getNumUpdated()
	{
		return updated;
	}

	private void transform(Specimen specimen)
	{
		List<SpecimenIdentification> identifications = specimen.getIdentifications();
		for (SpecimenIdentification si : identifications) {
			ScientificNameGroup group = nameCache.get(si.getScientificNameGroup());
			if (!exists(specimen, group)) {
				group.addSpecimen(copySpecimen(specimen, si.getScientificNameGroup()));
				group.setSpecimenCount(group.getSpecimens().size());
			}
		}
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

	private void initializeNameGroups(Collection<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing name groups");
		}
		nameCache.clear();
		/*
		 * Assume we have around 3 unique identifications per specimen
		 */
		Set<String> names = new HashSet<>(batchSize * 3);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				names.add(si.getScientificNameGroup());
			}
		}
		List<ScientificNameGroup> groups;
		if (LOAD_NAME_GROUPS_BY_ID)
			groups = loadNameGroupsById(names);
		else
			groups = loadNameGroupsByName(names);
		created += groups.size();
		updated += (names.size() - groups.size());
		if (logger.isDebugEnabled()) {
			logger.debug("ScientificNameGroup documents to be updated: {}", groups.size());
			logger.debug("ScientificNameGroup documents to be created: {}",
					(names.size() - groups.size()));
		}
		for (ScientificNameGroup group : groups) {
			nameCache.put(group.getName(), group);
		}
		/*
		 * Create new, empty ScientificNameGroup instances for remaining names
		 */
		for (String name : names) {
			if (!nameCache.containsKey(name)) {
				nameCache.put(name, new ScientificNameGroup(name));
			}
		}
	}

}
