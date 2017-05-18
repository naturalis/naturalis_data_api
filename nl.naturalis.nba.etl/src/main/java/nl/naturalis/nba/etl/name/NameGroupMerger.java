package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.dao.ScientificNameGroupDao;

class NameGroupMerger {

	private static final Logger logger = getLogger(NameGroupMerger.class);

	private int numCreated;
	private int numMerged;

	NameGroupMerger()
	{
	}

	Collection<ScientificNameGroup> merge(Collection<ScientificNameGroup> nameGroups)
	{
		HashSet<String> idSet = new HashSet<>(nameGroups.size());
		for (ScientificNameGroup sng : nameGroups) {
			idSet.add(sng.getId());
		}
		ScientificNameGroupDao sngDao = new ScientificNameGroupDao();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		ScientificNameGroup[] existing = sngDao.find(ids);
		HashMap<String, ScientificNameGroup> lookupTable = new HashMap<>(nameGroups.size() + 8, 1F);
		for (ScientificNameGroup sng : existing) {
			lookupTable.put(sng.getId(), sng);
		}
		boolean found = false;
		for (ScientificNameGroup newNameGroup : nameGroups) {
			if (newNameGroup.getId().equals("larus leucophthalmus")) {
				found=true;
			}
			ScientificNameGroup oldNameGroup = lookupTable.get(newNameGroup.getId());
			if (oldNameGroup == null) {
				lookupTable.put(newNameGroup.getId(), newNameGroup);
				++numCreated;
			}
			else {
				mergeNameGroups(oldNameGroup, newNameGroup);
				++numMerged;
			}
		}
		if(found) {
			logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			return lookupTable.values();
		}
		return lookupTable.values();
	}

	int getNumCreated()
	{
		return numCreated;
	}

	int getNumMerged()
	{
		return numMerged;
	}

	private static void mergeNameGroups(ScientificNameGroup sng1, ScientificNameGroup sng2)
	{
		if (sng2.getTaxa() != null) {
			for (SummaryTaxon st : sng2.getTaxa()) {
				if (!exists(st, sng1.getTaxa())) {
					sng1.addTaxon(st);
				}
				else {
					logger.warn("Encountered duplicate taxa for name group {}", sng1.getName());
				}
			}
			sng1.setTaxonCount(sng1.getTaxa().size());
		}
		if (sng2.getSpecimens() != null) {
			for (SummarySpecimen ss : sng2.getSpecimens()) {
				if (!exists(ss, sng1.getSpecimens())) {
					sng1.addSpecimen(ss);
				}
				else {
					logger.warn("Encountered duplicate specimens for name group {}",
							sng1.getName());
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
				logger.warn("Duplicate taxon: {}", st.getId());
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
				logger.warn("Duplicate specimen: {}", specimen.getId());
				return true;
			}
		}
		return false;
	}

}
