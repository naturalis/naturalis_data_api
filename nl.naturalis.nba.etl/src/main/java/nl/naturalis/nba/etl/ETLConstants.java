package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;

/**
 * Constants used throughout the import library.
 * 
 * @author Ayco Holleman
 *
 */
public interface ETLConstants {

	/**
	 * Optional system property that can be used to set the size of
	 * Elasticsearch bulk index requests (the number of documents to be indexed
	 * at once).
	 */
	String SYSPROP_LOADER_QUEUE_SIZE = "nl.naturalis.nba.etl.queueSize";

	/**
	 * Optional system property that causes the ETL programs to suppress ERROR
	 * and WARN messages while still letting through INFO messages. This can
	 * sometimes be helpful if you expect very large numbers of well-known
	 * errors that cause you to miss interesting INFO messages in the log files.
	 */
	String SYSPROP_SUPPRESS_ERRORS = "nl.naturalis.nba.etl.suppressErrors";

	/**
	 * &34;nl.naturalis.nba.etl.testGenera&34;. Optional system property that
	 * causes the ETL programs only import a record if its genus field has one
	 * of the value of the genera specified here (comma-separated). This
	 * applies, of course, only to taxonomic data sources. If you do not specify
	 * this system property on the Java command line, or if you leave it emtpy,
	 * all records are imported. String comparisons are done in a
	 * case-insensitive way.
	 */
	String SYSPROP_TEST_GENERA = "nl.naturalis.nba.etl.testGenera";

	/**
	 * &34;nl.naturalis.nba.etl.dry&34;. Optional system property that causes
	 * the ETL programs only transform and validate the source data, generate
	 * log files, but not index actually delete, create or update any
	 * Elasticsearch documents (a &34;dry run&34;).
	 */
	String SYSPROP_DRY_RUN = "nl.naturalis.nba.etl.dry";

	/**
	 * Boolean property determining whether or not to delete all documents from
	 * a source system before starting an import for that source system.
	 */
	String SYSPROP_TRUNCATE = "nl.naturalis.nba.etl.truncate";
	
	/**
	 * &34;etl.output&34;. Optional system property that defines where the 
	 * output of the ETL process should go. To the document store (es), which is
	 * the default (also when the property is NOT set), or the the file system
	 * (file).
	 */
	String SYSPROP_ETL_OUTPUT = "etl.output";

	/**
	 * &34;etl.enrich&34;. Boolean property which defines whether the documents
	 * should be enriched during the import process (in contrast to the 
	 * enrichment during the standalone process),
	 */
	String SYSPROP_ETL_ENRICH = "etl.enrich";
	
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
	LicenseType LICENCE_TYPE = LicenseType.COPYRIGHT;
	/**
	 * CC0
	 */
	License LICENCE = License.CC0_10;
	/**
	 * https://data.biodiversitydata.nl
	 */
	String PURL_SERVER_BASE_URL = "https://data.biodiversitydata.nl";
	/**
	 * Brahms
	 */
	String BRAHMS_ABCD_SOURCE_ID = "Brahms";
	/**
	 * Botany
	 */
	String BRAHMS_ABCD_COLLECTION_TYPE = "Botany";

}
