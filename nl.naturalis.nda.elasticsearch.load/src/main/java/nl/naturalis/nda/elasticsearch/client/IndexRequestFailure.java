package nl.naturalis.nda.elasticsearch.client;

import org.elasticsearch.action.bulk.BulkItemResponse;

/**
 * Provides information about an index request failure.
 * 
 * @author Ayco Holleman
 *
 */
public class IndexRequestFailure {

	private final String index;
	private final String type;
	private final Object object;
	private final String message;

	public IndexRequestFailure(BulkItemResponse response, Object obj)
	{
		this.index = response.getIndex();
		this.type = response.getType();
		this.object = obj;
		this.message = response.getFailureMessage();
	}

	/**
	 * The ElasticSearch index that the object belongs to.
	 * 
	 * @return
	 */
	public String getIndex()
	{
		return index;
	}

	/**
	 * The ElasticSearch type of the object.
	 * 
	 * @return
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * The object that failed to be indexed.
	 * 
	 * @return
	 */
	public Object getObject()
	{
		return object;
	}

	/**
	 * The failure message from ElasticSearch.
	 * 
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}

}
