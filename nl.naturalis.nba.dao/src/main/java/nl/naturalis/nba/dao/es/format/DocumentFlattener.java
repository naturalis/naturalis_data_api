package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.common.Path;

/**
 * A {@code DocumentFlattener} flattens Elasticsearch documents (converted to
 * instances {@code Map&lt;String,Object&gt;}). The result is a list of nested
 * documents (not flat records in the classical sense!) specified by a path
 * within the Elasticsearch document (e.g. gatheringEvent.gatheringPersons).
 * Here and elsewhere these nested documents are also referred to as entities,
 * because it is from they are the basic and central entity of the
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentFlattener {

	private Path pathToEntity;
	private int entitiesPerDocument;

	/**
	 * Creates a new {@code DocumentFlattener}.
	 * 
	 * @param pathToEntity
	 *            The path to the {@link EntityObject entity object} within the
	 *            document
	 * @param entitiesPerDocument
	 *            An estimate of the average number of entities per document
	 */
	public DocumentFlattener(Path pathToEntity, int entitiesPerDocument)
	{
		this.pathToEntity = pathToEntity;
		this.entitiesPerDocument = entitiesPerDocument;
	}

	public List<EntityObject> flatten(Map<String, Object> document)
	{
		List<EntityObject> entityNodes = new ArrayList<>(entitiesPerDocument);
		EntityObject root = new EntityObject(document, null);
		flatten(root, pathToEntity, entityNodes);
		return entityNodes;
	}

	@SuppressWarnings("unchecked")
	private static void flatten(EntityObject node, Path pathToEntity, List<EntityObject> entityNodes)
	{
		if (pathToEntity.countElements() == 0) {
			entityNodes.add(node);
			return;
		}
		Object obj = readField(node.getData(), pathToEntity);
		if (obj == MISSING_VALUE)
			return;
		if (obj instanceof List) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
			for (Map<String, Object> map : list) {
				EntityObject child = new EntityObject(map, node);
				flatten(child, pathToEntity.shift(), entityNodes);
			}
		}
		else {
			Map<String, Object> map = (Map<String, Object>) obj;
			EntityObject child = new EntityObject(map, node);
			flatten(child, pathToEntity.shift(), entityNodes);
		}
	}


}
