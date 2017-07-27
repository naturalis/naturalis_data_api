package nl.naturalis.nba.etl;

/**
 * Constants used throughout the import library.
 * 
 * @author Ayco Holleman
 *
 */
public interface ETLConstants {

	/**
	 * Optional system property that can be used to fine-tune bulk indexing (the
	 * number of documents indexed at once).
	 */
	String SYSPROP_LOADER_QUEUE_SIZE = "queueSize";

	/**
	 * Optional system property that causes the ETL programs to suppress ERROR
	 * and WARN messages while still letting through INFO messages. This can
	 * sometimes be helpful if you expect very large numbers of well-known
	 * errors that cause you to miss interesting INFO messages in the log files.
	 */
	String SYSPROP_SUPPRESS_ERRORS = "suppressErrors";

	/**
	 * &34;nl.naturalis.nba.etl.test.genera&34;. Optional system property that
	 * causes the ETL programs only import a record if its genus field has one
	 * of the value of the genera specified here (comma-separated). This
	 * applies, of course, only to taxonomic data sources. If you do not specify
	 * this system property on the Java command line, or if you leave it emtpy,
	 * all records are imported. String comparison are done in a
	 * case-insensitive way.
	 */
	String SYSPROP_TEST_GENERA = "nl.naturalis.nba.etl.test.genera";

	/**
	 * &34;nl.naturalis.nba.etl.dry&34;. Optional system property that causes
	 * the ETL programs only transform and validate the source data, but not
	 * index it (a &34;dry run&34;).
	 */
	String SYSPROP_DRY_RUN = "nl.naturalis.nba.etl.dry";

	/**
	 * Boolean property determining whether or not to delete all documents from
	 * a source system before starting an import for that source system.
	 */
	String SYSPROP_TRUNCATE = "nl.naturalis.nba.etl.truncate";

	/**
	 * &34;nl.naturalis.nba.etl.name.batchSize&34;.
	 */
	String SYS_PROP_SNG_IMPORT_BATCH_SIZE = "nl.naturalis.nba.etl.name.batchSize";

	/**
	 * &34;nl.naturalis.nba.etl.name.scrollTimeout&34;.
	 */
	String SYS_PROP_SNG_IMPORT_SCROLL_TIMEOUT = "nl.naturalis.nba.etl.name.scrollTimeout";

	/**
	 * &34;nl.naturalis.nba.etl.name.readBatchSize&34;.
	 */
	String SYS_PROP_ENRICH_READ_BATCH_SIZE = "nl.naturalis.nba.etl.enrich.readBatchSize";

	/**
	 * &34;nl.naturalis.nba.etl.name.writeBatchSize&34;.
	 */
	String SYS_PROP_ENRICH_WRITE_BATCH_SIZE = "nl.naturalis.nba.etl.enrich.writeBatchSize";

	/**
	 * &34;nl.naturalis.nba.etl.name.scrollTimeout&34;.
	 */
	String SYS_PROP_ENRICH_SCROLL_TIMEOUT = "nl.naturalis.nba.etl.enrich.scrollTimeout";

	/**
	 * Naturalis Biodiversity Center
	 */
	String SOURCE_INSTITUTION_ID = "Naturalis Biodiversity Center";
	/**
	 * Copyright
	 */
	String LICENCE_TYPE = "Copyright";
	/**
	 * CC0
	 */
	String LICENCE = "CC0";
	/**
	 * http://data.biodiversitydata.nl
	 */
	String PURL_SERVER_BASE_URL = "http://data.biodiversitydata.nl";
	/**
	 * Brahms
	 */
	String BRAHMS_ABCD_SOURCE_ID = "Brahms";
	/**
	 * Botany
	 */
	String BRAHMS_ABCD_COLLECTION_TYPE = "Botany";

}
