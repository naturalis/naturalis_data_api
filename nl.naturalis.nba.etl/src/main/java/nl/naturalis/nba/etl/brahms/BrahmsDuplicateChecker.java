package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.BARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;
import static nl.naturalis.nba.etl.ETLUtil.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
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
		String[] allowedOptions = { "", "fast", "file", "all" };
		String option = "";
		if (args.length != 0)
			option = args[0];
		if (!Arrays.asList(allowedOptions).contains(option)) {
			logger.error("Unknown option. Please choose from: \"fast\", \"file\" or \"all\".");
			logger.error("Using fast check ...");
		}
		try {
			new BrahmsDuplicateChecker().checkImportFiles(option);
		}
		catch (Throwable t) {
			logger.error(t.getClass() + " terminated unexpectedly: " + t.getMessage());
		}
	}

	public void checkImportFiles(String option)
	{
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			System.exit(1);
		}
		switch (option) {
			case "file":
				doDetailedCheck(csvFiles);
				break;
			case "all":
				checkAllFiles(csvFiles);
				break;
			default:
				checkFast(csvFiles);
		}
	}

	/**
	 * Compares the number of records in the file with the number of unique IDs,
	 * taken from the field BARCODE. Sends a warning to the log file when there
	 * is a difference between the two.
	 * 
	 * @param f
	 *            the file to be checked
	 */
	public void checkFast(File[] files)
	{

		logger.info("--- Fast checking files for duplicates --- ");
		for (File f : files) {
			logger.info(" ");
			logger.info("Checking file: " + f.getName());
			long start = System.currentTimeMillis();
			CSVExtractor<BrahmsCsvField> extractor = null;
			ETLStatistics extractionStats = new ETLStatistics();
			extractor = createExtractor(f, extractionStats);
			objectIDs.clear();
			int uniqueIDs = 0;
			int recordsProcessed = 0;
			int recordsRejected = 0;
			String barcode = "";

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

			uniqueIDs = objectIDs.size();
			recordsProcessed = extractionStats.recordsProcessed;
			recordsRejected = extractionStats.recordsRejected;
			logger.info("Check finished in {} seconds", getDuration(start));
			logger.info("Records processed: {}", recordsProcessed);
			logger.info("Records rejected: {}", recordsRejected);
			logger.info("Total number of unique IDs: " + uniqueIDs);

			if (recordsProcessed == (uniqueIDs + recordsRejected)) {
				logger.info("File contains no duplicates.");
			}
			else {
				logger.warn("File contains {} duplicate records!",
						(recordsProcessed - recordsRejected - uniqueIDs));
			}
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
	public void doDetailedCheck(File[] files)
	{

		logger.info("--- Detailed check for duplicates per file --- ");
		long start = System.currentTimeMillis();

		for (File f : files) {
			logger.info(" ");
			logger.info("Checking file: " + f.getName());
			CSVExtractor<BrahmsCsvField> extractor = null;
			ETLStatistics extractionStats = new ETLStatistics();
			extractor = createExtractor(f, extractionStats);
			objectIDs.clear();
			duplicateIDsCount.clear();
			int uniqueIDs = 0;
			int recordsProcessed = 0;
			int recordsRejected = 0;
			String barcode = "";

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

				if (objectIDs.contains(barcode)) { // We have a duplicate!
					if (duplicateIDsCount.get(barcode) == null) {
						// This is the first duplicate so this barcode has been used twice so far
						duplicateIDsCount.put(barcode, 2);
					}
					else {
						duplicateIDsCount.put(barcode, duplicateIDsCount.get(barcode) + 1);
					}
				}
				else {
					objectIDs.add(barcode); // Not a duplicate so add to the set
				}
				extractionStats.recordsProcessed++;
			}

			uniqueIDs = objectIDs.size();
			recordsProcessed = extractionStats.recordsProcessed;
			recordsRejected = extractionStats.recordsRejected;

			if (!duplicateIDsCount.isEmpty()) {
				for (Map.Entry<String, Integer> IDCount : duplicateIDsCount.entrySet()) {
					logger.info("{} occurs {} times", IDCount.getKey(), IDCount.getValue());
				}
			}

			logger.info("Check finished in {} seconds", getDuration(start));
			logger.info("Records processed: {}", recordsProcessed);
			logger.info("Records rejected: {}", recordsRejected);
			logger.info("Total number of unique IDs: " + uniqueIDs);

			if (recordsProcessed == (uniqueIDs + recordsRejected)) {
				logger.info("File contains no duplicates.");
			}
			else {
				logger.warn("Total number of duplicate records: {}",
						(recordsProcessed - recordsRejected - uniqueIDs));
				logger.warn("Number of barcodes used more than once: {}",
						(duplicateIDsCount.size()));
			}
		}
	}

	/**
	 * Checks all files at once for duplicate IDs
	 * 
	 * @param files
	 */
	public void checkAllFiles(File[] files)
	{
		logger.info("--- Detailed check for duplicates in all files --- ");
		long start = System.currentTimeMillis();
		objectIDs.clear();
		duplicateIDsCount.clear();
		int uniqueIDs = 0;
		int recordsProcessed = 0;
		int recordsRejected = 0;

		for (File f : files) {
			logger.info(" ");
			logger.info("Processing file: " + f.getName());
			CSVExtractor<BrahmsCsvField> extractor = null;
			ETLStatistics extractionStats = new ETLStatistics();
			extractor = createExtractor(f, extractionStats);
			String barcode = "";

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

				if (objectIDs.contains(barcode)) { // We have a duplicate!
					if (duplicateIDsCount.get(barcode) == null) {
						duplicateIDsCount.put(barcode, 2); // This is the first duplicate so this barcode has been used twice so far
					}
					else {
						duplicateIDsCount.put(barcode, duplicateIDsCount.get(barcode) + 1);
					}
				}
				else {
					objectIDs.add(barcode); // Not a duplicate so add to the set
				}
				extractionStats.recordsProcessed++;
			}

			recordsProcessed += extractionStats.recordsProcessed;
			recordsRejected += extractionStats.recordsRejected;
			uniqueIDs = objectIDs.size();

			logger.info("Records processed: {}", recordsProcessed);
			logger.info("Unique records sofar: {}", uniqueIDs);
			logger.info("Duplicate records sofar: {}",
					recordsProcessed - recordsRejected - uniqueIDs);
		}

		if (!duplicateIDsCount.isEmpty()) {
			int i = 1;
			logger.info("The following duplicates have been found:");
			for (Map.Entry<String, Integer> IDCount : duplicateIDsCount.entrySet()) {
				logger.info("#{} - {} occurs {} times", i, IDCount.getKey(), IDCount.getValue());
				i++;
			}
		}

		logger.info("");
		logger.info("--- Summary of the duplicate check ---");
		logger.info("Check finished in {} seconds", getDuration(start));
		logger.info("Total number of records processed: {}", recordsProcessed);
		logger.info("Total number of records rejected: {}", recordsRejected);
		logger.info("Total number of unique records: {}", uniqueIDs);
		if (duplicateIDsCount.isEmpty()) {
			logger.info("No duplicates have been found.");
		}
		else {
			logger.warn("Total number of duplicate records: {}",
					recordsProcessed - recordsRejected - uniqueIDs);
			logger.warn("Number of barcodes used more than once: {}", duplicateIDsCount.size());
		}
	}

	private CSVExtractor<BrahmsCsvField> createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor<BrahmsCsvField> extractor = new CSVExtractor<>(f, BrahmsCsvField.class, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}

}
