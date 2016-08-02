package nl.naturalis.nba.dao.es.format;

import java.util.Map;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * <p>
 * Defines the capacity to provide a name and a value for a field in a data set.
 * This capacity is defined in a format-agnostic way: the data set might be
 * formatted as CSV, XML, JSON, etc. Different formats have different string
 * escaping rules, so each format will have its own implementation(s) of
 * {@code IDataSetField}. On the other hand, this interface relies fairly
 * strongly on the fact that we use Elasticsearch as a data store. The
 * {@link #getValue(Map) getValue} method, which defines the capacity to provide
 * a value for a field in a data set, takes a Map&lt;String,Object&gt; instance
 * as input. This is the type of object you get when you make the Elasticsearch
 * API call {@code SearchHit.getSource()}.
 * </p>
 * <p>
 * Implementations of this interface come in three flavors:<br>
 * <ol>
 * <li><b>Data fields</b> retrieve the value to be written directly from the
 * Elasticsearch document passed to the {@link #getValue(Map) getValue} method,
 * presumably because they know which Elasticsearch field to access.
 * <li><b>Calculated fields</b> use a {@link ICalculator} instance to calculate
 * the value to be written. The calculator may or may not use the data contained
 * in the Elasticsearch document.
 * <li><b>Constant fields</b> write a fixed value. This type of fields ignores
 * the Elasticsearch document passed to the {@link #getValue(Map) getValue}
 * method and simply return a string literal.
 * </ol>
 * However, the manner in which an {@code IDataSetField} implementation
 * retrieves the value to be written to the output document is an implementation
 * detail. No sub-interfaces are provided (or needed by XML/CSV/DwCA/JSON
 * writers) for these three flavors of {@code IDataSetField} flavors.
 * </p>
 * 
 * @see IDataSetFieldFactory
 * 
 * @author Ayco Holleman
 *
 */
public interface IDataSetField {

	/**
	 * The name of the field. The name can, for example, be used to provide a
	 * header for a CSV field or a tag name for an XML element.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * The value to be output to the data set, using the specified Elasticsearch
	 * document as input.
	 * 
	 * @param esDocumentAsMap
	 * @return
	 */
	String getValue(Map<String, Object> esDocumentAsMap);

}
