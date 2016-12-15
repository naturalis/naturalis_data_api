package nl.naturalis.nba.etl;

import org.elasticsearch.action.bulk.BulkItemResponse;

/**
 * Provides information about an index request failure.
 * 
 * @author Ayco Holleman
 *
 */
class BulkIndexFailure {

	private String index;
	private String type;
	private Object object;
	private String message;

	BulkIndexFailure(BulkItemResponse response, Object obj)
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
	String getIndex()
	{
		return index;
	}

	/**
	 * The ElasticSearch type of the object.
	 * 
	 * @return
	 */
	String getType()
	{
		return type;
	}

	/**
	 * The object that failed to be indexed.
	 * 
	 * @return
	 */
	Object getObject()
	{
		return object;
	}

	/**
	 * The failure message from ElasticSearch.
	 * 
	 * @return
	 */
	String getMessage()
	{
		return message;
	}
}
