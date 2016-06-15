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
	String SYSPROP_ES_BULK_REQUEST_SIZE = "es.bulk.request.size";
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
