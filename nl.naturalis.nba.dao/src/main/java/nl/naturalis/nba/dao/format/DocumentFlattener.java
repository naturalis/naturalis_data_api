package nl.naturalis.nba.dao.format;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.Path;

/**
 * A {@code DocumentFlattener} flattens an Elasticsearch document. It produces a
 * list of nested documents extracted from the document. The location of these
 * nested documents are specified by a {@link Path path} within the document
 * (e.g. gatheringEvent.gatheringPersons). They are referred to as entities,
 * because they are the primary data source for a single record within a data
 * set. The flattener wraps the nested documents into an {@link EntityObject}.
 * This class maintains a reference to the original Elasticsearch document so
 * that data set writers still have access to data from the nested document's
 * parent.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentFlattener {

	private Path pathToEntity;
	private int entitiesPerDocument;

	public DocumentFlattener()
	{
		this(null, 1);
	}

	/**
	 * Creates a new {@code DocumentFlattener}.
	 * 
	 * @param pathToEntity
	 *            The path to the {@link EntityObject entity object} within the
	 *            document. You may pass {@code null} if the entire document is
	 *            the entity object.
	 */
	public DocumentFlattener(Path pathToEntity)
	{
		this(pathToEntity, 8);
	}

	/**
	 * Creates a new {@code DocumentFlattener}.
	 * 
	 * @param pathToEntity
	 *            The path to the {@link EntityObject entity object} within the
	 *            document. You may pass {@code null} if the entire document is
	 *            the entity object.
	 * @param entitiesPerDocument
	 *            An estimate of the average number of entities per document.
	 *            This will be used as the initial list size for the list
	 *            returned by the {@link #flatten(Map) flatten} method.
	 */
	public DocumentFlattener(Path pathToEntity, int entitiesPerDocument)
	{
		this.pathToEntity = pathToEntity;
		this.entitiesPerDocument = entitiesPerDocument;
	}

	public List<EntityObject> flatten(Map<String, Object> document)
	{
		if (pathToEntity == null) {
			return Arrays.asList(new EntityObject(document));
		}
		List<EntityObject> entityNodes = new ArrayList<>(entitiesPerDocument);
		EntityObject root = new EntityObject(document, null);
		flatten(root, pathToEntity, entityNodes);
		return entityNodes;
	}

	@SuppressWarnings("unchecked")
	private static void flatten(EntityObject node, Path path, List<EntityObject> entities)
	{
		if (path.countElements() == 0) {
			entities.add(node);
			return;
		}
		Object obj = readField(node.getData(), path.element(0));
		if (obj == MISSING_VALUE) {
			return;
		}
		if (obj instanceof List) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
			for (Map<String, Object> map : list) {
				EntityObject child = new EntityObject(map, node);
				flatten(child, path.shift(), entities);
			}
		}
		else {
			Map<String, Object> map = (Map<String, Object>) obj;
			EntityObject child = new EntityObject(map, node);
			flatten(child, path.shift(), entities);
		}
	}

}
