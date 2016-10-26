package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.ISpecimenAccess;

/**
 * Interface indicating that each instance of a class implementing this
 * interface corresponds to a single document in the Elasticsearch document
 * store underlying the NBA. Only instances of classes implementing this
 * interface can be retrieved using the API's {@code find()} methods (e.g.
 * {@link ISpecimenAccess#find(String)}). N.B. Contrast this with
 * {@link NbaTraceableObject}. Instances of these classes can be traced back to
 * <i>source system</i> records. Source system records can, in principle, be
 * manipulated, split and joined beyond recognition before becoming an
 * Elasticsearch document. Therefore it is not necessarily the case that all
 * Elasticsearch documents can be traced back to a single source system record.
 * 
 * @author Ayco Holleman
 *
 */
public interface IDocumentObject extends INbaModelObject {

	/**
	 * Returns the Elasticsearch system ID of the document corresponding to this
	 * instance. This is the value of the standard Elasticsearch {@code _id}
	 * field (not part of the document itself but of the {@code SearchHit}
	 * encapsulating the document).
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Sets the value of the Elasticsearch system ID. We don't let Elasticsearch
	 * auto-generate IDs. Instead, IDs are generated as follows:<br>
	 * <br>
	 * "&lt;{@link NbaTraceableObject#getSourceSystemId() source-system-id}&gt;"
	 * {@code + "@" +} "&lt;{@link SourceSystem#getCode()
	 * source-system-code}&gt;"<br>
	 * <br>
	 * (The source system code is a code assigned to the source system, e.g.
	 * BRAHMS, not some record-level code.)
	 */
	public void setId(String id);

}
