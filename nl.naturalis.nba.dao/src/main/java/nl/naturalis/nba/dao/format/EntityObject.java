package nl.naturalis.nba.dao.format;

import java.util.Map;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;

/**
 * An entity object is an Elasticsearch document or an object nested within it
 * that functions as the main data source for a record within a data set.
 * Suppose, for example, that you want to print out CSV records containing
 * specimen collector information. Then the entity object would be the
 * {@link GatheringEvent#getGatheringPersons() gatheringPersons} object within
 * the {@link Specimen#getGatheringEvent() gatheringEvent} object within the
 * {@link DocumentType#SPECIMEN Specimen} document type. Since there may be
 * multiple collectors associated with a specimen, one specimen document may
 * yield multiple CSV records. An entity object maintains a reference to the
 * parent document, because you might want to include data from it in your (CSV)
 * record.
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
	 * Returns the entire document of which this entity object was part of. Even
	 * though the records you write to a data set will mostly contain data from
	 * the entity object, some data may need to come from the parent or
	 * ancestors of the entity object. Hence this method.
	 */
	public Object getDocument()
	{
		return document;
	}

	/**
	 * Returns the raw data of the entity object. The Map&lt;String, Object&gt;
	 * that you get back (ordinarily) is the data source for a single record of
	 * the data set. It can be queried using
	 * {@link JsonUtil#readField(Map, String[])}.
	 */
	public Object getEntity()
	{
		return entity;
	}

	/**
	 * Returns the parent of the entity, wrapped into another
	 * {@code EntityObject}.
	 * 
	 * @return
	 */
	public EntityObject getParentEntity()
	{
		return parent;
	}

}
