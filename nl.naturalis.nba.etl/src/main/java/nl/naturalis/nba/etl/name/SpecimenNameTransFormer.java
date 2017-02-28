package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.name.NameImportUtil.copySpecimen;
import static nl.naturalis.nba.etl.name.NameImportUtil.createName;
import static nl.naturalis.nba.etl.name.NameImportUtil.loadNameGroups;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

class NameTransformer extends AbstractDocumentTransformer<Specimen, NameGroup> {

	private HashMap<String, NameGroup> nameCache;
	private int batchSize;

	NameTransformer(ETLStatistics stats, int batchSize)
	{
		super(stats);
		this.batchSize = batchSize;
		this.nameCache = new HashMap<>(batchSize + 8, 1F);
	}

	@Override
	protected String getObjectID()
	{
		return input.getId();
	}

	@Override
	protected List<NameGroup> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		Specimen specimen = input;
		List<SpecimenIdentification> identifications = specimen.getIdentifications();
		for (SpecimenIdentification si : identifications) {
			String name = createName(si);
			NameGroup group = nameCache.get(name);
			group.addSpecimen(copySpecimen(specimen));
			stats.objectsAccepted++;
		}
		/*
		 * This class only nominally fits into the ETL architectue of the NBA.
		 * Its output (the return value of this method) is not directly fed into
		 * a Loader. Instead, a custom Loader class (NameGroupUpserter) is used
		 * for indexing, and we feed it with the values in the nameCache.
		 */
		return null;
	}

	void prepareForBatch(List<Specimen> specimens)
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

	Collection<NameGroup> getNameGroups()
	{
		return nameCache.values();
	}

}
