package nl.naturalis.nba.etl.name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

import static nl.naturalis.nba.etl.name.NameImportUtil.*;

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
		// No record-level validations, so:
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			List<SpecimenIdentification> identifications = input.getIdentifications();
			List<NameGroup> result = new ArrayList<>(identifications.size());
			Set<String> names = new HashSet<>(identifications.size());
			for (SpecimenIdentification si : identifications) {
				String name = createName(si.getScientificName());
				NameGroup sns = nameCache.get(name);
				addSpecimen(sns, si);
				if (!names.contains(name)) {
					result.add(sns);
					names.add(name);
				}
				stats.objectsAccepted++;
			}
			return result;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private void addSpecimen(NameGroup name, SpecimenIdentification si)
	{
		//		Specimen specimen = input;
		//		SpecimenSummary summary = new SpecimenSummary();
		//		summary.setId(specimen.getId());
		//		summary.setUnitID(specimen.getUnitID());
		//		summary.setSourceSystem(specimen.getSourceSystem().getName());
		//		summary.setRecordBasis(specimen.getRecordBasis());
		//		name.addSpecimen(summary);
		//		DefaultClassification dc = si.getDefaultClassification();
		//		if (dc != null) {
		//			if (dc.getKingdom() != null)
		//				name.addKingdom(dc.getKingdom());
		//			if (dc.getPhylum() != null)
		//				name.addPhylum(dc.getPhylum());
		//			if (dc.getClassName() != null)
		//				name.addClass(dc.getClassName());
		//			if (dc.getOrder() != null)
		//				name.addOrder(dc.getOrder());
		//			if (dc.getFamily() != null)
		//				name.addFamily(dc.getFamily());
		//			if (dc.getGenus() != null)
		//				name.addGenus(dc.getGenus());
		//			if (dc.getSubgenus() != null)
		//				name.addSubgenus(dc.getSubgenus());
		//			if (dc.getSpecificEpithet() != null)
		//				name.addSpecificEpithet(dc.getSpecificEpithet());
		//			if (dc.getInfraspecificEpithet() != null)
		//				name.addInfraspecificEpithet(dc.getInfraspecificEpithet());
		//		}
		//		ScientificName sn = si.getScientificName();
		//		if (sn != null) {
		//			if (sn.getGenusOrMonomial() != null)
		//				name.addGenus(sn.getGenusOrMonomial());
		//			if (sn.getSpecificEpithet() != null)
		//				name.addSpecificEpithet(sn.getSpecificEpithet());
		//			if (sn.getInfraspecificEpithet() != null)
		//				name.addInfraspecificEpithet(sn.getInfraspecificEpithet());
		//		}
		//		if (si.getVernacularNames() != null) {
		//			for (VernacularName vn : si.getVernacularNames()) {
		//				name.addVernacularName(vn.getName());
		//			}
		//		}
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
				names.add(createName(si.getScientificName()));
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
