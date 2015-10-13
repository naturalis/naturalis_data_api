package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.pad;
import static org.domainobject.util.StringUtil.rpad;

import org.slf4j.Logger;

/**
 * A Java bean maintaining a set of running totals for an ETL program. A
 * statistics object is passed around by the ETL components of an import program
 * so each can update the counter(s) relevant to its job. A distinction is made
 * between record-level statistics and object-level statistics. This is because
 * one record in a data source may yield multiple objects (i.e. ElasticSearch
 * documents). For example one record in a Brahms CSV dump may contain multiple
 * images. Thus the number of records processed is not necessarily equal to the
 * number of documents indexed, even if there were no validation errors. For
 * this type of data sources an attempt is made to distinguish between
 * record-level validation errors and object-level validation errors, but the
 * distinction is sometimes somewhat arbitrary.
 * <p>
 * For data sources with a one-to-one relationship between records and objects
 * (each record produces one object/document), the distinction between
 * record-level statistics and object-level statistics is totally arbitrary.
 * Current implementations of {@link Transformer} only use the
 * {@link #recordsRejected} and {@link #recordsSkipped} counters to report on
 * invalid c.q. skipped data while leaving the {@link #objectsRejected} and
 * {@link #objectsSkipped} counters alone.
 * <p>
 * The following rules apply:<br>
 * 
 * <pre>
 * recordsSkipped + recordsRejected + recordsAccepted == recordsProcessed
 * objectsSkipped + objectsRejected + objectsAccepted == objectsProcessed
 * documentsRejected + documentsIndexed == objectsAccepted
 * </pre>
 * <p>
 * For data sources where each record produces just one document an extra rule
 * applies:<br>
 * 
 * <pre>
 * {@code objectsSkipped + objectsRejected + objectsAccepted == recordsAccepted}
 * </pre>
 * 
 * <h3>Nested Documents</h3>
 * <p>
 * Sometimes the records in a data source, or the objects extracted from them,
 * are not processed to create new documents. Instead they are only used to
 * enrich <i>existing</i> documents. For example, the vernacular names in the
 * vernacular.txt file of a DwC archive are not stored in a separate document
 * type. Instead, they are nested within existing taxon documents. The same
 * applies for synonyms, literature references and geological distribution data.
 * In this case the number of indexed documents is virtually meaningless and has
 * no relation with the number of valid (accepted) objects. If a data source
 * provides 10 children for a particular parent document, the parent document
 * could be re-indexed anywhere between 1 and 10 times during the course of the
 * program, depending on how far apart the CSV/XML records containing the
 * children were. If they all came one after another in the data source, they
 * are added all at once to the parent document, resulting in just one index
 * request for 10 child records. Thus, the following rule does <b>not</b> apply
 * any longer:<br>
 * 
 * <pre>
 * documentsRejected + documentsIndexed == objectsAccepted
 * </pre>
 * 
 * @author Ayco Holleman
 *
 */
public class ETLStatistics {

	/**
	 * The number of times that the source data could not be parsed into a
	 * record. This counter is maintained by extractor components (e.g.
	 * {@link CSVExtractor CSV extractors}). The {@code badInput} counter is
	 * useful when processing CSV files, where a raw line may not be parsable
	 * into a {@code CSVRecord}. It is not very useful when processing XML
	 * files, because these are parsed as a whole into a DOM tree, which is an
	 * all-or-nothing operation. Note that this counter does <i>not</i>
	 * contribute to the number of processed records. If we have bad input (e.g.
	 * a CSV record with too few fields), we by definition don't know if it was
	 * one, two, or whatever number of records (maybe the remaining fields came
	 * on the next line because of an un-escaped newline character). We only
	 * start counting records once we're passed the extraction phase.
	 */
	public int badInput;

	/**
	 * The number of records processed by the import program. This counter is
	 * maintained by {@link Transformer} objects
	 */
	public int recordsProcessed;

	/**
	 * The number of records that are not meant to be processed by the import
	 * program. For example, the taxa.txt file in a CoL DwC archive contains
	 * both taxa and synonyms. The taxon importer only cares about the taxa in
	 * that file while the synonym importer only cares about the synonyms in
	 * that file. This counter is maintained by {@link Transformer} objects.
	 */
	public int recordsSkipped;

	/**
	 * The number of records that failed some validation. This counter is
	 * maintained by {@link Transformer} objects.
	 */
	public int recordsRejected;

	/**
	 * The number of successfully validated records. This counter is maintained
	 * by {@link Transformer} objects.
	 */
	public int recordsAccepted;

	/**
	 * The number of objects processed by the import program. This counter is
	 * maintained by {@link Transformer} objects.
	 */
	public int objectsProcessed;

	/**
	 * The number of objects that are not meant to be imported by the import
	 * program. This counter is maintained by {@link Transformer} objects.
	 */
	public int objectsSkipped;

	/**
	 * The number of objects that failed some validation. This counter is
	 * maintained by {@link Transformer} objects.
	 */
	public int objectsRejected;

	/**
	 * The number of objects that passed validation. This counter is maintained
	 * by {@link Transformer} objects.
	 */
	public int objectsAccepted;

