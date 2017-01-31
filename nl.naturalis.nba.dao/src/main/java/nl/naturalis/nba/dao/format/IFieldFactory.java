package nl.naturalis.nba.dao.format;

import java.net.URI;

import nl.naturalis.nba.api.Path;

/**
 * A {@code ITypedFieldFactory} produces format-specific versions of data set
 * fields. For each format (CSV, XML, etc.) a concrete implementation must be
 * provided that produces {@link IField} instances that format and escape values
 * as appropriate for that format.
 * 
 * @author Ayco Holleman
 *
 */
public interface IFieldFactory {

	/**
	 * Returns an {@link IField} instance that provides a default value for the
	 * specified field.
	 * 
	 * @param name
	 * @param term
	 * @param constant
	 * @return
	 */
	IField createConstantField(String name, URI term, String constant)
			throws FieldConfigurationException;

	/**
	 * Returns an {@link IField} instance that uses an {@link ICalculator}
	 * instance to calculate a value for the specified field.
	 * 
	 * @param name
	 * @param term
	 * @param calculator
	 * @param args
	 * @return
	 */
	IField createdCalculatedField(String name, URI term, ICalculator calculator)
			throws FieldConfigurationException;

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in the {@link EntityObject entity object}. The {@code path} argument must
	 * specify the path of the source field <i>relative</i> to the
	 * {@link EntityObject entity object}.
	 * 
	 * @param name
	 * @param term
	 * @param path
	 * @param dataSource
	 */
	IField createEntityDataField(String name, URI term, Path path, DataSource dataSource)
			throws FieldConfigurationException;

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in the Elasticsearch document that contains the {@link EntityObject
	 * entity object}. The {@code path} argument must specify the <i>full</i>
	 * path of the source field. It must be specified as an array of path
	 * elements, with each element representing a successively deeper level in
	 * the Elasticsearch document.
	 * 
	 * @param name
	 * @param term
	 * @param path
	 * @param dataSource
	 */
	IField createDocumentDataField(String name, URI term, Path path, DataSource dataSource)
			throws FieldConfigurationException;
}