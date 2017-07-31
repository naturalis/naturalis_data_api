package nl.naturalis.nba.dao.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.InvalidPathException;
import nl.naturalis.nba.common.PathValueReader;

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

	public List<EntityObject> flatten(Object document) throws InvalidPathException
	{
		if (pathToEntity == null) {
			return Arrays.asList(new EntityObject(document));
		}
		List<EntityObject> entityNodes = new ArrayList<>(entitiesPerDocument);
		EntityObject root = new EntityObject(document);
		flatten(root, pathToEntity, entityNodes);
		return entityNodes;
	}

	private void flatten(EntityObject node, Path path, List<EntityObject> entities)
			throws InvalidPathException
	{
		if (path.countElements() == 0) {
			entities.add(node);
			return;
		}
		PathValueReader pvr = new PathValueReader(path.element(0));
		Object obj = pvr.read(node.getEntity());
		if (obj == null) {
			return;
		}
		if (obj instanceof Iterable) {
			for (Object element : (Iterable<?>) obj) {
				EntityObject child = new EntityObject(node.getDocument(), element, node);
				flatten(child, path.shift(), entities);
			}
		}
		else {
			EntityObject child = new EntityObject(node.getDocument(), obj, node);
			flatten(child, path.shift(), entities);
		}
	}

}
