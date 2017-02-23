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

class NameTransformer extends AbstractDocumentTransformer<Specimen, NameGroup> {

	private HashMap<String, NameGroup> nameCache;
	private HashMap<String, List<Taxon>> taxonCache;

	NameTransformer(ETLStatistics stats)
	{
		super(stats);
		this.nameCache = new HashMap<>(NameImporter.BATCH_SIZE * 4);
		this.taxonCache = new HashMap<>(NameImporter.BATCH_SIZE);
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
				String fsn = si.getScientificName().getFullScientificName();
				if (fsn == null) {
					//warn("Missing scientific name");
					stats.objectsRejected++;
					continue;
				}
				NameGroup sns = nameCache.get(fsn);
				addSpecimen(sns, si);
				addTaxa(sns, fsn);
				if (!names.contains(fsn)) {
					result.add(sns);
					names.add(fsn);
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

	private void addTaxa(NameGroup sns, String fullScientificName)
	{
//		List<Taxon> taxa = taxonCache.get(fullScientificName);
//		if (taxa == null) {
//			return;
//		}
//		for (Taxon taxon : taxa) {
//			TaxonSummary summary = new TaxonSummary();
//			summary.setId(taxon.getId());
//			summary.setSourceSystem(taxon.getSourceSystem().getName());
//			summary.setRank(taxon.getTaxonRank());
//			sns.addTaxon(summary);
//			if (taxon.getVernacularNames() != null) {
//				for (VernacularName vn : taxon.getVernacularNames()) {
//					sns.addVernacularName(vn.getName());
//				}
//			}
//			if (taxon.getSynonyms() != null) {
//				for (ScientificName sn : taxon.getSynonyms()) {
//					sns.addSynonym(sn.getFullScientificName());
//				}
//			}
//		}
	}

	void prepareForBatch(List<Specimen> specimens)
	{
		nameCache.clear();
		taxonCache.clear();
		/*
		 * Let's assume we have around 3 identification per specimen on average.
		 */
		Set<String> ids = new HashSet<>(NameImporter.BATCH_SIZE * 3);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				if (si.getScientificName().getFullScientificName() != null) {
					ids.add(si.getScientificName().getFullScientificName());
				}
			}
		}
		List<NameGroup> names = NameImportUtil.loadNames(ids);
		for (NameGroup name : names) {
			nameCache.put(name.getName(), name);
		}
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				String key = si.getScientificName().getFullScientificName();
				if (!nameCache.containsKey(key)) {
					nameCache.put(key, new NameGroup(key));
				}
			}
		}
		List<Taxon> taxa = NameImportUtil.loadTaxa(ids);
		for (Taxon taxon : taxa) {
			String key = taxon.getAcceptedName().getFullScientificName();
			List<Taxon> value = taxonCache.get(key);
			if (value == null) {
				value = new ArrayList<>(2); // Either COL or NSR
				taxonCache.put(key, value);
			}
			value.add(taxon);
		}
	}

}
