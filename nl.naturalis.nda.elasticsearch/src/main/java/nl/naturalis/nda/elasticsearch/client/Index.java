package nl.naturalis.nda.elasticsearch.client;

/**
 * Interface defining methods for classes representing an ElasticSearch index,
 * presumably by wrapping an ElasticSearch client.
 * 
 * @author ayco_holleman
 * 
 */
public interface Index {

	/**
	 * Whether or not the index has already been created.
	 * 
	 * @return {@code true} if yes, {@code false} if not.
	 */
	boolean exists();


	/**
	 * Describe the index (i.e. its mapping).
	 * 
	 * @return The mapping or null the index has not been created yet.
	 */
	String describe();


	/**
	 * Describes all indices (i.e. their mappings) in the cluster. This is one
	 * of the methods that does not specifically operate against the
	 * encapsulated index. For ease of use it is still a non-static method.
	 * 
	 * @return The mappings
	 */
	String describeAllIndices();


	/**
	 * Create the index.
	 */
	void create();


	/**
	 * Create the index along with the specified mappings
	 * 
	 * @param mappings The mappings to create in the index.
	 * 
	 */
	void create(String mappings);


	/**
	 * Adds a new type to the index or overrides an existing one.
	 * 
	 * @param name The name of the type
	 * @param mapping The mapping for the type
	 * 
	 */
	void createType(String name, String mapping);


	/**
	 * Delete the index from the cluster.
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	boolean delete();


	/**
	 * Delete all indices and their data from the cluster. Handle with care!
	 * This is one of the methods that does not specifically operate against the
	 * encapsulated index. For ease of use it is still a non-static method.
	 * 
	 */
	void deleteAllIndices();


	/**
	 * Adds the JSON representation of the specified object to the index.
	 * 
	 * @param type The type of the document
	 * @param obj The object to add
	 * @param id The document ID
	 */
	void saveObject(String type, Object obj, String id);

	/**
	 * Add a new document to the index.
	 * 
	 * @param type The type of the document
	 * @param json The document
	 * @param id The document ID
	 * 
	 */
	void save(String type, String json, String id);



}