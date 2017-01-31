package nl.naturalis.nba.dao.format;

import java.util.Map;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;

/**
 * An entity object is an Elasticsearch document or an object nested within it
 * that functions as the main data source for a record within a data set.
 * Suppose, for example, that you want to print out specimen collector
 * information, then the entity object would be the
 * {@link GatheringEvent#getGatheringPersons() gatheringPersons} object within
 * the {@link Specimen#getGatheringEvent() gatheringEvent} object within the
 * {@link DocumentType#SPECIMEN Specimen} document type. Since there may be
 * multiple collectors associated with a specimen, one specimen document may
 * yield multiple specimen collector records.
 * 
 * See also {@link Entity#toString()}.
 * 
 * @author Ayco Holleman
 *
 */
public class EntityObject {

	private Map<String, Object> data;
	private EntityObject parent;

	public EntityObject(Map<String, Object> data)
	{
		this.data = data;
		this.parent = null;
	}

	public EntityObject(Map<String, Object> data, EntityObject parent)
	{
		this.data = data;
		this.parent = parent;
	}

	/**
	 * Returns the entire document of which this entity object was part of. Even
	 * though the records you write to a data set will mostly contain data from
	 * the entity object, some data may need to come from the parent or
	 * ancestors of the entity object. Hence this method.
	 */
	public Map<String, Object> getDocument()
	{
		if (parent == null)
			return data;
		EntityObject dn;
		for (dn = parent; dn.parent != null; dn = dn.parent)
			;
		return dn.data;
	}

	/**
	 * Returns the raw data of the entity object. The Map&lt;String, Object&gt;
	 * that you get back (ordinarily) is the data source for a single record of
	 * the data set. It can be queried using
	 * {@link JsonUtil#readField(Map, String[])}.
	 */
	public Map<String, Object> getData()
	{
		return data;
	}

}
