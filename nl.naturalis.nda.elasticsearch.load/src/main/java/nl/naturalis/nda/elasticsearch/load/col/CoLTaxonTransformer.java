package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.domain.TaxonomicRank.*;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;

/**
 * The transformer component in the CoL ETL cycle.
 * 
 * @author Ayco Holleman
 *
 */
class CoLTaxonTransformer extends AbstractCSVTransformer<CoLTaxonCsvField, ESTaxon> {

	private static final List<String> allowedTaxonRanks;

	static {
		allowedTaxonRanks = Arrays.asList("species", "infraspecies");
	}

	private String colYear;

	public CoLTaxonTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	public void setColYear(String colYear)
	{
		this.colYear = colYear;
	}

	@Override
	protected boolean skipRecord()
	{
		/*
		 * acceptedNameUsageID field is a foreign key to accepted name record.
		 * If it is set, the record is itself not an accepted name record, so we
		 * must skip it.
		 */
		return input.get(acceptedNameUsageID) != null;
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		String rank = input.get(taxonRank);
		if (!allowedTaxonRanks.contains(rank)) {
			stats.recordsSkipped++;
			if (logger.isDebugEnabled())
				debug("Ignoring taxon with rank \"%s\"", rank);
			return null;
		}
		try {
			stats.recordsAccepted++;
			stats.objectsProcessed++;
			ESTaxon taxon = new ESTaxon();
			taxon.setSourceSystem(SourceSystem.COL);
			taxon.setSourceSystemId(input.get(taxonID));
			taxon.setTaxonRank(input.get(taxonRank));
			taxon.setAcceptedName(getScientificName());
			taxon.setDefaultClassification(getClassification());
			addMonomials(taxon);
			setRecordURI(taxon);
			setTaxonDescription(taxon);
			stats.objectsAccepted++;
			return Arrays.asList(taxon);
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private void setTaxonDescription(ESTaxon taxon)
	{
		String descr = input.get(description);
		if (descr != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(descr);
			taxon.addDescription(td);
		}
	}

	private void setRecordURI(ESTaxon taxon)
	{
		String refs = input.get(references);
		if (refs == null) {
			if (!suppressErrors)
				warn("RecordURI not set. Missing Catalogue Of Life URL");
		}
		else {
			String[] chunks = refs.split("annual-checklist");
			if (chunks.length != 2) {
				if (!suppressErrors)
					warn("RecordURI not set. Could not parse URL: \"%s\"", refs);
			}
			else {
				StringBuilder url = new StringBuilder(96);
				url.append(chunks[0]);
				url.append("annual-checklist");
				url.append('/');
				url.append(colYear);
				url.append(chunks[1]);
				try {
					taxon.setRecordURI(URI.create(url.toString()));
				}
				catch (IllegalArgumentException e) {
					if (!suppressErrors)
						warn("RecordURI not set. Invalid URL: \"%s\"", refs);
				}
			}
		}
	}

	private DefaultClassification getClassification()
	{
		DefaultClassification dc = new DefaultClassification();
		dc.setKingdom(input.get(kingdom));
		dc.setPhylum(input.get(phylum));
		dc.setClassName(input.get(classRank));
		dc.setOrder(input.get(order));
		dc.setSuperFamily(input.get(superfamily));
		dc.setFamily(input.get(family));
		dc.setGenus(input.get(genericName));
		dc.setSubgenus(input.get(subgenus));
		dc.setSpecificEpithet(input.get(specificEpithet));
		dc.setInfraspecificEpithet(input.get(infraspecificEpithet));
		return dc;
	}

	private ScientificName getScientificName()
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(input.get(scientificName));
		sn.setGenusOrMonomial(input.get(genericName));
		sn.setSpecificEpithet(input.get(specificEpithet));
		sn.setInfraspecificEpithet(input.get(infraspecificEpithet));
		sn.setAuthorshipVerbatim(input.get(scientificNameAuthorship));
		sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
		return sn;
	}

	private static void addMonomials(ESTaxon taxon)
	{
		DefaultClassification dc = taxon.getDefaultClassification();
		Monomial m;
		if (dc.getKingdom() != null) {
			m = new Monomial(KINGDOM, dc.getKingdom());
			taxon.addMonomial(m);
		}
		if (dc.getPhylum() != null) {
			m = new Monomial(PHYLUM, dc.getPhylum());
			taxon.addMonomial(m);
		}
		if (dc.getClassName() != null) {
			m = new Monomial(CLASS, dc.getClassName());
			taxon.addMonomial(m);
		}
		if (dc.getOrder() != null) {
			m = new Monomial(ORDER, dc.getOrder());
			taxon.addMonomial(m);
		}
		if (dc.getSuperFamily() != null) {
			m = new Monomial(SUPER_FAMILY, dc.getSuperFamily());
			taxon.addMonomial(m);
		}
		if (dc.getFamily() != null) {
			m = new Monomial(FAMILY, dc.getFamily());
			taxon.addMonomial(m);
		}
		// Tribe not used in Catalogue of Life.
		if (dc.getGenus() != null) {
			m = new Monomial(GENUS, dc.getGenus());
			taxon.addMonomial(m);
		}
		if (dc.getSubgenus() != null) {
			m = new Monomial(SUBGENUS, dc.getSubgenus());
			taxon.addMonomial(m);
		}
		if (dc.getSpecificEpithet() != null) {
			m = new Monomial(SPECIES, dc.getSpecificEpithet());
			taxon.addMonomial(m);
		}
		if (dc.getInfraspecificEpithet() != null) {
			m = new Monomial(SUBSPECIES, dc.getInfraspecificEpithet());
			taxon.addMonomial(m);
		}
	}

}
