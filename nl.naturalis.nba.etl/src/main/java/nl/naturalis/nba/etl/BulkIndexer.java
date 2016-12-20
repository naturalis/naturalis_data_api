package nl.naturalis.nba.etl;

import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.util.es.ESUtil;

class BulkIndexer<T extends IDocumentObject> {

	private final DocumentType<T> dt;

	BulkIndexer(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	void index(List<T> objs, List<String> ids, List<String> parentIds)
			throws BulkIndexException
	{
		Client client = ESClientManager.getInstance().getClient();
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		ObjectMapper om = dt.getObjectMapper();
		BulkRequestBuilder brb = client.prepareBulk();
		for (int i = 0; i < objs.size(); ++i) {
			IndexRequestBuilder irb = client.prepareIndex(index, type);
			try {
				irb.setSource(om.writeValueAsBytes(objs.get(i)));
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
			throw new BulkIndexException(response, objs);
		}
	}

}
