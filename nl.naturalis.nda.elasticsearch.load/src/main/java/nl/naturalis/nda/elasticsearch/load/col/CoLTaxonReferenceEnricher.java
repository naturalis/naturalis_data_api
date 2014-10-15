package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.CSVImporter.val;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_TAXON;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
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
		IndexNative index = new IndexNative(DEFAULT_NDA_INDEX_NAME);
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
	private final int maxRecords;


	public CoLTaxonReferenceEnricher(Index index)
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);
		prop = System.getProperty("maxRecords", "0");
		maxRecords = Integer.parseInt(prop);
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		List<ESTaxon> objects = new ArrayList<ESTaxon>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);

		int lineNo = 0;
		int processed = 0;
		int indexed = 0;
		int bad = 0;

		String line;
		CSVRecord record;

		try {

			++lineNo;
			lnr.readLine(); // Skip header			

			ESTaxon taxon;
			Reference reference;
			while ((line = lnr.readLine()) != null) {
				++lineNo;
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + lineNo);
					continue;
				}
				++processed;
				try {
					record = CSVParser.parse(line, format).iterator().next();
					String id = CoLTaxonImporter.ID_PREFIX + val(record, CsvField.taxonID.ordinal());
					reference = new Reference();
					reference.setTitleCitation(val(record, CsvField.title.ordinal()));
					reference.setCitationDetail(val(record, CsvField.description.ordinal()));
					Date pubDate = TransferUtil.parseDate(val(record, CsvField.date.ordinal()));
					reference.setPublicationDate(pubDate);
					reference.setAuthor(new Person(val(record, CsvField.creator.ordinal())));
					taxon = index.get(LUCENE_TYPE_TAXON, id, ESTaxon.class);
					if (taxon == null) {
						logger.debug("Orphan reference: " + id);
					}
					else if (taxon.getReferences() == null || !taxon.getReferences().contains(reference)) {
						taxon.addReference(reference);
						objects.add(taxon);
						ids.add(id);
						if (objects.size() >= bulkRequestSize) {
							index.saveObjects(LUCENE_TYPE_TAXON, objects, ids);
							indexed += objects.size();
							objects.clear();
							ids.clear();
						}
					}
				}
				catch (Throwable t) {
					++bad;
					logger.error("Error at line " + lineNo + ": " + t.getMessage());
					logger.error("Line: [[" + line + "]]");
					logger.debug("Stack trace: ", t);
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(LUCENE_TYPE_TAXON, objects, ids);
				indexed += objects.size();
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Records processed: " + processed);
		logger.info("Bad records: " + bad);
		logger.info("Documents indexed: " + indexed);
	}

}
