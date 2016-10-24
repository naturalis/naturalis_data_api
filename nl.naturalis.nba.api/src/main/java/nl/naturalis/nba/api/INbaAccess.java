package nl.naturalis.nba.api;

import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies a common set of methods that can be called against any type of
 * document within the NBA document store.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The data model object representing the Elasticsearch document
 */
public interface INbaAccess<T extends IDocumentObject> {

	/**
	 * Returns the data model object with the specified system ID, or
	 * {@code null} if there is no data model object with the specified system
	 * ID.
	 * 
	 * @param id
	 *            The NBA system ID of the data model object
	 * @return
	 */
	T find(String id);

	/**
	 * Returns the data model objects with the specified system IDs, or a
	 * zero-length array no specimens were found.
	 * 
	 * @param id
	 *            The NBA system IDs of the requested data model objects
	 * @return
	 */
	T[] find(String[] ids);

	/**
	 * Returns the documents conforming to the provided query specification.
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * Returns the raw JSON source (converted to a
	 * <code>Map&lt;String,Object&gt;</code> instance) of the documents
	 * conforming to the provided query specification. This is especially useful
	 * if you are only interested in a few fields within the Taxon document. Use
	 * {@link QuerySpec#setFields(java.util.List) QuerySpec.setFields} or
	 * {@link QuerySpec#addFields(String...) QuerySpec.addFields} to select the
	 * fields you are interested in. Note that clients can still convert the raw
	 * JSON source to "strongly typed" data model objects (e.g. {@link Taxon} or
	 * {@link Specimen} instances) using <code>JsonUtil.convert</code> in the
	 * nl.naturalis.common.json package. This package is distributed with the
	 * Java client. Also note that the system ID of a document (the _id field of
	 * a search hit) is not part of the document itself. However you can still
	 * select it by calling <code>querySpec.addField("id")</code>. The system ID
	 * will then be added with key "id" to the map containing the selected
	 * fields.
	 * 
	 * @param spec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<Map<String, Object>> queryRaw(QuerySpec spec) throws InvalidQueryException;

	/**
	 * Returns the raw JSON source (converted to a
	 * <code>Map&lt;String,Object&gt;</code> instance) of the documents
	 * conforming to the provided query specification. See
	 * {@link #queryRaw(QuerySpec)}. This method requires more client-side
	 * programming but responds as soon as the first documents from
	 * Elasticsearch arrive. Also this method requires less server-side memory
	 * and places no limit on the amount of documents being processed per call
	 * (see {@link QuerySpec#setSize(int)}).
	 * 
	 * @param spec
	 * @param out
	 * @throws InvalidQueryException
	 */
	//void queryRaw(QuerySpec spec, OutputStream out) throws InvalidQueryException;
}
