package nl.naturalis.nda.elasticsearch.load.col;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;
import nl.naturalis.nda.elasticsearch.load.col.CoLReferenceImporter.CsvField;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLTaxonReferenceEnricher {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		String dwcaDir = System.getProperty("dwcaDir");
		if (dwcaDir == null) {
			throw new Exception("Missing property \"dwcaDir\"");
		}
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		try {
			CoLTaxonReferenceEnricher enricher = new CoLTaxonReferenceEnricher(index);
			enricher.importCsv(dwcaDir + "/reference.txt");
		}
		finally {
			index.getClient().close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CoLTaxonReferenceEnricher.class);

	private final Index index;
	private final int bulkRequestSize;


	public CoLTaxonReferenceEnricher(Index index)
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		List<ESTaxon> objects = new ArrayList<ESTaxon>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);
		int processed = 0;
		int skipped = 0;
		int bad = 0;

		String line;
		CSVRecord record;

		try {
			lnr.readLine(); // Skip header		
			ESTaxon taxon;
			Reference reference;
			while ((line = lnr.readLine()) != null) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + (processed + 1));
				}
				try {
					record = CSVParser.parse(line, format).iterator().next();
					String id = CoLTaxonImporter.ID_PREFIX + record.get(CsvField.taxonID.ordinal());
					reference = new Reference();
					reference.setTitleCitation(record.get(CsvField.title.ordinal()));
					reference.setCitationDetail(record.get(CsvField.description.ordinal()));
					Date pubDate = TransferUtil.parseDate(record.get(CsvField.date.ordinal()));
					reference.setPublicationDate(pubDate);
					reference.setAuthor(new Person(record.get(CsvField.creator.ordinal())));
					taxon = index.get(CoLTaxonImporter.LUCENE_TYPE, id, ESTaxon.class);
					if (taxon == null) {
						logger.warn("Orphan reference: " + id);
						continue;
					}
					if (taxon.getReferences() == null) {
						taxon.setReferences(new ArrayList<Reference>(Arrays.asList(reference)));
					}
					else if (!taxon.getReferences().contains(reference)) {
						taxon.getReferences().add(reference);
					}
					else {
						continue;
					}
					objects.add(taxon);
					ids.add(id);
					if (objects.size() >= bulkRequestSize) {
						index.saveObjects(CoLTaxonImporter.LUCENE_TYPE, objects, ids);
						objects.clear();
						ids.clear();
					}
				}
				catch (Throwable t) {
					++bad;
					logger.error("Error at line " + (processed + 1), t);
				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(CoLTaxonImporter.LUCENE_TYPE, objects, ids);
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Records processed: " + processed);
		logger.info("Records skipped: " + skipped);
		logger.info("Bad records: " + bad);
		logger.info("Ready");
	}

}
