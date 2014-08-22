package nl.naturalis.nda.elasticsearch.load.col;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;
import nl.naturalis.nda.elasticsearch.load.col.TaxaImporter.CsvField;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonSynonymsEnricher {

	public static void main(String[] args) throws IOException
	{
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

		TaxonSynonymsEnricher enricher = new TaxonSynonymsEnricher(index);
		enricher.importCsv("C:/test/col-dwca/taxa.txt");

		index.getClient().close();
	}

	private static final Logger logger = LoggerFactory.getLogger(TaxonSynonymsEnricher.class);
	private static final int DEFAULT_BATCH_SIZE = 1000;

	private static final String LUCENE_TYPE = "Taxon";

	private final Index index;
	private int batchSize = DEFAULT_BATCH_SIZE;


	public TaxonSynonymsEnricher(Index index)
	{
		this.index = index;
	}


	private void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		List<ESTaxon> objects = new ArrayList<ESTaxon>(batchSize);
		List<String> ids = new ArrayList<String>(batchSize);
		int processed = 0;
		int bad = 0;

		String line;
		CSVRecord record;

		try {
			lnr.readLine(); // Skip header		
			ESTaxon taxon;
			while ((line = lnr.readLine()) != null) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + (processed + 1));
				}
				try {
					record = CSVParser.parse(line, format).iterator().next();
					if (getInt(record, TaxaImporter.CsvField.acceptedNameUsageID.ordinal()) == 0) {
						continue;
					}
					String id = "COL-" + record.get(CsvField.acceptedNameUsageID.ordinal());
					String synonym = record.get(CsvField.scientificName.ordinal());
					taxon = index.get(LUCENE_TYPE, id, ESTaxon.class);
					if (taxon == null) {
						logger.warn("Orphan synonym: " + synonym);
						continue;
					}
					//logger.info("Adding synonym: " + synonym);
					if (taxon.getSynonyms() == null || !taxon.getSynonyms().contains(synonym)) {
						taxon.addSynonym(synonym);
					}
					else {
						continue;
					}
					objects.add(taxon);
					ids.add(id);
					if (objects.size() == batchSize) {
						index.saveObjects(LUCENE_TYPE, objects, ids);
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
				index.saveObjects(LUCENE_TYPE, objects, ids);
			}
		}
		finally {
			lnr.close();
		}
	}


	private static int getInt(CSVRecord record, int fieldNo)
	{
		String s = record.get(fieldNo);
		if (s.trim().length() == 0) {
			return 0;
		}
		return Integer.parseInt(s);
	}

}
