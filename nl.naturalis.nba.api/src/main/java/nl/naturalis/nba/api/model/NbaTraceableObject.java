package nl.naturalis.nba.api.model;

import java.net.URI;

/**
 * Abstract base class for all domain model classes that can be traced back to a
 * single, identifiable record in one of the NBA's source systems.
 */
public abstract class NbaTraceableObject {

	private SourceSystem sourceSystem;
	private String sourceSystemId;
	private URI recordURI;

	/**
	 * Returns the source system that contained the record corresponding to this
	 * instance.
	 * 
	 * @return
	 */
	public SourceSystem getSourceSystem()
	{
		return sourceSystem;
	}

	/**
	 * Sets the source system that contained the record corresponding to this
	 * instance.
	 * 
	 * @param sourceSystem
	 */
	public void setSourceSystem(SourceSystem sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}

	/**
	 * Returns the ID of the record within the source system. This is typically
	 * an auto-generated database ID.
	 * 
	 * @return
	 */
	public String getSourceSystemId()
	{
		return sourceSystemId;
	}

	/**
	 * Sets the ID of the record within the source system. This is typically an
	 * auto-generated database ID.
	 * 
	 * @param sourceSystemId
	 */
	public void setSourceSystemId(String sourceSystemId)
	{
		this.sourceSystemId = sourceSystemId;
	}

	/**
	 * Returns the URI through which the source system record can be accessed,
	 * if applicable.
	 * 
	 * @return
	 */
	public URI getRecordURI()
	{
		return recordURI;
	}

	/**
	 * Sets the URI through which the source system record can be accessed, if
	 * applicable.
	 * 
	 * @param recordURI
	 */
	public void setRecordURI(URI recordURI)
	{
		this.recordURI = recordURI;
	}

}
