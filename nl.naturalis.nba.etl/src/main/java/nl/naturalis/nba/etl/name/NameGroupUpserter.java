package nl.naturalis.nba.etl.name;

import java.util.Collection;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.etl.BulkIndexException;

public class NameGroupUpserter {

	private static final TimeValue REQUEST_TIMEOUT = TimeValue.timeValueMinutes(5);

	static void upsert(Collection<NameGroup> nameGroups) throws BulkIndexException
	{
		Client client = ESClientManager.getInstance().getClient();
		DocumentType<NameGroup> dt = DocumentType.NAME_GROUP;
		ObjectMapper om = dt.getObjectMapper();
		BulkRequestBuilder brb = client.prepareBulk();
		brb.setTimeout(REQUEST_TIMEOUT);
		for (NameGroup nameGroup : nameGroups) {
			boolean isNewNameGroup = nameGroup.getId() == null;
			/*
			 * Set id to null b/c it should not appear in the JSON document to
			 * be inserted/updated. It is the system ID of the document, which
			 * is not part of the document source itself.
			 */
			nameGroup.setId(null);
			byte[] data;
			try {
				data = om.writeValueAsBytes(nameGroup);
			}
			catch (JsonProcessingException e) {
				throw new DaoException(e);
			}
			if (isNewNameGroup) {
				IndexRequest request = new IndexRequest();
				request.index(dt.getIndexInfo().getName());
				request.type(dt.getName());
				request.id(nameGroup.getName());
				request.source(data);
				brb.add(request);
			}
			else {
				UpdateRequest request = new UpdateRequest();
				request.index(dt.getIndexInfo().getName());
				request.type(dt.getName());
				request.id(nameGroup.getName());
				request.doc(data);
				brb.add(request);
			}
		}
		BulkResponse response = brb.get();
		if (response.hasFailures()) {
			throw new BulkIndexException(response, nameGroups);
		}
	}

}
