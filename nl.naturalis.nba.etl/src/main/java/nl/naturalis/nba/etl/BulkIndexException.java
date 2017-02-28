package nl.naturalis.nba.etl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

public class BulkIndexException extends Exception {

	private int bulkRequestSize;
	private List<BulkIndexFailure> failures;

	public BulkIndexException(BulkResponse response, Collection<?> objs)
	{
		super(response.buildFailureMessage());
		bulkRequestSize = objs.size();
		failures = new ArrayList<>();
		Iterator<BulkItemResponse> iter0 = response.iterator();
		Iterator<?> iter1 = objs.iterator();
		while (iter0.hasNext()) {
			BulkItemResponse item = iter0.next();
			Object obj = iter1.next();
			if (item.isFailed())
				failures.add(new BulkIndexFailure(item, obj));
		}
	}

	/**
	 * Returns the total number of objects in the bulk index request.
	 * 
	 * @return
	 */
	public int getBulkRequestSize()
	{
		return bulkRequestSize;
	}

	/**
	 * Returns the number of successfully indexed objects.
	 * 
	 * @return
	 */
	public int getSuccessCount()
	{
		return bulkRequestSize - failures.size();
	}

	/**
	 * Returns the number of objects that failed to be indexed.
	 * 
	 * @return
	 */
	public int getFailureCount()
	{
		return failures.size();
	}

	/**
	 * Returns a failure objects for each of the objects that failed to get
	 * indexed.
	 * 
	 * @return
	 */
	public List<BulkIndexFailure> getFailures()
	{
		return failures;
	}

}
