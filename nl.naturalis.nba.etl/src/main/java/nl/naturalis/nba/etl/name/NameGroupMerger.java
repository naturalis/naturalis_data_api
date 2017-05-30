package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;
import nl.naturalis.nba.dao.ScientificNameGroupDao;

class NameGroupMerger {

	private static final Logger logger = getLogger(NameGroupMerger.class);

	private static final String ERR_DUP_TAXON = "Duplicate taxon id \"{}\" for name group {}";
	private static final String ERR_DUP_SPECIMEN = "Duplicate specimen id \"{}\" for name group {}";

	private int numCreated;
	private int numMerged;

	NameGroupMerger()
	{
	}

	Collection<ScientificNameGroup> merge(Collection<ScientificNameGroup> nameGroups)
	{
		if(logger.isDebugEnabled()) {
			logger.debug("Merging name groups");
		}
		HashSet<String> idSet = new HashSet<>(nameGroups.size());
		for (ScientificNameGroup sng : nameGroups) {
			idSet.add(sng.getId());
		}
		ScientificNameGroupDao sngDao = new ScientificNameGroupDao();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		if (logger.isDebugEnabled()) {
			logger.debug("Number of unique name groups in current batch: {}", ids.length);
			logger.debug("Searching for already indexed name groups among {}", ids.length);
		}
		ScientificNameGroup[] oldNameGroups = sngDao.find(ids);
		if (logger.isDebugEnabled()) {
			logger.debug("Creating lookup table for {} already indexed name groups",
					oldNameGroups.length);
		}
		HashMap<String, ScientificNameGroup> lookupTable = new HashMap<>(nameGroups.size() + 8, 1F);
		for (ScientificNameGroup oldNameGroup : oldNameGroups) {
			lookupTable.put(oldNameGroup.getId(), oldNameGroup);
		}
		for (ScientificNameGroup newNameGroup : nameGroups) {
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

	private static void mergeNameGroups(ScientificNameGroup oldNameGroup,
			ScientificNameGroup newNameGroup)
	{
		if (newNameGroup.getTaxa() != null) {
			for (SummaryTaxon taxon : newNameGroup.getTaxa()) {
				if (!isNewTaxon(taxon, oldNameGroup)) {
					oldNameGroup.addTaxon(taxon);
				}
			}
			oldNameGroup.setTaxonCount(oldNameGroup.getTaxa().size());
		}
		if (newNameGroup.getSpecimens() != null) {
			for (SummarySpecimen specimen : newNameGroup.getSpecimens()) {
				if (isNewSpecimen(specimen, oldNameGroup)) {
					oldNameGroup.addSpecimen(specimen);
				}
			}
			oldNameGroup.setSpecimenCount(oldNameGroup.getSpecimens().size());
		}
	}

	private static boolean isNewTaxon(SummaryTaxon taxon, ScientificNameGroup sng)
	{
		if (sng.getTaxa() == null) {
			return true;
		}
		for (SummaryTaxon oldTaxon : sng.getTaxa()) {
			if (oldTaxon.getId().equals(taxon.getId())) {
				logger.warn(ERR_DUP_TAXON, taxon.getId(), sng);
				return false;
			}
		}
		return true;
	}

	private static boolean isNewSpecimen(SummarySpecimen specimen, ScientificNameGroup sng)
	{
		if (sng.getSpecimens() == null) {
			return true;
		}
		for (SummarySpecimen oldSpecimen : sng.getSpecimens()) {
			if (oldSpecimen.getId().equals(specimen.getId())) {
				logger.warn(ERR_DUP_SPECIMEN, specimen.getId(), sng.getName());
				return false;
			}
		}
		return true;
	}

}
