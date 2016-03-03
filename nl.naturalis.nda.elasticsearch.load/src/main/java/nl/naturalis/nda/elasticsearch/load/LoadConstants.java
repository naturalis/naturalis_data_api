package nl.naturalis.nda.elasticsearch.load;

/**
 * Constants used throughout the import library.
 * 
 * @author Ayco Holleman
 *
 */
public interface LoadConstants {

	/**
	 * Required system property telling us the location of the config dir (e.g.
	 * containing nda-import.properties).
	 */
	String SYSPROP_CONFIG_DIR = "ndaConfDir";
	/**
	 * Optional system property specifying the number of documents to be indexed
	 * at once.
	 */
	String SYSPROP_ES_BULK_REQUEST_SIZE = "es.bulk.request.size";
	/**
	 * ElastichSearch {@code _id} prefix for Brahms documents.
	 */
	String ES_ID_PREFIX_BRAHMS = "BRAHMS-";
	/**
	 * ElastichSearch {@code _id} prefix for CoL documents.
	 */
	String ES_ID_PREFIX_COL = "COL-";
	/**
	 * ElastichSearch {@code _id} prefix for NSR documents.
	 */
	String ES_ID_PREFIX_NSR = "NSR-";
	/**
	 * ElastichSearch {@code _id} prefix for CRS documents.
	 */
	String ES_ID_PREFIX_CRS = "CRS-";
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
