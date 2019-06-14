package nl.naturalis.nba.etl.utils;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class ETLDaoUtil {
  
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
  
  
  public static void saveObject(String id, String parentId, IDocumentObject obj,
      boolean refreshIndex)
  {
    DocumentType<?> dt = DocumentType.forClass(obj.getClass());
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    IndexRequestBuilder irb = client().prepareIndex(index, type);
    if (id != null) {
      irb.setId(id);
    }
    if (parentId != null) {
      irb.setParent(parentId);
    }
    byte[] data = JsonUtil.serialize(obj);
    irb.setSource(data, XContentType.JSON);
    irb.execute().actionGet();
    if (refreshIndex) {
      ESUtil.refreshIndex(dt);
    }
  }

    private static Client client()
  {
    return ESClientManager.getInstance().getClient();
  }
}
