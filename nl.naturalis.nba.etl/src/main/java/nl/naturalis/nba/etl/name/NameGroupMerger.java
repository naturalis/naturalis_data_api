package nl.naturalis.nba.etl.name;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.dao.ScientificNameGroupDao;

class NameGroupMerger {

	private Collection<ScientificNameGroup> nameGroups;

	NameGroupMerger(Collection<ScientificNameGroup> nameGroups)
	{
		this.nameGroups = nameGroups;
	}

	Collection<ScientificNameGroup> merge()
	{
		HashSet<String> ids = new HashSet<>(nameGroups.size());
		for (ScientificNameGroup sng : nameGroups) {
			ids.add(sng.getId());
		}
		ScientificNameGroupDao sngDao = new ScientificNameGroupDao();
		ScientificNameGroup[] existing = sngDao.find(ids.toArray(new String[ids.size()]));
		HashMap<String, ScientificNameGroup> lookupTable = new HashMap<>(nameGroups.size() + 8, 1F);
		for (ScientificNameGroup sng : existing) {
			lookupTable.put(sng.getId(), sng);
		}
		for (ScientificNameGroup newNameGroup : nameGroups) {
			ScientificNameGroup oldNameGroup = lookupTable.get(newNameGroup.getId());
			if (oldNameGroup == null) {
				lookupTable.put(newNameGroup.getId(), newNameGroup);
			}
			else {
				mergeNameGroups(oldNameGroup, newNameGroup);
			}
		}
		return lookupTable.values();
	}

	private static void mergeNameGroups(ScientificNameGroup sng1, ScientificNameGroup sng2)
	{
		if (sng2.getTaxa() != null) {
			for (SummaryTaxon st : sng2.getTaxa()) {
				if (!exists(st, sng1.getTaxa())) {
					sng1.addTaxon(st);
				}
			}
			sng1.setTaxonCount(sng1.getTaxa().size());
		}
		if (sng2.getSpecimens() != null) {
			for (SummarySpecimen ss : sng2.getSpecimens()) {
				if (!exists(ss, sng1.getSpecimens())) {
					sng1.addSpecimen(ss);
				}
			}
			sng1.setSpecimenCount(sng1.getSpecimens().size());
		}
	}

	private static boolean exists(SummaryTaxon taxon, List<SummaryTaxon> taxa)
	{
		if (taxa == null) {
			return false;
		}
		for (SummaryTaxon st : taxa) {
			if (st.getId().equals(taxon.getId())) {
				return true;
			}
		}
		return false;
	}

	private static boolean exists(SummarySpecimen specimen, List<SummarySpecimen> specimens)
	{
		if (specimens == null) {
			return false;
		}
		for (SummarySpecimen ss : specimens) {
			if (ss.getId().equals(specimen.getId())) {
				return true;
			}
		}
		return false;
	}

}
