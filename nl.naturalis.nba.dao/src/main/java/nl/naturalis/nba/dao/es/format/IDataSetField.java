package nl.naturalis.nba.dao.es.format;

import java.util.Map;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * <p>
 * An {@code IDataSetField} represents a single field in a data set. This
 * interface is format-agnostic. The {@code IDataSetField} interface can be used
 * to write CSV files, XML documents, DwC archives, etc. Different formats have
 * different string escaping rules, so for each format there will be different
 * implementations of {@code IDataSetField}. However, the main purpose of this
 * interface is to define where the field gets its value from in the first
 * place. The {@link #getValue(Map) getValue} method of this interface takes an
 * Elasticsearch document (converted to a Map&lt;String,Object&gt;) as input.
 * Implementations of {@code IDataSetField} define how to extract a value from
 * this document. {@code IDataSetField} instances come in three flavors:<br>
 * <ol>
 * <li><b>Data fields</b> retrieve the value to be written directly from the
 * Elasticsearch document, presumably because they know which Elasticsearch
 * field to access.
 * <li><b>Calculated fields</b> use an {@link ICalculator} instance to calculate
 * the value to be written. The calculator may or may not use the data contained
 * in the Elasticsearch document.
 * <li><b>Constant fields</b> write a fixed value. This type of fields ignores
 * the Elasticsearch document passed to the {@link #getValue(Map) getValue}
 * method and simply return a string literal.
 * </ol>
 * </p>
 * 
 * @see IDataSetFieldFactory
 * @see FieldConfigurator
 * 
 * @author Ayco Holleman
 *
 */
public interface IDataSetField {

	/**
	 * The name of the field. If an how this information is used depends on the
	 * implementation. When writing a CSV file the name can be used as a header;
	 * when writing an XML file it can be used as a tag name.
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
