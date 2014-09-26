package nl.naturalis.nda.elasticsearch.load.col;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;
import nl.naturalis.nda.elasticsearch.load.col.CoLVernacularNameImporter.CsvField;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoLTaxonVernacularNameEnricher {

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
			CoLTaxonVernacularNameEnricher enricher = new CoLTaxonVernacularNameEnricher(index);
			enricher.importCsv(dwcaDir + "/vernacular.txt");
		}
		finally {
			index.getClient().close();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CoLTaxonVernacularNameEnricher.class);

	private final Index index;
	private final int bulkRequestSize;


	public CoLTaxonVernacularNameEnricher(Index index)
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
			VernacularName vn;
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
					taxon = index.get(CoLTaxonImporter.LUCENE_TYPE, id, ESTaxon.class);
					vn = new VernacularName();
					vn.setName(record.get(CsvField.vernacularName.ordinal()));
					vn.setLanguage(record.get(CsvField.language.ordinal()));
					if (taxon == null) {
						logger.warn("Orphan name: " + vn.getName());
						continue;
					}
					if (taxon.getVernacularNames() == null || !taxon.getVernacularNames().contains(vn)) {
						taxon.addVernacularName(vn);
					}
					else {
						continue;
					}
					objects.add(taxon);
					ids.add(id);
					if (objects.size() == bulkRequestSize) {
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
