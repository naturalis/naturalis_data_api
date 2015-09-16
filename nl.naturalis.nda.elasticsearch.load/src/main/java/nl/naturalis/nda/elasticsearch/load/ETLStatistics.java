package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.pad;
import static org.domainobject.util.StringUtil.rpad;

import org.slf4j.Logger;

/**
 * A Java bean maintaining a set of running totals for an ETL program. A
 * statistics object is passed around by the various ETL components of an import
 * program so each can update the counter(s) relevant to their job. A
 * distinction is made between record-level statistics and object-level (a.k.a.
 * document-level) statistics. This is because one record from a data source may
 * yield multiple ElasticSearch documents. For example a record in a Brahms CSV
 * dump may contain multiple images. Thus the number of records processed is not
 * necessarily equal to the number of documents indexed, even if there were no
 * validation errors. For this type of data sources we try to distinguish
 * between record-level validation errors and object-level validation errors,
 * although the distinction is sometimes somewhat arbitrary.
 * 
 * @author Ayco Holleman
 *
 */
public class ETLStatistics {

	/**
	 * The number of times that the source data could not be parsed into a
	 * record. This counter is meant to be maintained by extractor components
	 * (e.g. {@link CSVExtractor CSV extractors}) and should not be updated by
	 * other ETL components. The {@code badInput} counter is useful when
	 * processing CSV files, where a raw line may not be parsable into a
	 * {@code CSVRecord}. It is not very useful when processing XML files,
	 * because these are parsed as a whole into a DOM tree.
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
	 * The number of objects that passed validation
	 */
	public int objectsAccepted;

	private boolean useObjectsAccepted;

	/**
	 * Determines which counter to use for successfully transformed data.
	 * Ordinarily the following rule applies:<br>
	 * <br>
	 * {@code objectsSkipped + objectsRejected + objectsIndexed = objectsProcessed}
	 * <br>
	 * <br>
	 * In this case {@code objectsIndexed} is the number of successfully
	 * transformed data. The transformer keeps track of {@code objectsSkipped}
	 * and {@code objectsRejected} while the loader keeps track of
	 * {@code objectsIndexed}. ETL programs for which this rule applies don't
	 * need to keep track of the {@code objectsAccepted} counter. However, if a
	 * data source is only used to add children (nested objects) to an already
	 * existing parent document, this rule no longer applies. The rule that
	 * applies then is:<br>
	 * <br>
	 * {@code objectsSkipped + objectsRejected + objectsAccepted = objectsProcessed}
	 * <br>
	 * <br>
	 * In this case the transformer provides all three statistics and the number
	 * of indexations is more or less meaningless. If a data source provides 10
	 * children for a particular parent document, the parent document will be
	 * re-indexed anywhere between 1 and 10 times during the course of the
	 * program, depending on how far apart the CSV/XML records containing the
	 * children were (if they all came one after another in the data source,
	 * they are added all at once to the parent document, resulting in just one
	 * index request for 10 child records).
	 * 
	 * @param b
	 */
	public void setUseObjectsAccepted(boolean b)
	{
		this.useObjectsAccepted = b;
	}

	/**
	 * Whether or not to use the objectsAccepted counter in stead of the
	 * objectsIndexed counter.
	 * 
	 * @return
	 * 
	 * @see #setUseObjectsAccepted(boolean)
	 */
	public boolean isUseObjectsAccepted()
	{
		return useObjectsAccepted;
	}

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
		objectsAccepted = 0;
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
		objectsAccepted += other.objectsAccepted;
	}

	/**
	 * Log statistic about the ETL cycle.
	 * 
	 * @param logger
	 */
	public void logStatistics(Logger logger)
	{
		logStatistics(logger, null);
	}

	/**
	 * Log statistic about the ETL cycle, using a user-friendly name for the
	 * type of objects being indexed.
	 * 
	 * @param logger
	 * @param niceName
	 */
	public void logStatistics(Logger logger, String niceName)
	{
		logger.info(" ");
		if (niceName != null) {
			String title = niceName.toUpperCase() + " IMPORT";
			logger.info(pad(title, 38));
		}
		else {
			niceName = "Objects";
		}
		logger.info("=====================================");
		logger.info(statistic("Extraction/parse failures", badInput));
		logger.info(" ");
		logger.info(statistic("Records skipped", recordsSkipped));
		logger.info(statistic("Records accepted", recordsAccepted));
		logger.info(statistic("Records rejected", recordsRejected));
		logger.info("------------------------------------- +");
		logger.info(statistic("Records processed", recordsProcessed));

		logger.info(" ");
		logger.info(statistic(niceName, "skipped", objectsSkipped));
		if (useObjectsAccepted)
			logger.info(statistic(niceName, "accepted", objectsAccepted));
		else
			logger.info(statistic(niceName, "indexed", objectsIndexed));
		logger.info(statistic(niceName, "rejected", objectsRejected));
		logger.info("------------------------------------- +");
		logger.info(statistic(niceName, "processed", objectsProcessed));

		if (useObjectsAccepted) {
			logger.info(" ");
			logger.info(statistic("ElasticSearch index requests", objectsIndexed));
		}

		logger.info("=====================================");
		logger.info(" ");
	}

	private static String statistic(String niceName, String statName, int stat)
	{
		return rpad(niceName + " " + statName, 28, ": ") + String.format("%7d", stat);
	}

	private static String statistic(String statName, int stat)
	{
		return rpad(statName, 28, ": ") + String.format("%7d", stat);
	}

}
