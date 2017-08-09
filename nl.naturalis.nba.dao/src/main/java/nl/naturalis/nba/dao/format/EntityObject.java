package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;

/**
 * <p>
 * An entity object wraps an Elasticsearch document or an object nested within
 * it. Each record that gets written to a dataset is populated from a single
 * {@code EntityObject}. A record is never populated directly from a plain
 * Elasticsearch document. Suppose, for example, that you want to print CSV
 * records containing specimen collector information. Then the entity object
 * would be the {@link GatheringEvent#getGatheringPersons() gatheringPersons}
 * object within the {@link Specimen#getGatheringEvent() gatheringEvent} object
 * within the {@link DocumentType#SPECIMEN Specimen} document type. Since there
 * may be multiple collectors associated with a specimen, one specimen document
 * may yield multiple CSV records. Therefore the class responsible for printing
 * the CSV records cannot be fed with raw Specimen documents. Instead, each
 * document is first pulled through a {@link DocumentFlattener}, which produces
 * a list of entity objects, which are then fed to the class responsible for
 * printing the CSV records.
 * </p>
 * <p>
 * An entity object maintains a direct reference to the Elasticsearch document
 * from which it was extracted, because you might want to include data from it
 * in your CSV record. For example, if you are printing records containing
 * literature references for taxa, you might still want to have a CSV field
 * containing the ID of the taxon that the literature reference refers to. An
 * entity object also maintains a reference to its direct parent object. This
 * becomes important if you want to include some data from the parent object in
 * the CSV record (if the parent object is itself an array or list element, you
 * cannot navigate unambiguously from the root of the document to the entity
 * object).
 * </p>
 * 
 * See also {@link Entity#toString()}.
 * 
 * @author Ayco Holleman
 *
 */
public class EntityObject {

	private Object document;
	private Object entity;
	private EntityObject parent;

	public EntityObject(Object document)
	{
		this.document = document;
		this.entity = document;
		this.parent = null;
	}

	public EntityObject(Object document, Object entity, EntityObject parent)
	{
		this.document = document;
		this.entity = entity;
		this.parent = parent;
	}

	/**
	 * Returns the entire document from which this entity object was created.
	 */
	public Object getDocument()
	{
		return document;
	}

	/**
	 * Returns the object that functions as the main data source for a single
	 * record within a dataset. That object may be the Elasticsearch document
	 * itself, or it may be a single, nested object within the Elasticsearch
	 * document.
	 */
	public Object getEntity()
	{
		return entity;
	}

	/**
	 * Returns the parent of the entity, wrapped into another
	 * {@code EntityObject}. This way you you can navigate all the way back to
	 * the Elasticsearch document itself.
	 * 
	 * @return
	 */
	public EntityObject getParentEntity()
	{
		return parent;
	}

}
