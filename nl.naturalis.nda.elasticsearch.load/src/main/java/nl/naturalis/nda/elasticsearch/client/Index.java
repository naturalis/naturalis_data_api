package nl.naturalis.nda.elasticsearch.client;

import java.util.List;

/**
 * Interface defining methods for classes operating on a single ElasticSearch
 * index.
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
	 * Whether or not a mapping exists for the specified document type.
	 * 
	 * @param type The document type to verify
	 * 
	 * @return @return {@code true} if yes, {@code false} if not.
	 */
	boolean typeExists(String type);


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
	 * Creates an empty index with one shard and zero replicas.
	 */
	void create();


	/**
	 * Creates an empty index with the specified number of shards and replicas
	 * 
	 * @param numShards The number of shards
	 * @param numReplicas The number of replicas
	 */
	void create(int numShards, int numReplicas);


	void create(String settings);

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


	/**
	 * Deletes the specified type (along with all documents of that type) from
	 * the index.
	 * 
	 * @param name
	 * @return
	 */
	boolean deleteType(String name);


	/**
	 * Delete all indices and their data from the cluster. Handle with care!
	 * This is one of the methods that does not specifically operate against the
	 * encapsulated index. For ease of use it is still a non-static method.
	 * 
	 */
	void deleteAllIndices();


	/**
	 * Load the document of the specified type and id and convert it to an
	 * object of the specified class.
	 * 
	 * @param type
	 * @param id
	 * @param targetClass
	 * @return
	 */
	<T> T get(String type, String id, Class<T> targetClass);


	/**
	 * Delete the document with the specified id
	 * 
	 * @param type The type of the document
	 * @param id The id of the document
	 * @return Whether or not the document was found
	 */
	boolean deleteDocument(String type, String id);


	/**
	 * Deletes all documents from the specified document type where the
	 * specified field has the specified value.
	 * 
	 * @param type
	 * @param field
	 * @param value
	 */
	void deleteWhere(String type, String field, String value);


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
	 * Adds the specified object to the index.
	 * 
	 * @param type The type of the document
	 * @param obj The object to add
	 * @param id The document ID. Specify {@code null} if you want the ID to be
	 *            auto-generated by ElasticSearch.
	 * @param parentId the document ID of the parent
	 */
	void saveObject(String type, Object obj, String id, String parentId);


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