package nl.naturalis.nba.dao.es.format;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	private String[] pathToEntity;
	private int entitiesPerDocument;

	/**
	 * Creates a new {@code DocumentFlattener}.
	 * 
	 * @param pathToEntity
	 *            The path to a nested object (within an Elasticsearch document)
	 *            that functions as the central entity of a flat record. The
	 *            path is specified as a string array where each element
	 *            specifies a successively deeper level within the Elasticsearch
	 *            document.For example: new String[] { "gatheringEvent",
	 *            "gatheringPersons" }.
	 * @param entitiesPerDocument
	 *            An estimate of the average number of entities per document
	 */
	public DocumentFlattener(String[] pathToEntity, int entitiesPerDocument)
	{
		this.pathToEntity = pathToEntity;
		this.entitiesPerDocument = entitiesPerDocument;
	}

	public List<DocumentNode> flatten(Map<String, Object> document)
	{
		List<DocumentNode> entityNodes = new ArrayList<>(entitiesPerDocument);
		DocumentNode root = new DocumentNode(document, null);
		flatten(root, pathToEntity, entityNodes);
		return entityNodes;
	}

	@SuppressWarnings("unchecked")
	private static void flatten(DocumentNode node, String[] pathToEntityNode,
			List<DocumentNode> entityNodes)
	{
		if (pathToEntityNode.length == 0) {
			entityNodes.add(node);
			return;
		}
		Object obj = readField(node.getEntity(), new String[] { pathToEntityNode[0] });
		if (obj == MISSING_VALUE)
			return;
		pathToEntityNode = dive(pathToEntityNode);
		if (obj instanceof List) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
			for (Map<String, Object> map : list) {
				DocumentNode child = new DocumentNode(map, node);
				flatten(child, pathToEntityNode, entityNodes);
			}
		}
		else {
			Map<String, Object> map = (Map<String, Object>) obj;
			DocumentNode child = new DocumentNode(map, node);
			flatten(child, pathToEntityNode, entityNodes);
		}
	}

	private static String[] dive(String[] path)
	{
		String[] nestedPath = new String[path.length - 1];
		System.arraycopy(path, 1, nestedPath, 0, path.length - 1);
		return nestedPath;
	}

}
