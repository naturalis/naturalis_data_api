package nl.naturalis.nda.elasticsearch.load.col;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.CSVImporter;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.*;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLTaxonImporter extends CSVImporter<ESTaxon> {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		String dwcaDir = System.getProperty("dwcaDir");
		String rebuild = System.getProperty("rebuild", "false");
		if (dwcaDir == null) {
			throw new Exception("Missing property \"dwcaDir\"");
		}

		IndexNative index = new IndexNative(LoadUtil.getDefaultClient(), DEFAULT_NDA_INDEX_NAME);
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_TAXON);
			Thread.sleep(2000);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE_TAXON, mapping);
		}
		try {
			CoLTaxonImporter importer = new CoLTaxonImporter(index);
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


	public CoLTaxonImporter(Index index)
	{
		super(index, LUCENE_TYPE_TAXON);
		setSpecifyId(true);
		setSpecifyParent(false);
		String prop = System.getProperty("bulkRequestSize", "1000");
		setBulkRequestSize(Integer.parseInt(prop));
		prop = System.getProperty("maxRecords", "0");
		setMaxRecords(Integer.parseInt(prop));
	}


	@Override
	protected List<ESTaxon> transfer(CSVRecord record)
	{
		final ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(val(record, CsvField.taxonID.ordinal()));
		taxon.setTaxonRank(val(record, CsvField.taxonRank.ordinal()));

		final ScientificName sn = new ScientificName();
		taxon.setAcceptedName(sn);
		sn.setFullScientificName(val(record, CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(val(record, CsvField.genericName.ordinal()));
		sn.setSpecificEpithet(val(record, CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(val(record, CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(val(record, CsvField.scientificNameAuthorship.ordinal()));

		final DefaultClassification dc = new DefaultClassification();
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
			monomial = new Monomial(DefaultClassification.Rank.KINGDOM.toString(), dc.getKingdom());
			taxon.addMonomial(monomial);
		}
		if (dc.getPhylum() != null) {
			monomial = new Monomial(DefaultClassification.Rank.PHYLUM.toString(), dc.getPhylum());
			taxon.addMonomial(monomial);
		}
		if (dc.getClassName() != null) {
			monomial = new Monomial(DefaultClassification.Rank.CLASS.toString(), dc.getClassName());
			taxon.addMonomial(monomial);
		}
		if (dc.getOrder() != null) {
			monomial = new Monomial(DefaultClassification.Rank.ORDER.toString(), dc.getOrder());
			taxon.addMonomial(monomial);
		}
		if (dc.getSuperFamily() != null) {
			monomial = new Monomial(DefaultClassification.Rank.SUPER_FAMILY.toString(), dc.getSuperFamily());
			taxon.addMonomial(monomial);
		}
		if (dc.getFamily() != null) {
			monomial = new Monomial(DefaultClassification.Rank.FAMILY.toString(), dc.getFamily());
			taxon.addMonomial(monomial);
		}
		if (dc.getGenus() != null) {
			monomial = new Monomial(DefaultClassification.Rank.GENUS.toString(), dc.getGenus());
			taxon.addMonomial(monomial);
		}
		if (dc.getSubgenus() != null) {
			monomial = new Monomial(DefaultClassification.Rank.SUBGENUS.toString(), dc.getSubgenus());
			taxon.addMonomial(monomial);
		}
		if (dc.getSpecificEpithet() != null) {
			monomial = new Monomial(DefaultClassification.Rank.SPECIFIC_EPITHET.toString(), dc.getSpecificEpithet());
			taxon.addMonomial(monomial);
		}
		if (dc.getInfraspecificEpithet() != null) {
			monomial = new Monomial(DefaultClassification.Rank.INFRASPECIFIC_EPITHET.toString(), dc.getInfraspecificEpithet());
			taxon.addMonomial(monomial);
		}
	}

}
