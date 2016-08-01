package nl.naturalis.nba.dao.es.format;

import java.util.Map;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * Defines a field in a data set. A field has a name and a value. The name
 * might, for example, be used to derive a tag name for an XML element or a
 * column header for a CSV file. The value is the value to be <i>written to</i>
 * the XML document, CSV file, etc. The value is always a {@link String},
 * formatted and escaped as appropriate for the output document. Implementations
 * of this interface come in three flavors:<br>
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
 * 
 * @author Ayco Holleman
 *
 */
public interface IDataSetField {

	/**
	 * The name of the field. For CSV files the name is used as the header for
	 * the CSV column.
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
