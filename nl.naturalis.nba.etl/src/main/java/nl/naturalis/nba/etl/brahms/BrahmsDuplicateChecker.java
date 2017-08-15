package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * BrahmsDuplicateChecker - tools that checks for each of the import files if it
 * contains records with duplicate object id's.
 * 
 * BrahmsDuplicateChecker --fast : returns "No duplicates" it no duplicates were
 * found, "Contains duplicate object ID's!" otherwise.
 * 
 * 
 * @author Tom Gilissen
 *
 */
public class BrahmsDuplicateChecker {

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(BrahmsDuplicateChecker.class);
	private final boolean suppressErrors = true;

	private Set<String> objectIDs = new HashSet<>(4096);
	private HashMap<String, Integer> duplicateIDsCount = new HashMap<>();

	public static void main(String[] args)
	{
		String option = "";
		if (args.length != 0)
			option = args[0];
		try {
			new BrahmsDuplicateChecker().checkImportFiles(option);
		}
		catch (Throwable t) {
			logger.error(t.getClass() + " terminated unexpectedly: " + t.getMessage());
		}
	}

	public void checkImportFiles(String option)
	{
		logger.info("--- Start with duplicate check ---");
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			System.exit(1);
		}
		for (File csvFile : csvFiles) {
			objectIDs.clear();
			duplicateIDsCount.clear();
			// option = "detailed";
			switch (option) {
				case "detailed":
					thoroughCheckFile(csvFile);
					break;
				default:
					fastCheckFile(csvFile);
			}
		}
	}

	/**
	 * Compares the number of records in the file with the number of unique IDs,
	 * taken from the field BARCODE. Sends a warning to the log file when there
	 * is a difference between the two.
	 * 
	 * @param f  the file to be checked
	 */
	public void fastCheckFile(File f) {

		CSVExtractor<BrahmsCsvField> extractor = null;
		ETLStatistics extractionStats = new ETLStatistics();
		extractor = createExtractor(f, extractionStats);
		long start = System.currentTimeMillis();
		String barcode = "";

		logger.info(" ");
		logger.info("Checking file: " + f.getName());

		for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
			if (rec == null) {
				continue;
			}
			barcode = rec.get(BARCODE);
			if (barcode != null) {
				objectIDs.add(rec.get(BARCODE));
			}
			else {
				extractionStats.recordsRejected++;
			}
			extractionStats.recordsProcessed++;
		}
		
		int uniqueIDs = objectIDs.size();
		int recordsProcessed = extractionStats.recordsProcessed;
		int recordsRejected = extractionStats.recordsRejected;
		
		logger.info("Check finished in {} seconds", nl.naturalis.nba.etl.ETLUtil.getDuration(start) );
		logger.info("Records processed: {}", recordsProcessed);
		logger.info("Records rejected: {}", extractionStats.recordsRejected);
		logger.info("Total number of unique IDs: " + uniqueIDs);

		if (recordsProcessed == (uniqueIDs + recordsRejected)) {
			logger.info("File contains no duplicates.");
		} else {
			logger.warn("File contains {} duplicate records!", (recordsProcessed - recordsRejected - uniqueIDs));
		}
	}

		
	/**
	 * Compares the number of records in the file with the number of unique IDs,
	 * taken from the field BARCODE, and counts the number of duplicate ID's.
	 * Sends a warning to the log file when there is a difference and reveals
	 * the number of duplicate IDs.
	 * 
	 * @param f
	 */
	public void thoroughCheckFile(File f)
	{
		CSVExtractor<BrahmsCsvField> extractor = null;
		ETLStatistics extractionStats = new ETLStatistics();
		extractor = createExtractor(f, extractionStats);
		long start = System.currentTimeMillis();
		String barcode = "";

		logger.info(" ");
		logger.info("Checking file: " + f.getName());

		for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
			if (rec == null) {
				continue;			
			}

			barcode = rec.get(BARCODE);
			if (barcode == null) {
				extractionStats.recordsRejected++;
				extractionStats.recordsProcessed++;
				continue;
			}

			if (objectIDs.contains(barcode)) {
				// We have a duplicate!
				if (duplicateIDsCount.get(barcode) == null) {
					duplicateIDsCount.put(barcode, 2); // This is the first duplicate so this barcode has been used twice so far
				}
				else {
					duplicateIDsCount.put(barcode, duplicateIDsCount.get(barcode) + 1);
				}
			}
			else {
				// Not a duplicate so add to the set
				objectIDs.add(barcode);
			}
			extractionStats.recordsProcessed++;
		}

		int uniqueIDs = objectIDs.size();
		int recordsProcessed = extractionStats.recordsProcessed;
		int recordsRejected = extractionStats.recordsRejected;

		if (!duplicateIDsCount.isEmpty()) {
			for (Map.Entry<String, Integer> IDCount : duplicateIDsCount.entrySet()) {
				logger.info("{} occurs {} times", IDCount.getKey(), IDCount.getValue());
			}
		}

		logger.info("Check finished in {} seconds", nl.naturalis.nba.etl.ETLUtil.getDuration(start));
		logger.info("Records processed: {}", recordsProcessed);
		logger.info("Records rejected: {}", extractionStats.recordsRejected);
		logger.info("Total number of unique IDs: " + uniqueIDs);

		if (recordsProcessed == (uniqueIDs + recordsRejected)) {
			logger.info("File contains no duplicates.");
		}
		else {
			logger.warn("File contains {} barcodes that are used more than once!",
					(duplicateIDsCount.size()));
		}
	}



	private CSVExtractor<BrahmsCsvField> createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor<BrahmsCsvField> extractor = new CSVExtractor<>(f, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}

}
