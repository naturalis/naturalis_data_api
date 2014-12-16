package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.domain.TaxonomicRank.CLASS;
import static nl.naturalis.nda.domain.TaxonomicRank.FAMILY;
import static nl.naturalis.nda.domain.TaxonomicRank.GENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.KINGDOM;
import static nl.naturalis.nda.domain.TaxonomicRank.ORDER;
import static nl.naturalis.nda.domain.TaxonomicRank.PHYLUM;
import static nl.naturalis.nda.domain.TaxonomicRank.SPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBGENUS;
import static nl.naturalis.nda.domain.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nda.domain.TaxonomicRank.SUPER_FAMILY;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.TaxonomicStatus;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLTaxonImporter extends CSVImporter<ESTaxon> {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), DEFAULT_NDA_INDEX_NAME);

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_TAXON);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE_TAXON, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_TAXON)) {
				index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.COL.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
				index.addType(LUCENE_TYPE_TAXON, mapping);
			}
		}

		try {
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
			String dwcaDir = LoadUtil.getConfig().required("col.csv_dir");
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			index.getClient().close();
		}

	}

	//@formatter:off
	static enum CsvField {
		taxonID
		, identifier
		, datasetID
		, datasetName
		, acceptedNameUsageID
		, parentNameUsageID
		, taxonomicStatus
		, taxonRank
		, verbatimTaxonRank
		, scientificName
		, kingdom
		, phylum
		, classRank
		, order
		, superfamily
		, family
		, genericName
		, genus
		, subgenus
		, specificEpithet
		, infraspecificEpithet
		, scientificNameAuthorship
		, source
		, namePublishedIn
		, nameAccordingTo
		, modified
		, description
		, taxonConceptID
		, scientificNameID
		, references	
	}
	//@formatter:on

	static final Logger logger = LoggerFactory.getLogger(CoLTaxonImporter.class);
	static final String ID_PREFIX = "COL-";

	private static final String ANNUAL_CHECKLIST_URL_COMPONENT = "annual-checklist";
	private static final List<String> ALLOWED_TAXON_RANKS = Arrays.asList("species", "infraspecies");

	private final String colYear;


	public CoLTaxonImporter(Index index)
	{
		super(index, LUCENE_TYPE_TAXON);
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty("bulkRequestSize", "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty("maxRecords", "0");
		setMaxRecords(Integer.parseInt(prop));
		colYear = LoadUtil.getConfig().required("col.year");
	}


	@Override
	protected List<ESTaxon> transfer(CSVRecord record, String csvRecord, int lineNo)
	{

		String taxonRank = val(record, CsvField.taxonRank.ordinal());
		if (!ALLOWED_TAXON_RANKS.contains(taxonRank)) {
			logger.debug(String.format("Ignoring taxon with rank \"%s\"", taxonRank));
			return null;
		}

		ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(val(record, CsvField.taxonID.ordinal()));
		
		String references = val(record, CsvField.references.ordinal());
		if (references == null) {
			logger.warn("Missing URL for taxon " + taxon.getSourceSystemId());
		}
		else {
			String[] chunks = references.split(ANNUAL_CHECKLIST_URL_COMPONENT);
			if (chunks.length != 2) {
				logger.error("Unexpected URL: " + references);
			}
			else {
				String url = new StringBuilder(96).append(chunks[0]).append(ANNUAL_CHECKLIST_URL_COMPONENT).append('/').append(colYear)
						.append(chunks[1]).toString();
				try {
					taxon.setRecordURI(URI.create(url));
				}
				catch (IllegalArgumentException e) {
					logger.error(String.format("Invalid URL for taxon with id %s: \"%s\"", taxon.getSourceSystemId(), references));
				}
			}
		}
		taxon.setTaxonRank(val(record, CsvField.taxonRank.ordinal()));

		ScientificName sn = new ScientificName();
		sn.setFullScientificName(val(record, CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(val(record, CsvField.genericName.ordinal()));
		sn.setSpecificEpithet(val(record, CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(val(record, CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(val(record, CsvField.scientificNameAuthorship.ordinal()));
		sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
		taxon.setAcceptedName(sn);

		DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);

		dc.setKingdom(val(record, CsvField.kingdom.ordinal()));
		dc.setPhylum(val(record, CsvField.phylum.ordinal()));
		dc.setClassName(val(record, CsvField.classRank.ordinal()));
		dc.setOrder(val(record, CsvField.order.ordinal()));
		dc.setSuperFamily(val(record, CsvField.superfamily.ordinal()));
		dc.setFamily(val(record, CsvField.family.ordinal()));
		dc.setGenus(val(record, CsvField.genericName.ordinal()));
		dc.setSubgenus(val(record, CsvField.subgenus.ordinal()));
		dc.setSpecificEpithet(val(record, CsvField.specificEpithet.ordinal()));
		dc.setInfraspecificEpithet(val(record, CsvField.infraspecificEpithet.ordinal()));

		addMonomials(taxon);

		String description = val(record, CsvField.description.ordinal());
		if (description != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(description);
			taxon.addDescription(td);
		}

		return Arrays.asList(taxon);
	}


	protected boolean skipRecord(CSVRecord record)
	{
		if (ival(record, CsvField.acceptedNameUsageID.ordinal()) == 0) {
			return false;
		}
		return true;
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String id = ID_PREFIX + val(record, CsvField.taxonID.ordinal());
		return Arrays.asList(id);
	}


	private static void addMonomials(ESTaxon taxon)
	{
		final DefaultClassification dc = taxon.getDefaultClassification();
		Monomial monomial;
		if (dc.getKingdom() != null) {
			monomial = new Monomial(KINGDOM, dc.getKingdom());
			taxon.addMonomial(monomial);
		}
		if (dc.getPhylum() != null) {
			monomial = new Monomial(PHYLUM, dc.getPhylum());
			taxon.addMonomial(monomial);
		}
		if (dc.getClassName() != null) {
			monomial = new Monomial(CLASS, dc.getClassName());
			taxon.addMonomial(monomial);
		}
		if (dc.getOrder() != null) {
			monomial = new Monomial(ORDER, dc.getOrder());
			taxon.addMonomial(monomial);
		}
		if (dc.getSuperFamily() != null) {
			monomial = new Monomial(SUPER_FAMILY, dc.getSuperFamily());
			taxon.addMonomial(monomial);
		}
		if (dc.getFamily() != null) {
			monomial = new Monomial(FAMILY, dc.getFamily());
			taxon.addMonomial(monomial);
		}
		// Tribe not used in Catalogue of Life.
		if (dc.getGenus() != null) {
			monomial = new Monomial(GENUS, dc.getGenus());
			taxon.addMonomial(monomial);
		}
		if (dc.getSubgenus() != null) {
			monomial = new Monomial(SUBGENUS, dc.getSubgenus());
			taxon.addMonomial(monomial);
		}
		if (dc.getSpecificEpithet() != null) {
			monomial = new Monomial(SPECIES, dc.getSpecificEpithet());
			taxon.addMonomial(monomial);
		}
		if (dc.getInfraspecificEpithet() != null) {
			monomial = new Monomial(SUBSPECIES, dc.getInfraspecificEpithet());
			taxon.addMonomial(monomial);
		}
	}

}
