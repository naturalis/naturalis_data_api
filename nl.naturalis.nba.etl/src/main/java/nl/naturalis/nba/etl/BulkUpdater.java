package nl.naturalis.nba.etl;

import java.util.Collection;
import java.util.Iterator;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;

public class BulkUpdater<T extends IDocumentObject> {

	private static final TimeValue REQUEST_TIMEOUT = TimeValue.timeValueMinutes(5);

	private final DocumentType<T> dt;

	public BulkUpdater(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	public int update(Collection<T> objs) throws BulkIndexException
	{
		Client client = ESClientManager.getInstance().getClient();
		ObjectMapper om = dt.getObjectMapper();
		BulkRequestBuilder brb = client.prepareBulk();
		brb.setTimeout(REQUEST_TIMEOUT);
		for (T obj : objs) {
			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(dt.getIndexInfo().getName());
			updateRequest.type(dt.getName());
			updateRequest.id(obj.getId());
			obj.setId(null);
			try {
				updateRequest.doc(om.writeValueAsBytes(obj));
			}
			catch (JsonProcessingException e) {
				throw new DaoException(e);
			}
			brb.add(updateRequest);
		}
		BulkResponse response = brb.get();
		if (response.hasFailures()) {
			throw new BulkIndexException(response, objs);
		}
		int updates = 0;
		Iterator<BulkItemResponse> iterator = response.iterator();
		while (iterator.hasNext()) {
			BulkItemResponse item = iterator.next();
			Result result = item.getResponse().getResult();
			if (result == Result.UPDATED) {
				++updates;
			}
		}
		return updates;
	}

}
