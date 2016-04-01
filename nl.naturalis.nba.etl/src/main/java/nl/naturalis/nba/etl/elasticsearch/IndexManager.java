package nl.naturalis.nba.etl.elasticsearch;

import java.util.Collection;
import java.util.List;

/**
 * Interface defining a simple API for common operations on an ElasticSearch
 * index. The API includes CRUD-type operations as well as DDL-type operations.
 * With a few exceptions the methods of this interface implicitly operate on a
 * <i>single</i> index, most likely set and fixed in the constructor of
 * implementing classes (hence the name IndexManager).
 * 
 * @author Ayco Holleman
 * 
 */
public interface IndexManager {

	/**
	 * Whether the index managed by this instance actually exists.
	 * 
	 * @return {@code true} if yes, {@code false} if not.
	 */
	boolean exists();

	/**
	 * Whether the index contains a definition (mapping) of the specified
	 * document type.
	 * 
	 * @param type
	 *            The document type to verify
	 * 
	 * @return @return {@code true} if yes, {@code false} if not.
	 */
	boolean typeExists(String type);

	/**
	 * Describes the index (i.e. its mapping).
	 * 
	 * @return The mapping or null the index has not been created yet.
	 */
	String describe();

	/**
	 * Describes all indices (i.e. their mappings) in the cluster. This method
	 * does <i>not</i> specifically operate on the encapsulated index. For ease
	 * of use, though, it is still included as part of the index manager
	 * interface.
	 * 
	 * @return The mappings
	 */
	String describeAllIndices();

	/**
	 * Creates the index with one shard and zero replicas.
	 */
	void create();

	/**
	 * Creates the index with the specified number of shards and replicas.
	 * 
	 * @param numShards
	 *            The number of shards
	 * @param numReplicas
	 *            The number of replicas
	 */
	void create(int numShards, int numReplicas);

	/**
	 * Creates the index using the specified settings.
	 * 
	 * @param settings
	 *            A JSON string containing the settings for the index.
	 */
	void create(String settings);

	/**
	 * Deletes the index and everything in it. Use with care!
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	boolean delete();

	/**
	 * Deletes <b>all</b> indices and their data from the cluster. Use with
	 * care! This method does <i>not</i> specifically operate on the
	 * encapsulated index. For ease of use, though, it is still included as part
	 * of the index manager interface.
	 */
	void deleteAllIndices();

	/**
	 * Adds a new document type (mapping) to the index or overwrites an existing
	 * one.
	 * 
	 * @param name
	 *            The name of the type
	 * @param mapping
	 *            The mapping for the type
	 * 
	 */
	void addType(String name, String mapping);

	/**
	 * Deletes the specified document type (mapping) from index, along with all
	 * documents of that type.
	 * 
	 * @param name
	 * @return
	 */
	boolean deleteType(String name);

	/**
	 * Retrieves the object corresponding to the specified id.
	 * 
	 * @param type
	 *            The document type corresponding to type T
	 * @param id
	 *            The id of the document
	 * @param targetClass
	 *            The class of the requested object
	 * @return An object of type T
	 */
	<T> T get(String type, String id, Class<T> targetClass);

	/**
	 * Retrieves the objects corresponding to the specified collection of ids.
	 * 
	 * @param type
	 *            The document type corresponding to type T
	 * @param ids
	 *            The ids
	 * @param targetClass
	 *            The class of the requested objects
	 * @return A list of objects of type T
	 */
	<T> List<T> get(String type, Collection<String> ids, Class<T> targetClass);

	/**
	 * Deletes the document with the specified id.
	 * 
	 * @param type
	 *            The type of the document
	 * @param id
	 *            The id of the document
	 * @return Whether or not the document was found
	 */
	boolean deleteDocument(String type, String id);

	/**
	 * Deletes all documents of the specified type where the specified field has
	 * the specified value.
	 * 
	 * @param type
	 * @param field
	 * @param value
	 */
	void deleteWhere(String type, String field, String value);

	/**
	 * Adds a new document of the specified type to the index.
	 * 
	 * @param type
	 *            The type of the document
	 * @param json
	 *            The document
	 * @param id
	 *            The document ID
	 * 
	 */
	void saveDocument(String type, String json, String id);

	/**
	 * Adds the specified object to the index.
	 * 
	 * @param type
	 *            The type of the document
	 * @param obj
	 *            The object to add
	 * @param id
	 *            The document ID
	 */
	void saveObject(String type, Object obj, String id);

	/**
	 * Adds the specified object to the index.
	 * 
	 * @param type
	 *            The type of the document
	 * @param obj
	 *            The object to add
	 * @param id
	 *            The document ID. Specify {@code null} if you want the ID to be
	 *            auto-generated by ElasticSearch.
	 * @param parentId
	 *            the document ID of the parent
	 */
	void saveObject(String type, Object obj, String id, String parentId);

	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type
	 *            The type of the document
	 * @param objs
	 *            The objects to add
	 * @throws BulkIndexException
	 *             Thrown if some of the specified objects could not be indexed.
	 */
	void saveObjects(String type, List<?> objs) throws BulkIndexException;

	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type
	 *            The type of the document
	 * @param objs
	 *            The objects to add
	 * @param ids
	 *            The ids of the objects to add. For each object you must
	 *            specify an ID. In other words the sizes of the {@code objs}
	 *            list and the {@code ids} list must be equal. If you want
	 *            Lucene to generate the IDs for you, specify null.
	 * @throws BulkIndexException
	 *             Thrown if some of the specified objects could not be indexed.
	 */
	void saveObjects(String type, List<?> objs, List<String> ids) throws BulkIndexException;

	/**
	 * Adds multiple objects to the index, presumably using ElasticSearch's bulk
	 * processing capabilities.
	 * 
	 * @param type
	 *            The type of the document
	 * @param objs
	 *            The objects to add
	 * @param ids
	 *            The ids of the objects to add. For each object you must
	 *            specify an ID. In other words the sizes of the {@code objs}
	 *            list and the {@code ids} list must be equal. If you want
	 *            Lucene to generate the IDs for you, specify null.
	 * @param parentIds
	 *            The IDs of the parents of the objects. Specify null if the
	 *            objects do not have a relational parent.
	 * @throws BulkIndexException
	 *             Thrown if some of the specified objects could not be indexed.
	 */
	void saveObjects(String type, List<?> objs, List<String> ids, List<String> parentIds)
			throws BulkIndexException;

}