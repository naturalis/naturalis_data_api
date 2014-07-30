package nl.naturalis.nda.elasticsearch.client;

import java.util.List;

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
	 * Delete the index and all its types and data from the cluster.
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	boolean delete();


	/**
	 * Adds a new type to the index or overrides an existing one.
	 * 
	 * @param name The name of the type
	 * @param mapping The mapping for the type
	 * 
	 */
	void addType(String name, String mapping);


	boolean deleteType(String name);


	/**
	 * Delete all indices and their data from the cluster. Handle with care!
	 * This is one of the methods that does not specifically operate against the
	 * encapsulated index. For ease of use it is still a non-static method.
	 * 
	 */
	void deleteAllIndices();


	/**
	 * Add a new document of the specified type to the index.
	 * 
	 * @param type The type of the document
	 * @param json The document
	 * @param id The document ID
	 * 
	 */
	void saveDocument(String type, String json, String id);


	/**
	 * Adds the specified object to the index.
	 * 
	 * @param type The type of the document
	 * @param obj The object to add
	 * @param id The document ID
	 */
	void saveObject(String type, Object obj, String id);


	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type The type of the document
	 * @param objs The objects to add
	 */
	void saveObjects(String type, List<?> objs);


	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type The type of the document
	 * @param objs The objects to add
	 * @param ids The ids of the objects to add. For each object you must
	 *            specify an ID. In other words the sizes of the {@objs}
	 *            list and the {@ids} list must be equal. If you want Lucene to
	 *            generate the IDs for you, specify null.
	 */
	void saveObjects(String type, List<?> objs, List<String> ids);


	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type The type of the document
	 * @param objs The objects to add
	 * @param ids The ids of the objects to add. For each object you must
	 *            specify an ID. In other words the sizes of the {@objs}
	 *            list and the {@ids} list must be equal. If you want Lucene to
	 *            generate the IDs for you, specify null.
	 * @param parentIds The IDs of the parents of the objects. Specify null if
	 *            the objects do not have a relational parent.
	 */
	void saveObjects(String type, List<?> objs, List<String> ids, List<String> parentIds);

}