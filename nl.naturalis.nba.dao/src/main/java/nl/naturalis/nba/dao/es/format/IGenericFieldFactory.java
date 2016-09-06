package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.DocumentType;

public interface IGenericFieldFactory extends IFieldFactory {

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in the {@link EntityObject entity object}. The {@code path} argument must
	 * specify the path of the source field <i>relative</i> to the entity
	 * object. (See also {@link Entity#getPath()}.) It must be specified as an
	 * array of path elements, with each element representing a successively
	 * deeper level in the entity object.
	 */
	IField createEntityDataField(String name, Path path);

	/**
	 * Returns an {@link IField} instance that retrieves its value from a field
	 * in an Elasticsearch document. The {@code path} argument must specify the
	 * <i>full</i> path of the source field. It must be specified as an array of
	 * path elements, with each element representing a successively deeper level
	 * in the Elasticsearch document. For example, the
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
	 */
	IField createDocumentDataField(String name, Path path);

}
