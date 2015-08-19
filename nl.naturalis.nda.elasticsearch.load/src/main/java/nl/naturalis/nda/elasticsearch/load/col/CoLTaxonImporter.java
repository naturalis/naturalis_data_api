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
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

import java.io.IOException;
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
import nl.naturalis.nda.elasticsearch.load.CSVImportUtil;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.Registry;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

public class CoLTaxonImporter extends CSVExtractor<ESTaxon> {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
			String dwcaDir = Registry.getInstance().getConfig().required("col.csv_dir");
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
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

	private static final Logger logger = Registry.getInstance().getLogger(CoLTaxonImporter.class);
	private static final String ANNUAL_CHECKLIST_URL_COMPONENT = "annual-checklist";
	private static final List<String> ALLOWED_TAXON_RANKS = Arrays.asList("species", "infraspecies");

	private final String colYear;


	public CoLTaxonImporter(Index index)
	{
		super(index, LUCENE_TYPE_TAXON);
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty(CoLImportAll.SYSPROP_BATCHSIZE, "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty(CoLImportAll.SYSPROP_MAXRECORDS, "0");
		setMaxRecords(Integer.parseInt(prop));		
		colYear = Registry.getInstance().getConfig().required("col.year");
	}


	@Override
	public void importCsv(String path) throws IOException
	{
		index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.COL.getCode());
		super.importCsv(path);
	}


	@Override
	protected List<ESTaxon> transfer(CSVRecord record, String csvRecord, int lineNo)
	{

		String taxonRank = CSVImportUtil.val(record, CsvField.taxonRank.ordinal());
		if (!ALLOWED_TAXON_RANKS.contains(taxonRank)) {
			logger.debug(String.format("Ignoring taxon with rank \"%s\"", taxonRank));
			return null;
		}

		ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(CSVImportUtil.val(record, CsvField.taxonID.ordinal()));

		String references = CSVImportUtil.val(record, CsvField.references.ordinal());
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
		taxon.setTaxonRank(CSVImportUtil.val(record, CsvField.taxonRank.ordinal()));

		ScientificName sn = new ScientificName();
		sn.setFullScientificName(CSVImportUtil.val(record, CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(CSVImportUtil.val(record, CsvField.genericName.ordinal()));
		sn.setSpecificEpithet(CSVImportUtil.val(record, CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(CSVImportUtil.val(record, CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(CSVImportUtil.val(record, CsvField.scientificNameAuthorship.ordinal()));
		sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
		taxon.setAcceptedName(sn);

		DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);

		dc.setKingdom(CSVImportUtil.val(record, CsvField.kingdom.ordinal()));
		dc.setPhylum(CSVImportUtil.val(record, CsvField.phylum.ordinal()));
		dc.setClassName(CSVImportUtil.val(record, CsvField.classRank.ordinal()));
		dc.setOrder(CSVImportUtil.val(record, CsvField.order.ordinal()));
		dc.setSuperFamily(CSVImportUtil.val(record, CsvField.superfamily.ordinal()));
		dc.setFamily(CSVImportUtil.val(record, CsvField.family.ordinal()));
		dc.setGenus(CSVImportUtil.val(record, CsvField.genericName.ordinal()));
		dc.setSubgenus(CSVImportUtil.val(record, CsvField.subgenus.ordinal()));
		dc.setSpecificEpithet(CSVImportUtil.val(record, CsvField.specificEpithet.ordinal()));
		dc.setInfraspecificEpithet(CSVImportUtil.val(record, CsvField.infraspecificEpithet.ordinal()));

		addMonomials(taxon);

		String description = CSVImportUtil.val(record, CsvField.description.ordinal());
		if (description != null) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(description);
			taxon.addDescription(td);
		}

		return Arrays.asList(taxon);
	}


	protected boolean skipRecord(CSVRecord record)
	{
		if (CSVImportUtil.ival(record, CsvField.acceptedNameUsageID.ordinal()) == 0) {
			return false;
		}
		return true;
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String id = CoLImportAll.ID_PREFIX + CSVImportUtil.val(record, CsvField.taxonID.ordinal());
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
