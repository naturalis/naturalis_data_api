package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.find;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameSummary;
import nl.naturalis.nba.api.model.ScientificNameSummary.SpecimenSummary;
import nl.naturalis.nba.api.model.ScientificNameSummary.TaxonSummary;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

class NameTransformer extends AbstractDocumentTransformer<Specimen, ScientificNameSummary> {

	private final NameLoader loader;

	NameTransformer(ETLStatistics stats, NameLoader loader)
	{
		super(stats);
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return input.getId();
	}

	@Override
	protected List<ScientificNameSummary> doTransform()
	{
		// No record-level validations, so:
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			List<ScientificNameSummary> result = new ArrayList<>(input.getIdentifications().size());
			for (SpecimenIdentification si : input.getIdentifications()) {
				String fsn = si.getScientificName().getFullScientificName();
				if (fsn == null) {
					/*
					 * It happens, but should it? Are empty full scientific
					 * names OK?
					 */
					warn("Missing scientific name");
					continue;
				}
				ScientificNameSummary sns = loader.findInQueue(fsn);
				boolean queued = true;
				if (sns == null) {
					queued = false;
					sns = find(SCIENTIFIC_NAME_SUMMARY, fsn);
					if (sns == null) {
						sns = new ScientificNameSummary(fsn);
					}
				}
				addSpecimen(sns, si);
				addTaxa(sns, fsn);
				if (!queued) {
					result.add(sns);
				}
			}
			return result;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private void addSpecimen(ScientificNameSummary name, SpecimenIdentification si)
	{
		Specimen specimen = input;
		SpecimenSummary summary = new SpecimenSummary();
		summary.setId(specimen.getId());
		summary.setUnitID(specimen.getUnitID());
		summary.setSourceSystem(specimen.getSourceSystem().getName());
		summary.setRecordBasis(specimen.getRecordBasis());
		name.addSpecimen(summary);
		DefaultClassification dc = si.getDefaultClassification();
		if (dc != null) {
			if (dc.getKingdom() != null)
				name.addKingdom(dc.getKingdom());
			if (dc.getPhylum() != null)
				name.addPhylum(dc.getPhylum());
			if (dc.getClassName() != null)
				name.addClass(dc.getClassName());
			if (dc.getOrder() != null)
				name.addOrder(dc.getOrder());
			if (dc.getFamily() != null)
				name.addFamily(dc.getFamily());
			if (dc.getGenus() != null)
				name.addGenus(dc.getGenus());
			if (dc.getSpecificEpithet() != null)
				name.addSpecificEpithet(dc.getSpecificEpithet());
			if (dc.getInfraspecificEpithet() != null)
				name.addInfraspecificEpithet(dc.getInfraspecificEpithet());
		}
		ScientificName sn = si.getScientificName();
		if (sn != null) {
			if (sn.getGenusOrMonomial() != null)
				name.addGenus(sn.getGenusOrMonomial());
			if (sn.getSpecificEpithet() != null)
				name.addSpecificEpithet(sn.getSpecificEpithet());
			if (sn.getInfraspecificEpithet() != null)
				name.addInfraspecificEpithet(sn.getInfraspecificEpithet());
		}
		if (si.getVernacularNames() != null) {
			for (VernacularName vn : si.getVernacularNames()) {
				name.addVernacularName(vn.getName());
			}
		}
	}

	private static void addTaxa(ScientificNameSummary sns, String fullScientificName)
	{
		List<Taxon> taxa = find(TAXON, "acceptedName.fullScientificName", fullScientificName);
		for (Taxon taxon : taxa) {
			TaxonSummary summary = new TaxonSummary();
			summary.setId(taxon.getId());
			summary.setSourceSystem(taxon.getSourceSystem().getName());
			summary.setRank(taxon.getTaxonRank());
			sns.addTaxon(summary);
			if (taxon.getVernacularNames() != null) {
				for (VernacularName vn : taxon.getVernacularNames()) {
					sns.addVernacularName(vn.getName());
				}
			}
			if (taxon.getSynonyms() != null) {
				for (ScientificName sn : taxon.getSynonyms()) {
					sns.addSynonym(sn.getFullScientificName());
				}
			}
		}
	}

}
