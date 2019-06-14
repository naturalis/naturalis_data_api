package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.api.model.TaxonomicRank.CLASS;
import static nl.naturalis.nba.api.model.TaxonomicRank.FAMILY;
import static nl.naturalis.nba.api.model.TaxonomicRank.GENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.KINGDOM;
import static nl.naturalis.nba.api.model.TaxonomicRank.ORDER;
import static nl.naturalis.nba.api.model.TaxonomicRank.PHYLUM;
import static nl.naturalis.nba.api.model.TaxonomicRank.SPECIES;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBGENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUPER_FAMILY;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.classRank;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.description;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.family;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.genericName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.infraspecificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.kingdom;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.order;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.phylum;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.references;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificNameAuthorship;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.specificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.subgenus;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.superfamily;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonRank;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonDescription;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.TransformUtil;

/**
 * The transformer component in the CoL ETL cycle.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
class CoLTaxonTransformer extends AbstractCSVTransformer<CoLTaxonCsvField, Taxon> {

	private static final List<String> allowedTaxonRanks;

	static {
		allowedTaxonRanks = Arrays.asList("species", "infraspecies");
	}

	private String colYear;
	private String[] testGenera;

	public CoLTaxonTransformer(ETLStatistics stats)
	{
		super(stats);
		testGenera = getTestGenera();
	}

	public void setColYear(String colYear)
	{
		this.colYear = colYear;
	}

	@Override
	protected boolean skipRecord()
	{
		/*
		 * The acceptedNameUsageID field is a foreign key to an accepted name record in
		 * the same CSV file. If the field is empty, it means the record is itself an
		 * accepted name record, so we must process it.
		 */
		if (input.get(acceptedNameUsageID) != null) {
			return true;
		}
		if (testGenera != null && !isTestSetGenus()) {
			return true;
		}
		return false;
	}

	@Override
	protected String getObjectID()
	{
		return input.get(taxonID);
	}

	@Override
	protected List<Taxon> doTransform()
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
			Taxon taxon = new Taxon();
			taxon.setId(getElasticsearchId(COL, objectID));
			taxon.setSourceSystem(COL);
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

	private void setTaxonDescription(Taxon taxon)
	{
		String descr = input.get(description);
		if (descr != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(descr);
			taxon.addDescription(td);
		}
	}

	private void setRecordURI(Taxon taxon)
	{
		String refs = input.get(references);
		if (refs == null) {
			if (!suppressErrors)
				warn("RecordURI not set. Missing Catalogue Of Life URL");
		}
		else {
			try {
				taxon.setRecordURI(URI.create(refs));
			} catch (IllegalArgumentException e) {
				if (!suppressErrors) warn("RecordURI not set. Invalid URL: \"%s\"", refs);
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
		sn.setSubgenus(input.get(subgenus));
		sn.setSpecificEpithet(input.get(specificEpithet));
		sn.setInfraspecificEpithet(input.get(infraspecificEpithet));
		sn.setAuthorshipVerbatim(input.get(scientificNameAuthorship));
		sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
		TransformUtil.setScientificNameGroup(sn);
		return sn;
	}

	private static void addMonomials(Taxon taxon)
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

	private boolean isTestSetGenus()
	{
		String genus = input.get(genericName);
		if (genus == null) {
			return false;
		}
		genus = genus.toLowerCase();
		for (String s : testGenera) {
			if (s.equals(genus)) {
				return true;
			}
		}
		return false;
	}

}
