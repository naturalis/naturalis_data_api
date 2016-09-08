package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.Path;

public interface ITypedFieldFactory extends IFieldFactory {

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in the {@link EntityObject entity object}. The {@code path} argument must
	 * specify the path of the source field <i>relative</i> to the
	 * {@link EntityObject entity object}.
	 */
	IField createEntityDataField(String name, Path path, DataSource dataSource)
			throws FieldConfigurationException;

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in the Elasticsearch document that contains the {@link EntityObject
	 * entity object}. The {@code path} argument must specify the <i>full</i>
	 * path of the source field. It must be specified as an array of path
	 * elements, with each element representing a successively deeper level in
	 * the Elasticsearch document.
	 */
	IField createDocumentDataField(String name, Path path, DataSource dataSource)
			throws FieldConfigurationException;

}
