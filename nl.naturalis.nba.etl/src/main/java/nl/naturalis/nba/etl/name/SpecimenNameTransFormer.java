package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.name.NameImportUtil.copySpecimen;
import static nl.naturalis.nba.etl.name.NameImportUtil.createName;
import static nl.naturalis.nba.etl.name.NameImportUtil.loadNameGroups;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.SummarySpecimen;

class SpecimenNameTransformer {

	private static final Logger logger = getLogger(SpecimenNameTransformer.class);

	private HashMap<String, NameGroup> nameCache;
	private int batchSize;

	private int created;
	private int updated;

	SpecimenNameTransformer(int batchSize)
	{
		this.batchSize = batchSize;
		this.nameCache = new HashMap<>(batchSize + 8, 1F);
	}

	public Collection<NameGroup> transform(Collection<Specimen> specimens)
	{
		prepareForBatch(specimens);
		for (Specimen specimen : specimens) {
			transform(specimen);
		}
		return nameCache.values();
	}

	public int getNumCreated()
	{
		return created;
	}

	public int getNumUpdated()
	{
		return updated;
	}

	private void transform(Specimen specimen)
	{
		List<SpecimenIdentification> identifications = specimen.getIdentifications();
		for (SpecimenIdentification si : identifications) {
			String name = createName(si);
			NameGroup group = nameCache.get(name);
			group.addSpecimen(copySpecimen(specimen));
			group.setSpecimenCount(group.getSpecimens().size());
		}
	}
	
	private static boolean exists(Specimen specimen, NameGroup group) {
		if(group.getSpecimens()==null) {
			return false;
		}
		for(SummarySpecimen ss : group.getSpecimens()) {
			//if(ss.getUnitID())
		}
		return false;
	}

	private void prepareForBatch(Collection<Specimen> specimens)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing name groups");
		}
		nameCache.clear();
		/*
		 * Assume we have around 4 identifications per specimen
		 */
		Set<String> names = new HashSet<>(batchSize * 4);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				names.add(createName(si));
			}
		}
		List<NameGroup> groups = loadNameGroups(names);
		created += groups.size();
		updated += (names.size() - groups.size());
		if (logger.isDebugEnabled()) {
			logger.debug("NameGroup documents to be updated: {}", groups.size());
			logger.debug("NameGroup documents to be created: {}", (names.size() - groups.size()));
		}
		for (NameGroup group : groups) {
			nameCache.put(group.getName(), group);
		}
		/*
		 * Create new, empty NameGroup instances for remaining names
		 */
		for (String name : names) {
			if (!nameCache.containsKey(name)) {
				nameCache.put(name, new NameGroup(name));
			}
		}
	}

}
