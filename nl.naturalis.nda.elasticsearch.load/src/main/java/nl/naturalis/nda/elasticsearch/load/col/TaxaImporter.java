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
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.StringUtil;

public class TaxaImporter extends CSVImporter<ESTaxon> {

	public static void main(String[] args) throws Exception
	{
		String dwcaDir = System.getProperty("dwcaDir");
		// Whether or not to rebuild the Taxon document store from scratch
		String rebuild = System.getProperty("rebuild", "false");
		if (dwcaDir == null) {
			throw new Exception("Missing property \"dwcaDir\"");
		}
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType(LUCENE_TYPE);
			Thread.sleep(2000);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE, mapping);
		}
		try {
			TaxaImporter importer = new TaxaImporter(index);
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			index.getClient().close();
		}
		System.out.println("Done");
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

	static final String LUCENE_TYPE = "Taxon";
	static final String ID_PREFIX = "COL-";


	public TaxaImporter(Index index)
	{
		super(index, LUCENE_TYPE);
		setSpecifyId(true);
		setSpecifyParent(false);
	}


	@Override
	protected List<ESTaxon> transfer(CSVRecord record)
	{
		final ESTaxon taxon = new ESTaxon();

		taxon.setSourceSystem(SourceSystem.COL);
		taxon.setSourceSystemId(record.get(CsvField.taxonID.ordinal()));

		final ScientificName sn = new ScientificName();
		taxon.setAcceptedName(sn);
		sn.setFullScientificName(record.get(CsvField.scientificName.ordinal()));
		sn.setGenusOrMonomial(record.get(CsvField.genus.ordinal()));
		sn.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		sn.setInfraspecificEpithet(record.get(CsvField.infraspecificEpithet.ordinal()));
		sn.setAuthorshipVerbatim(record.get(CsvField.scientificNameAuthorship.ordinal()));

		final DefaultClassification dc = new DefaultClassification();
		taxon.setDefaultClassification(dc);

		dc.setKingdom(record.get(CsvField.kingdom.ordinal()));
		dc.setPhylum(record.get(CsvField.phylum.ordinal()));
		dc.setClassName(record.get(CsvField.classRank.ordinal()));
		dc.setOrder(record.get(CsvField.order.ordinal()));
		dc.setSuperFamily(record.get(CsvField.superfamily.ordinal()));
		dc.setFamily(record.get(CsvField.family.ordinal()));
		dc.setGenus(record.get(CsvField.genus.ordinal()));
		dc.setSubgenus(record.get(CsvField.subgenus.ordinal()));
		dc.setSpecificEpithet(record.get(CsvField.specificEpithet.ordinal()));
		dc.setInfraspecificEpithet(record.get(CsvField.infraspecificEpithet.ordinal()));

		addMonomials(taxon);

		String description = record.get(CsvField.description.ordinal()).trim();
		if (description.length() != 0) {
			TaxonDescription td = new TaxonDescription();
			td.setDescription(description);
			taxon.addDescription(td);
		}

		return Arrays.asList(taxon);
	}


	protected boolean skipRecord(CSVRecord record)
	{
		if (getInt(record, CsvField.acceptedNameUsageID.ordinal()) == 0) {
			return false;
		}
		return true;
	}


	@Override
	protected List<String> getIds(CSVRecord record)
	{
		String id = ID_PREFIX + record.get(CsvField.taxonID.ordinal());
		return Arrays.asList(id);
	}


	private static void addMonomials(ESTaxon taxon)
	{
		final DefaultClassification dc = taxon.getDefaultClassification();
		Monomial monomial = new Monomial(DefaultClassification.Rank.KINGDOM.toString(), dc.getKingdom());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.PHYLUM.toString(), dc.getPhylum());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.CLASS.toString(), dc.getClassName());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.ORDER.toString(), dc.getOrder());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.SUPER_FAMILY.toString(), dc.getSuperFamily());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.FAMILY.toString(), dc.getFamily());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.GENUS.toString(), dc.getGenus());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.SUBGENUS.toString(), dc.getSubgenus());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.SPECIFIC_EPITHET.toString(), dc.getSpecificEpithet());
		taxon.addMonomial(monomial);
		monomial = new Monomial(DefaultClassification.Rank.INFRASPECIFIC_EPITHET.toString(), dc.getInfraspecificEpithet());
		taxon.addMonomial(monomial);
	}

}
