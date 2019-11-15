package nl.naturalis.nba.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.util.es.ESUtil;

public class DaoTestUtil {

	private static final DaoRegistry registry;
	@SuppressWarnings("unused")
	private static final Logger logger;

	static {
		registry = DaoRegistry.getInstance();
		logger = registry.getLogger(DaoTestUtil.class);
	}

	/**
	 * Asserts that the specified {@code QueryBuilder} instance has the same
	 * JSON representation as the contents of the specified file.
	 * 
	 * @param unitTestClass
	 * @param query
	 * @param file
	 * @return
	 */
	public static boolean queryEquals(QueryBuilder query, String file)
	{
		InputStream is = DaoTestUtil.class.getResourceAsStream(file);
		Map<String, Object> expected = JsonUtil.deserialize(is);
		Map<String, Object> actual = JsonUtil.deserialize(query.toString());
		return actual.equals(expected);
	}

	/**
	 * Asserts that the specified JSON string is equal the JSON in the specified
	 * file. Both JSON strings are first read into a map so formatting
	 * differences don't play a role.
	 * 
	 * @param unitTestClass
	 * @param jsonString
	 * @param jsonFile
	 * @return
	 */
	public static boolean jsonEquals(Class<?> unitTestClass, String jsonString, String jsonFile)
	{
		InputStream is = unitTestClass.getResourceAsStream(jsonFile);
		Map<String, Object> expected = JsonUtil.deserialize(is);
		Map<String, Object> actual = JsonUtil.deserialize(jsonString);
		return actual.equals(expected);
	}

	public static void saveSpecimens(Specimen... specimens)
	{
		DocumentType<?> dt = DocumentType.forClass(Specimen.class);
		ESUtil.disableAutoRefresh(dt.getIndexInfo());
		for (Specimen specimen : specimens) {
			saveSpecimen(specimen, false);
		}
		ESUtil.refreshIndex(dt.getIndexInfo());
	}

	public static void saveSpecimens(Collection<Specimen> specimens)
	{
		DocumentType<?> dt = DocumentType.forClass(Specimen.class);
		ESUtil.disableAutoRefresh(dt.getIndexInfo());
		for (Specimen specimen : specimens) {
			saveSpecimen(specimen, false);
		}
		ESUtil.refreshIndex(dt.getIndexInfo());
	}

	public static void saveSpecimen(Specimen specimen, boolean refreshIndex)
	{
		if (specimen.getId() == null) {
			String id = specimen.getUnitID() + "@" + specimen.getSourceSystem().getCode();
			// saveObject(id, null, specimen, refreshIndex);
			saveObject(id, specimen, refreshIndex);
		}
		else {
			String id = specimen.getId();
			specimen.setId(null);
			// saveObject(id, null, specimen, refreshIndex);
			saveObject(id, specimen, refreshIndex);
			specimen.setId(id);
		}
	}
	
	 public static void saveMultiMediaObject(MultiMediaObject mmo, boolean refreshIndex)
	  {
	    if (mmo.getId() == null) {
	      String id = mmo.getUnitID() + "@" + mmo.getSourceSystem().getCode();
	      // saveObject(id, null, mmo, refreshIndex);
	      saveObject(id, mmo, refreshIndex);
	    }
	    else {
	      String id = mmo.getId();
	      mmo.setId(null);
	      // saveObject(id, null, mmo, refreshIndex);
	      saveObject(id, mmo, refreshIndex);
	      mmo.setId(id);
	    }
	  }

	public static void saveGeoAreas(GeoArea... areas)
	{
		DocumentType<?> dt = DocumentType.forClass(GeoArea.class);
		ESUtil.disableAutoRefresh(dt.getIndexInfo());
		for (GeoArea area : areas) {
			saveGeoArea(area, false);
		}
		ESUtil.refreshIndex(dt.getIndexInfo());
	}

	public static void saveGeoArea(GeoArea area, boolean refreshIndex)
	{
		if (area.getId() == null) {
			String id = area.getSourceSystemId() + "@" + area.getSourceSystem().getCode();
			// saveObject(id, null, area, refreshIndex);
			saveObject(id, area, refreshIndex);
		}
		else {
			String id = area.getId();
			area.setId(null);
			// saveObject(id, null, area, refreshIndex);
			saveObject(id, area, refreshIndex);
			area.setId(id);
		}
	}

	public static void saveObject(IDocumentObject object, boolean refreshIndex)
	{
		// saveObject(null, null, object, refreshIndex);
	  saveObject(null, object, refreshIndex);
	}

//	public static void saveObject(String id, IDocumentObject object, boolean refreshIndex)
//	{
//		// saveObject(id, null, object, refreshIndex);
//	  saveObject(id, object, refreshIndex);
//	}
	
	 public static void saveObject(String id, IDocumentObject obj, boolean refreshIndex)
	  {
	    DocumentType<?> dt = DocumentType.forClass(obj.getClass());
	    String index = dt.getIndexInfo().getName();
	    // String type = dt.getName();
	    IndexRequest request = new IndexRequest();
	    request.index(index);
	    if (id != null) {
	      request.id(id);
	    }
	    byte[] source = JsonUtil.serialize(obj);
	    request.source(source, XContentType.JSON);
	    try {
        ESUtil.esClient().index(request, RequestOptions.DEFAULT);
        if (refreshIndex) {
          ESUtil.refreshIndex(dt);
        }   
      } catch (IOException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        throw new DaoException(String.format("Failed to index document in index \"%s\": %s", index, e.getMessage()));
      }
	  }

	
	
//  ES5
	 
//	  public static void saveObject(String id, IDocumentObject object, boolean refreshIndex)
//	  {
//	    saveObject(id, null, object, refreshIndex);
//	  }

//	public static void saveObject(String id, String parentId, IDocumentObject obj, boolean refreshIndex)
//	{
//		DocumentType<?> dt = DocumentType.forClass(obj.getClass());
//		String index = dt.getIndexInfo().getName();
//		String type = dt.getName();
//		IndexRequestBuilder irb = client().prepareIndex(index, type);
//		if (id != null) {
//			irb.setId(id);
//		}
//		if (parentId != null) {
//			irb.setParent(parentId);
//		}
//		byte[] data = JsonUtil.serialize(obj);
//		irb.setSource(data, XContentType.JSON);
//		irb.execute().actionGet();
//		if (refreshIndex) {
//			ESUtil.refreshIndex(dt);
//		}		
//	}

//	public static IndicesAdminClient indices()
//	{
//		return admin().indices();
//	}
//
//	private static AdminClient admin()
//	{
//		// return client().admin();
//	  return null;
//	}

	private static RestHighLevelClient client()
	{
		return ESClientManager.getInstance().getClient();
	}

}
