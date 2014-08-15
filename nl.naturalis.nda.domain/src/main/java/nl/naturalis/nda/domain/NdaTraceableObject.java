package nl.naturalis.nda.domain;

/**
 * The base class for all domain objects within the NDA API that can be traced
 * back to a single record in one of the NDA's source systems. In other words,
 * these objects are not aggregations or assembled from multiple source systems.
 */
public abstract class NdaTraceableObject extends NdaDomainObject {

	private SourceSystem sourceSystem;
	private String sourceSystemId;


	public SourceSystem getSourceSystem()
	{
		return sourceSystem;
	}


	public void setSourceSystem(SourceSystem sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}


	public String getSourceSystemId()
	{
		return sourceSystemId;
	}


	public void setSourceSystemId(String sourceSystemId)
	{
		this.sourceSystemId = sourceSystemId;
	}

}
