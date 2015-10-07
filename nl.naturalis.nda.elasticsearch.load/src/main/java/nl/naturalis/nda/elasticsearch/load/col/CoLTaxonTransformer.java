package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.domain.TaxonomicRank.*;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
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
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;

import org.apache.commons.csv.CSVRecord;

/**
 * The transformer component in the CoL ETL cycle.
 * 
 * @author Ayco Holleman
 *
 */
class CoLTaxonTransformer extends AbstractCSVTransformer<ESTaxon> {

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
	protected String getObjectID()
	{
		return val(input.getRecord(), taxonID);
	}

	@Override
	protected List<ESTaxon> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		CSVRecord rec = input.getRecord();
		String rank = val(rec, taxonRank);
		if (!allowedTaxonRanks.contains(rank)) {
			stats.objectsSkipped++;
			if (logger.isDebugEnabled())
				debug("Ignoring taxon with rank \"%s\"", rank);
			return null;
		}
		ESTaxon taxon = new ESTaxon();
		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(val(rec, taxonID));
		taxon.setTaxonRank(val(rec, taxonRank));
		taxon.setAcceptedName(getScientificName(rec));
		taxon.setDefaultClassification(getClassification(rec));
		addMonomials(taxon);
		setRecordURI(taxon);
		setTaxonDescription(taxon);
		return Arrays.asList(taxon);
	}

	private void setTaxonDescription(ESTaxon taxon)
	{
		String descr = val(input.getRecord(), description);
		if (descr != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(descr);
			taxon.addDescription(td);
		}
	}

	private void setRecordURI(ESTaxon taxon)
	{
		String refs = val(input.getRecord(), references);
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

	private static DefaultClassification getClassification(CSVRecord record)
	{
		DefaultClassification dc = new DefaultClassification();
		dc.setKingdom(val(record, kingdom));
		dc.setPhylum(val(record, phylum));
		dc.setClassName(val(record, classRank));
		dc.setOrder(val(record, order));
		dc.setSuperFamily(val(record, superfamily));
		dc.setFamily(val(record, family));
		dc.setGenus(val(record, genericName));
		dc.setSubgenus(val(record, subgenus));
		dc.setSpecificEpithet(val(record, specificEpithet));
		dc.setInfraspecificEpithet(val(record, infraspecificEpithet));
		return dc;
	}

	private static ScientificName getScientificName(CSVRecord record)
	{
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, scientificName));
		sn.setGenusOrMonomial(val(record, genericName));
		sn.setSpecificEpithet(val(record, specificEpithet));
		sn.setInfraspecificEpithet(val(record, infraspecificEpithet));
		sn.setAuthorshipVerbatim(val(record, scientificNameAuthorship));
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
