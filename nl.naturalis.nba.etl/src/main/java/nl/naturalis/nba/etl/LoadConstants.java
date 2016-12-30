package nl.naturalis.nba.etl;

/**
 * Constants used throughout the import library.
 * 
 * @author Ayco Holleman
 *
 */
public interface LoadConstants {

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
	 * Whether or not the name import program should import the individual name
	 * parts of a scientific name.
	 */
	String SYSPROP_IMPORT_NAME_PARTS = "nl.naturalis.nba.etl.name.importNameParts";
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
