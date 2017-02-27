package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.name.NameImportUtil.copySpecimen;
import static nl.naturalis.nba.etl.name.NameImportUtil.createName;
import static nl.naturalis.nba.etl.name.NameImportUtil.loadNameGroups;

import java.util.ArrayList;
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

	NameTransformer(ETLStatistics stats)
	{
		super(stats);
		this.nameCache = new HashMap<>(NameImporter.BATCH_SIZE * 4);
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
		List<NameGroup> result = new ArrayList<>(identifications.size());
		Set<String> names = new HashSet<>(identifications.size());
		for (SpecimenIdentification si : identifications) {
			String name = createName(si);
			NameGroup group = nameCache.get(name);
			group.addSpecimen(copySpecimen(specimen));
			if (!names.contains(name)) {
				result.add(group);
				names.add(name);
			}
			stats.objectsAccepted++;
		}
		return result;
	}

	void prepareForBatch(List<Specimen> specimens)
	{
		nameCache.clear();
		/*
		 * Assume we have around 3 identifications per specimen
		 */
		Set<String> names = new HashSet<>(NameImporter.BATCH_SIZE * 3);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				names.add(createName(si));
			}
		}
		List<NameGroup> groups = loadNameGroups(names);
		for (NameGroup group : groups) {
			nameCache.put(group.getName(), group);
		}
		for (String name : names) {
			if (!nameCache.containsKey(name)) {
				nameCache.put(name, new NameGroup(name));
			}
		}
	}

}
