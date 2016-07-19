package nl.naturalis.nba.dao.es;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;

import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESType;
import nl.naturalis.nba.dao.es.util.ESUtil;

public class ESTestUtils {

	private static final DAORegistry registry;
	@SuppressWarnings("unused")
	private static final Logger logger;

	static {
		registry = DAORegistry.getInstance();
		logger = registry.getLogger(ESTestUtils.class);
	}

	public static void saveSpecimens(ESSpecimen... specimens)
	{
		DocumentType dt = DocumentType.forClass(ESSpecimen.class);
		ESUtil.disableAutoRefresh(dt.getIndexInfo());
		for (ESSpecimen specimen : specimens) {
			saveSpecimen(specimen, false);
		}
		ESUtil.refreshIndex(dt.getIndexInfo());
	}

	public static void saveSpecimen(ESSpecimen specimen, boolean refreshIndex)
	{
		String id = specimen.getUnitID() + "@" + specimen.getSourceSystem().getCode();
		saveObject(id, null, specimen, refreshIndex);
	}

	public static void saveObject(ESType object, boolean refreshIndex)
	{
		saveObject(null, null, object, refreshIndex);
	}

	public static void saveObject(String id, ESType object, boolean refreshIndex)
	{
		saveObject(id, null, object, refreshIndex);
	}

	public static void saveObject(String id, String parentId, ESType obj, boolean refreshIndex)
	{
		DocumentType dt = DocumentType.forClass(obj.getClass());
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		String source = JsonUtil.toJson(obj);
		IndexRequestBuilder irb = client().prepareIndex(index, type);
		if (id != null) {
			irb.setId(id);
		}
		if (parentId != null) {
			irb.setParent(parentId);
		}
		irb.setSource(source);
		irb.execute().actionGet();
		if (refreshIndex) {
			ESUtil.refreshIndex(dt);
		}
	}

	public static IndicesAdminClient indices()
	{
		return admin().indices();
	}

	private static AdminClient admin()
	{
		return client().admin();
	}

	private static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}

}
