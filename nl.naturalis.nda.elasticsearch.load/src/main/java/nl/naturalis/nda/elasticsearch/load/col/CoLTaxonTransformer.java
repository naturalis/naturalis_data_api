package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.domain.TaxonomicRank.*;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.ival;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.col.CoLTaxonCsvField.*;
import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

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
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.CSVTransformer;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

class CoLTaxonTransformer implements CSVTransformer<ESTaxon> {

	static Logger logger = Registry.getInstance().getLogger(CoLTaxonTransformer.class);
	static List<String> allowedTaxonRanks = Arrays.asList("species", "infraspecies");

	private String objectID;
	private int lineNo;
	private boolean suppressErrors;
	private String colYear;

	public CoLTaxonTransformer()
	{
	}

	public void setColYear(String colYear)
	{
		this.colYear = colYear;
	}

	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	@Override
	public List<ESTaxon> transform(CSVRecordInfo info)
	{

		CSVRecord record = info.getRecord();
		lineNo = info.getLineNumber();
		objectID = val(record, taxonID);

		if (ival(record, acceptedNameUsageID) == 0) {
			// TODO: increase skipped records counter!
			return null;
		}

		String rank = val(record, taxonRank);
		if (!allowedTaxonRanks.contains(rank)) {
			if (logger.isDebugEnabled())
				debug("Ignoring taxon with rank \"%s\"", rank);
			return null;
		}

		ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(val(record, taxonID));

		String refs = val(record, references);
		if (refs == null) {
			if (!suppressErrors)
				warn("RecordURI not set. Missing Catalogue Of Life URL");
		}
		else {
			String[] chunks = refs.split("annual-checklist");
			if (chunks.length != 2) {
				if (!suppressErrors)
					warn("RecordURI not set. Cannot parse URL: \"%s\"", refs);
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
		taxon.setTaxonRank(val(record, taxonRank));

		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, scientificName));
		sn.setGenusOrMonomial(val(record, genericName));
		sn.setSpecificEpithet(val(record, specificEpithet));
		sn.setInfraspecificEpithet(val(record, infraspecificEpithet));
		sn.setAuthorshipVerbatim(val(record, scientificNameAuthorship));
		sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
		taxon.setAcceptedName(sn);

		DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);

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

		addMonomials(taxon);

		String descr = val(record, description);
		if (descr != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(descr);
			taxon.addDescription(td);
		}

		return Arrays.asList(taxon);
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

	@SuppressWarnings("unused")
	private void error(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.error(msg);
	}

	private void warn(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.warn(msg);
	}

	@SuppressWarnings("unused")
	private void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.info(msg);
	}

	private void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	private String messagePrefix()
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}
