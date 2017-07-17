package nl.naturalis.nba.dao.format;

import java.net.URI;
import java.util.Map;

/**
 * <p>
 * An {@code IField} represents a single field within a file belonging to a
 * particular data set. This interface is format-agnostic. The {@code IField}
 * interface can be used to write CSV files, XML documents, DwC archives, etc.
 * Different formats have different string escaping rules, so for each format
 * there will be different implementations of {@code IField}. However, the main
 * purpose of this interface is to define where the field gets its value
 * <b>from</b> rather than how to format the value. The {@link #getValue(Map)
 * getValue} method takes a Map&lt;String, Object&gt; instance as input.
 * Implementations of {@code IField} define how to extract a value from this
 * map. Although the use of a Map&lt;String, Object&gt; instance is strongly
 * suggestive of Elasticsearch (it is what you get when you call the
 * Elasticsearch API {@code SearchHit.getSource}), this type of object
 * also happens to be flexible enough to be used for complex source data, when a
 * data set's data source does not map one-to-one to an Elasticsearch document
 * type.
 * </p>
 * <p>
 * {@code IField} instances come in three flavors:<br>
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
 * @see ITypedFieldFactory
 * @see FieldConfigurator
 * 
 * @author Ayco Holleman
 *
 */
public interface IField {

	/**
	 * The name of the field. If an how this information is used depends on the
	 * implementation. When writing a CSV file the name can be used as a header;
	 * when writing an XML file it can be used as a tag name.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * A unique identifier for the concept expressed in this field. This could,
	 * for example, the id of a Dublin Core term or a Darwin Core term. This
	 * method may return {@code null} if the field's content does not correspond
	 * to a strictly defined concept.
	 */
	URI getTerm();

	/**
	 * The value to be written to the data set, formatted and escaped as
	 * appropriate for the type of output.
	 * 
	 * @param entity
	 * @return
	 */
	String getValue(EntityObject entity) throws FieldWriteException;

}
