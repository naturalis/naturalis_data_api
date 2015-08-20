package nl.naturalis.nda.elasticsearch.load;

import org.slf4j.Logger;

/**
 * A Java bean maintaining a set of running totals for an ETL program.
 * 
 * @author Ayco Holleman
 *
 */
public class ETLStatistics {

	/**
	 * The number of times that the source data could not be parsed into a
	 * record (usually during extraction phase). This is useful when processing
	 * CSV files, where a raw line may not be parsable into a {@code CSVRecord}.
	 * It is not very useful when processing XML files, because these are parsed
	 * as a whole into a DOM tree.
	 */
	public int badInput;

	/**
	 * The number of records processed by the import program.
	 */
	public int recordsProcessed;
	/**
	 * The number of records that contain data that are not meant to be imported
	 * by the import program. For example the taxa.txt file in CoL DwCA files
	 * containes both taxa and synonyms. The taxon importer only needs to
	 * process the taxa in that file.<br>
	 * {@code recordsSkipped + recordsRejected + recordsRejected = recordsProcessed}
	 */
	public int recordsSkipped;
	/**
	 * The number of records that failed some validation. Validation is done for
	 * the record as a whole, rather than for the object(s) extracted from it.
	 */
	public int recordsRejected;
	/**
	 * The number of records that made it to the transformation phase. Simply
	 * the number of records that were neither skipped nor rejected.<br>
	 * {@code recordsSkipped + recordsRejected + recordsAccepted = recordsProcessed}
	 */
	public int recordsAccepted;
	/**
	 * The number of objects processed by the import program. Note that one
	 * record may contain multiple objects. For example, one line in a Brahms
	 * CSV export may contain multiple images.<br>
	 * {@code objectsSkipped + objectsRejected + objectsIndexed = objectsProcessed}
	 */
	public int objectsProcessed;
	/**
	 * The number of objects that are not meant to be imported by the import
	 * program.
	 */
	public int objectsSkipped;
	/**
	 * The number of objects that failed some validation.
	 */
	public int objectsRejected;
	/**
	 * The number of objects that made it to ElasticSearch.
	 */
	public int objectsIndexed;

	/**
	 * Reset all counters
	 */
	public void reset()
	{
		badInput = 0;
		recordsProcessed = 0;
		recordsSkipped = 0;
		recordsRejected = 0;
		objectsProcessed = 0;
		objectsSkipped = 0;
		objectsRejected = 0;
		objectsIndexed = 0;
	}

	/**
	 * Add the counters from the specified statistics object to this statistics
	 * object.
	 * 
	 * @param other
	 */
	public void add(ETLStatistics other)
	{
		badInput += other.badInput;
		recordsProcessed += other.recordsProcessed;
		recordsSkipped += other.recordsSkipped;
		recordsRejected += other.recordsRejected;
		recordsAccepted += other.recordsAccepted;
		objectsProcessed += other.objectsProcessed;
		objectsSkipped += other.objectsSkipped;
		objectsRejected += other.objectsRejected;
		objectsIndexed += other.objectsIndexed;
	}

	public void logStatistics(Logger logger)
	{
		logger.info(" ");
		logger.info("Extraction/parse failures     : " + String.format("%7d", badInput));
		logger.info(" ");
		logger.info("Records skipped               : " + String.format("%7d", recordsSkipped));
		logger.info("Records investigated          : " + String.format("%7d", recordsAccepted));
		logger.info("Records rejected              : " + String.format("%7d", recordsRejected));
		logger.info("--------------------------------------- +");
		logger.info("Records processed             : " + String.format("%7d", recordsProcessed));
		logger.info(" ");
		logger.info("Objects indexed               : " + String.format("%7d", objectsIndexed));
		logger.info("Objects skipped               : " + String.format("%7d", objectsSkipped));
		logger.info("Objects rejected              : " + String.format("%7d", objectsRejected));
		logger.info("--------------------------------------- +");
		logger.info("Objects processed             : " + String.format("%7d", objectsProcessed));
		logger.info(" ");
	}

}