	/**
	 * The number of objects that could not be indexed by ElasticSearch. This
	 * counter is maintained by {@link ElasticSearchLoader loader} objects.
	 */
	public int documentsRejected;
	/**
	 * The number of documents indexed by ElasticSearch. This counter is
	 * maintained by {@link ElasticSearchLoader loader} objects.
	 */
	public int documentsIndexed;

	private boolean nested;
	private boolean oneToMany;

	/**
	 * Whether or not the data source being processed may yield multiple
	 * objects/documents per record.
	 * 
	 * @return
	 */
	public boolean isOneToMany()
	{
		return oneToMany;
	}

	/**
	 * Determines whether the data source being processed may yield multiple
	 * objects/documents per record. Specifying {@code true} or {@code false}
	 * results in slightly different output from
	 * {@link #logStatistics(Logger, String) logStatistics()}. When
	 * {@code false}, both of the following rules apply:
	 * 
	 * <pre>
	 * objectsSkipped + objectsRejected + objectsAccepted == recordsAccepted
	 * objectsSkipped + objectsRejected + objectsAccepted == objectsProcessed
	 * </pre>
	 * 
	 * Since the first rule is more insightful of the extract-transform-load
	 * process, that's the rule you will see in the output of
	 * {@link #logStatistics(Logger, String) logStatistics()}. For one-to-many
	 * data sources ({@code true}), however, only the second rule is guaranteed
	 * to apply, so that's the rule you will see in the output. When
	 * {@link #setNested(boolean) nesting} documents the {@code oneToMany} is
	 * ignored and the assumption will be that only second rule is guaranteed to
	 * apply.
	 * 
	 * @param oneToMany
	 */
	public void setOneToMany(boolean oneToMany)
	{
		this.oneToMany = oneToMany;
	}

	/**
	 * Whether or not the records being processed are stored as nested
	 * documents.
	 * 
	 * @return
	 * 
	 * @see #setNested(boolean)
	 */
	public boolean isNested()
	{
		return nested;
	}

	/**
	 * Determines whether the records being processed are stored as nested
	 * documents. Specifying {@code true} results in slightly different output
	 * when calling {@link #logStatistics(Logger, String) logStatistics()}. When
	 * {@code true}, the following rule does <b>not</b> apply any longer:
	 * 
	 * <pre>
	 * documentsRejected + documentsIndexed == objectsAccepted
	 * </pre>
	 * 
	 * Therefore, you will still be informed of the number of rejected and
	 * indexed documents, but you will not see them summed to yield the
	 * {@link #objectsAccepted} statistic.
	 * 
	 * @param nested
	 */
	public void setNested(boolean nested)
	{
		this.nested = nested;
	}

	/**
	 * Resets all counters to zero.
	 */
	public void reset()
	{
		badInput = 0;

		recordsProcessed = 0;
		recordsSkipped = 0;
		recordsRejected = 0;
		recordsAccepted = 0;

		objectsProcessed = 0;
		objectsSkipped = 0;
		objectsRejected = 0;
		objectsAccepted = 0;

		documentsRejected = 0;
		documentsIndexed = 0;
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
		objectsAccepted += other.objectsAccepted;

		documentsRejected += other.documentsRejected;
		documentsIndexed += other.documentsIndexed;
	}

	/**
	 * Logs statistics about the ETL cycle.
	 * 
	 * @param logger
	 */
	public void logStatistics(Logger logger)
	{
		logStatistics(logger, null);
	}

	/**
	 * Logs statistics about the ETL cycle, using a user-friendly name for the
	 * type of objects being indexed.
	 * 
	 * @param logger
	 * @param niceName
	 */
	public void logStatistics(Logger logger, String niceName)
	{
		logger.info(" ");
		if (niceName != null) {
			String title = niceName.toUpperCase();
			logger.info(pad(title, 38));
		}
		else {
			niceName = "Objects";
		}
		logger.info("=====================================");
		logger.info(statistic("Extraction/parse failures", badInput));
		logger.info(" ");
		logger.info(statistic("Records skipped", recordsSkipped));
		logger.info(statistic("Records rejected", recordsRejected));
		logger.info(statistic("Records accepted", recordsAccepted));
		logger.info("------------------------------------- +");
		logger.info(statistic("Records processed", recordsProcessed));

		logger.info(" ");
		logger.info(statistic(niceName, "skipped", objectsSkipped));
		logger.info(statistic(niceName, "rejected", objectsRejected));
		logger.info(statistic(niceName, "accepted", objectsAccepted));
		logger.info("------------------------------------- +");
		if (nested || oneToMany)
			logger.info(statistic(niceName, "processed", objectsProcessed));
		else
			logger.info(statistic("Records accepted", recordsAccepted));
		logger.info(" ");

		logger.info(statistic("Documents rejected", documentsRejected));
		logger.info(statistic("Documents indexed", documentsIndexed));
		if (!nested) {
			logger.info("------------------------------------- +");
			logger.info(statistic(niceName, "accepted", objectsAccepted));
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
