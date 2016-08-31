package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;

/**
 * A {@code IDataSetFieldFactory} produces format-specific versions of data set
 * fields. For each format (CSV, XML, etc.) a concrete implementation must be
 * provided that produces {@link IDataSetField} instances that format and escape
 * values as appropriate for that format.
 * 
 * @author Ayco Holleman
 *
 */
public interface IDataSetFieldFactory {

	/**
	 * Returns an {@link IDataSetField} instance that retrieves its value from a
	 * field in the {@link Entity entity object}. document. The {@code name}
	 * argument specifies the name of the data set field. The {@code path}
	 * argument specifies the path of the field <i>relative</i> to the entity
	 * object. (See also {@link EntityConfiguration#getPathToEntity()}.) It must be specified
	 * as an array of path elements, with each element representing a
	 * successively deeper level in the entity object.
	 * 
	 * @see FieldConfigurator
	 * 
	 * @param dt
	 *            The document type containing the Elasticsearch field
	 * @param name
	 *            The name of the data set field
	 * @param path
	 *            The Elasticsearch field providing the value for the data set
	 *            field
	 * @return
	 */
	IDataSetField createEntityDataField(DocumentType<?> dt, String name, String[] path);

	/**
	 * Returns an {@link IDataSetField} instance that retrieves its value from a
	 * field in an an Elasticsearch document. The {@code name} argument
	 * specifies the name of the data set field. The {@code path} argument
	 * specifies the <i>full</i> path of an Elasticsearch field within the
	 * Elasticsearch document. It must be specified as an array of path
	 * elements, with each element representing a successively deeper level in
	 * the Elasticsearch document. For example the
	 * {@code gatheringEvent.dateTimeBegin} field of a
	 * {@link DocumentType#SPECIMEN Specimen document} should be passed to this
	 * method as:<br>
	 * <code>
	 * new String[] {"gatheringEvent", "dateTimeBegin"}
	 * </code><br>
	 * Array access can be specified as in the following example:<br>
	 * <code>
	 * new String[] {"identifications", "0", "defaultClassification", "kingdom"}
	 * </code>
	 * 
	 * @see FieldConfigurator
	 * 
	 * @param dt
	 *            The document type containing the Elasticsearch field
	 * @param name
	 *            The name of the data set field
	 * @param path
	 *            The Elasticsearch field providing the value for the data set
	 *            field
	 * @return
	 */
	IDataSetField createDocumentDataField(DocumentType<?> dt, String name, String[] path);

	/**
	 * Returns an {@link IDataSetField} instance that provides a default value
	 * for the specified field.
	 * 
	 * @param dsc
	 * @param name
	 * @param constant
	 * @return
	 */
	IDataSetField createConstantField(DocumentType<?> dt, String name, String constant);

	/**
	 * Returns an {@link IDataSetField} instance that uses an
	 * {@link ICalculator} instance to calculate a value for the specified
	 * field.
	 * 
	 * @param dsc
	 * @param name
	 * @param calculator
	 * @return
	 */
	IDataSetField createdCalculatedField(DocumentType<?> dt, String name, ICalculator calculator);

}
