package nl.naturalis.nba.dao.es.format;

import java.util.Map;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.es.DocumentType;

/**
 * An {@code Entity} is the object within an Elasticsearch {@link DocumentType
 * document type} that must be considered as the main entity for a
 * {@link DataSetEntity file} within a data set. Suppose, for example, that you
 * want to print out specimen collector information, then the entity object
 * would be the {@link GatheringEvent#getGatheringPersons() gatheringPersons}
 * object within the {@link Specimen#getGatheringEvent() gatheringEvent} object
 * within the {@link DocumentType#SPECIMEN Specimen} document type. Since there
 * may be multiple collectors associated with a specimen, one specimen document
 * may yield multiple specimen collector records. The entity object may possibly
 * be the entire Elasticsearch document rather than any object nested within it.
 * 
 * See also {@link DataSetEntity#getPathToEntity()}.
 * 
 * @author Ayco Holleman
 *
 */
public class Entity {

	private Map<String, Object> data;
	private Entity parent;

	Entity(Map<String, Object> data, Entity parent)
	{
		this.data = data;
		this.parent = parent;
	}

	public Map<String, Object> getDocument()
	{
		if (parent == null)
			return data;
		Entity dn;
		for (dn = parent; dn.parent != null; dn = dn.parent)
			;
		return dn.data;
	}

	public Map<String, Object> getData()
	{
		return data;
	}

}
