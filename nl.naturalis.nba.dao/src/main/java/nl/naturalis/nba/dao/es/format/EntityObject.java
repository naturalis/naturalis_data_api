package nl.naturalis.nba.dao.es.format;

import java.util.Map;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * An entity object is the object within an Elasticsearch {@link DocumentType
 * document} that functions as the main entity for a {@link Entity
 * file} within a data set. Suppose, for example, that you want to print out
 * specimen collector information, then the entity object would be the
 * {@link GatheringEvent#getGatheringPersons() gatheringPersons} object within
 * the {@link Specimen#getGatheringEvent() gatheringEvent} object within the
 * {@link DocumentType#SPECIMEN Specimen} document type. Since there may be
 * multiple collectors associated with a specimen, one specimen document may
 * yield multiple specimen collector records. The entity object may possibly be
 * the entire Elasticsearch document rather than any object nested within it
 * (e.g. if you wanted to print out specimen information).
 * 
 * See also {@link Entity#getPath()}.
 * 
 * @author Ayco Holleman
 *
 */
public class EntityObject {

	private Map<String, Object> data;
	private EntityObject parent;

	EntityObject(Map<String, Object> data, EntityObject parent)
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
