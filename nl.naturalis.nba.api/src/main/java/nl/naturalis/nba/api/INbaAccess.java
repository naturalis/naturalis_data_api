package nl.naturalis.nba.api;

import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.LogicalOperator;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Specifies a common set of methods that can be called against any type of document
 * within the NBA document store.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The class representing the Elasticsearch document that you are given access
 *            to through an implementation of this interface.
 */
public interface INbaAccess<T extends IDocumentObject> {

	/**
	 * Returns the data model object with the specified system ID, or {@code null} if
	 * there is no data model object with the specified system ID. The system ID is not
	 * part of the Elasticsearch document from which the data model object was created. It
	 * corresponds with the {@code _id} field, which is retrieved separately from the
	 * document source. You can get the value of this field through
	 * {@link IDocumentObject#getId() IDocumentObject.getId}.
	 * 
	 * @see IDocumentObject
	 * 
	 * @param id
	 *            The NBA system ID of the data model object
	 * @return
	 */
	T find(String id);

	/**
	 * Returns the data model objects with the specified system IDs, or a zero-length
	 * array no specimens were found.
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
	 * Returns the raw JSON source (converted to a <code>Map&lt;String,Object&gt;</code>
	 * instance) of the documents conforming to the provided query specification. This is
	 * especially useful if you are only interested in a few fields within the Taxon
	 * document. Use {@link QuerySpec#setFields(java.util.List) QuerySpec.setFields} or
	 * {@link QuerySpec#addFields(String...) QuerySpec.addFields} to select the fields you
	 * are interested in. Note that clients can still convert the raw JSON source to
	 * "strongly typed" data model objects (e.g. {@link Taxon} or {@link Specimen}
	 * instances) using <code>JsonUtil.convert</code> in the nl.naturalis.common.json
	 * package. This package is distributed with the Java client. Also note that the
	 * system ID of a document (the _id field of a search hit) is not part of the document
	 * itself. However you can still select it by calling
	 * <code>querySpec.addField("id")</code>. The system ID will then be added with key
	 * "id" to the map containing the selected fields.
	 * 
	 * @param spec
	 * @return
	 * @throws InvalidQueryException
	 */
	QueryResult<Map<String, Object>> queryRaw(QuerySpec spec)
			throws InvalidQueryException;

	/**
	 * Returns the number of documents conforming to the provided query specification. You
	 * may specify {@code null} for the {@code querySpec} argument if you simply want a
	 * total document count. Otherwise you should only set the query conditions and
	 * (possibly) the {@link LogicalOperator logical operator} on the {@code QuerySpec}.
	 * Setting anything else on the {@code QuerySpec} has no effect on the result and may
	 * harm performance.
	 * 
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	long count(QuerySpec querySpec) throws InvalidQueryException;

	/**
	 * <p>
	 * Returns the unique values of the specified field. The result is returned as a list
	 * of {@link KeyValuePair key-value pairs} with the key specifying one of the unique
	 * values and the value specifying the number of documents with that particular value.
	 * Note that if the specified field is a multi-valued field (i.e. it translates into a
	 * {@link List} or array), the sum of the document counts may add up to more than the
	 * total number of documents for the applicable document type. You can provide a
	 * {@link QuerySpec} to specify one or more {@link Condition query conditions}. You
	 * should only set the query conditions and (possibly) the {@link LogicalOperator
	 * logical operator} on the {@code QuerySpec}. Setting anything else on the
	 * {@code QuerySpec} has no effect on the result and may harm performance. You may
	 * specify {@code null} for the {@code querySpec} argument. Example output:
	 * </p>
	 * <code>
	 *&nbsp;&nbsp;&nbsp;&nbsp;[<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key" : "Preserved specimen",<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : 2<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key" : "FossileSpecimen",<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : 1<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"key" : "Herbarium sheet",<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : 1<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 *&nbsp;&nbsp;&nbsp;&nbsp;]<br>
	 * </code>
	 * 
	 * @param forField
	 * @param querySpec
	 * @return
	 * @throws InvalidQueryException
	 */
	List<KeyValuePair<String, Long>> getDistinctValues(String forField,
			QuerySpec querySpec) throws InvalidQueryException;

	/*
	 * Returns the raw JSON source (converted to a <code>Map&lt;String,Object&gt;</code>
	 * instance) of the documents conforming to the provided query specification. See
	 * {@link #queryRaw(QuerySpec)}. This method requires more client-side programming but
	 * responds as soon as the first documents from Elasticsearch arrive. Also this method
	 * requires less server-side memory and places no limit on the amount of documents
	 * being processed per call (see {@link QuerySpec#setSize(int)}).
	 * 
	 * @param spec
	 * 
	 * @param out
	 * 
	 * @throws InvalidQueryException
	 */
	//void queryRaw(QuerySpec spec, OutputStream out) throws InvalidQueryException;
}
