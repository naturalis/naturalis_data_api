package nl.naturalis.nba.etl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;

public class BulkIndexer<T extends IDocumentObject> {

	private static final TimeValue REQUEST_TIMEOUT = TimeValue.timeValueMinutes(5);

	private final DocumentType<T> dt;

	public BulkIndexer(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	public void index(Collection<T> documents) throws BulkIndexException
	{
		ArrayList<T> objs;
		if (documents.getClass() == ArrayList.class) {
			objs = (ArrayList<T>) documents;
		}
		else {
			objs = new ArrayList<>(documents);
		}
		String[] ids = new String[objs.size()];
		/*
		 * For some deeply mysterious reason defying anything I thought I understood about
		 * Java it seems we have to do this in two separate loops, otherwise
		 * objs.get(i).getId() returns null where it really really shouldn't:
		 */
		for (int i = 0; i < objs.size(); i++) {
			ids[i] = objs.get(i).getId();
		}
		for (int i = 0; i < objs.size(); i++) {
			objs.get(i).setId(null);
		}
		index(objs, Arrays.asList(ids), null);
		for (int i = 0; i < objs.size(); i++) {
			objs.get(i).setId(ids[i]);
		}
	}

	public void index(List<T> documents, List<String> ids, List<String> parentIds)
			throws BulkIndexException
	{
		Client client = ESClientManager.getInstance().getClient();
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		ObjectMapper om = dt.getObjectMapper();
		BulkRequestBuilder brb = client.prepareBulk();
		brb.setTimeout(REQUEST_TIMEOUT);
		for (int i = 0; i < documents.size(); ++i) {
			IndexRequestBuilder irb = client.prepareIndex(index, type);
			try {
				irb.setSource(om.writeValueAsBytes(documents.get(i)));
				if (ids != null) {
					irb.setId(ids.get(i));
				}
				if (parentIds != null) {
					irb.setParent(parentIds.get(i));
				}
			}
			catch (JsonProcessingException e) {
				throw new DaoException(e);
			}
			brb.add(irb);
		}
		BulkResponse response = brb.get();
		if (response.hasFailures()) {
			throw new BulkIndexException(response, documents);
		}
	}

}
