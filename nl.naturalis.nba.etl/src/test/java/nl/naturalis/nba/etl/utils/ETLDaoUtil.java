package nl.naturalis.nba.etl.utils;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;

public class ETLDaoUtil {
  
  private static final Logger logger = getLogger(ETLDaoUtil.class);
  
  public static void saveMultiMediaObject(MultiMediaObject mmo, boolean refreshIndex)
  {
    if (mmo.getId() == null) {
      String id = mmo.getUnitID() + "@" + mmo.getSourceSystem().getCode();
      saveObject(id, null, mmo, refreshIndex);
    }
    else {
      String id = mmo.getId();
      mmo.setId(null);
      saveObject(id, null, mmo, refreshIndex);
      mmo.setId(id);
    }
  }
  
  
  public static void saveObject(String id, String parentId, IDocumentObject obj, boolean refreshIndex) {
    DocumentType<?> dt = DocumentType.forClass(obj.getClass());
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();

    // ES5
//    IndexRequestBuilder irb = client().prepareIndex(index, type);
//    if (id != null) {
//      irb.setId(id);
//    }
//    if (parentId != null) {
//      irb.setParent(parentId);
//    }
//    byte[] data = JsonUtil.serialize(obj);
//    irb.setSource(data, XContentType.JSON);
//    irb.execute().actionGet();
//    if (refreshIndex) {
//      ESUtil.refreshIndex(dt);
//    }
    

    // ES7
    IndexRequest request = new IndexRequest(index); 
    if (id != null) {
      request.id(id);       
    }
    String jsonString = JsonUtil.toJson(obj);
    request.source(jsonString, XContentType.JSON); 
    try {
      IndexResponse indexResponse = client().index(request, RequestOptions.DEFAULT);
      if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
        // Handle (if needed) the case where the document was created for the first time
        logger.info("Document has been saved in index {} with id {}", index, id);
    } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
        // Handle (if needed) the case where the document was rewritten as it was already existing
      logger.info("Document with id {} in index {} has been updated", id, index);
    }
    } catch (IOException e) {
      throw new DaoException(String.format("Failed to save object with id %s in index %s", id, index));
    }
  }

    private static RestHighLevelClient client()
  {
    return ESClientManager.getInstance().getClient();
  }
}
