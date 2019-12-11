package nl.naturalis.nba.etl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;

public class BulkIndexer<T extends IDocumentObject> {
  
  private static final Logger logger = ETLRegistry.getInstance().getLogger(BulkIndexer.class);

	private static final TimeValue REQUEST_TIMEOUT = TimeValue.timeValueMinutes(5);

	private DocumentType<T> dt;

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
	    for (int i = 0; i < objs.size(); i++) {
	      ids[i] = objs.get(i).getId();
	      objs.get(i).setId(null);
	    }
	    index(objs, Arrays.asList(ids));
	  }

	
	public void index(List<T> documents, List<String> ids)
			throws BulkIndexException
	{
		if (documents.size() == 0) {
			/*
			 * Contrary to Elasticsearch we are OK with saving 0 documents
			 */
			return;
		}
		RestHighLevelClient client = ESClientManager.getInstance().getClient();
		String index = dt.getIndexInfo().getName();

		ObjectMapper om = dt.getObjectMapper();
		
		// TODO: find out what works best in order to prevent performance issues
		// during a bulk transaction
		BulkRequest bulkRequest = new BulkRequest();
		// bulkRequest.timeout(REQUEST_TIMEOUT);
		bulkRequest.timeout(TimeValue.ZERO);
		// bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
		
		for (int i = 0; i < documents.size(); ++i) {
		  IndexRequest indexRequest = new IndexRequest(index);
		  try {
		    if (ids != null) {
		      indexRequest.id(ids.get(i));
		    }		    
		    indexRequest.source(om.writeValueAsBytes(documents.get(i)), XContentType.JSON);		    
      } catch (JsonProcessingException e) {
        throw new DaoException(e);
      }
		  bulkRequest.add(indexRequest);
		}
		
		try {
      BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      boolean hasFailed = false;
      List<T> failedDocs = new ArrayList<>();
      if (bulkResponse.hasFailures()) {
        logger.warn("There were errors while executing the BulkRequest");
        hasFailed = true;
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
          if (bulkItemResponse.isFailed()) { 
              BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
              failedDocs.add(documents.get( bulkItemResponse.getItemId() ));
              logger.error("Failed to index document {}: {}", failure.getId(), failure.getMessage());              
          }
        }
      }
      if (hasFailed) throw new BulkIndexException(bulkResponse, documents);
    } catch (IOException e) {
      throw new DaoException("Failed to execute a bulk index: " + e.getMessage());
    }
	}

}
