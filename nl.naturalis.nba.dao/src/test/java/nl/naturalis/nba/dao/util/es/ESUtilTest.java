package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.createAllIndices;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteAllIndices;
import static nl.naturalis.nba.dao.util.es.ESUtil.getDistinctIndices;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.dao.util.es.ESUtil.getIndexSetting;
import static nl.naturalis.nba.dao.util.es.ESUtil.getIndexSettings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.IndexInfo;
import nl.naturalis.nba.dao.util.es.ESUtil;

/**
 * Unit tests for methods in the ESUtil class. We can't test that much here
 * because we very much depend on how Elasticsearch and NBA are configured. So
 * we mainly use the test method to print out stuff for which we would otherwise
 * have created a temporary main(String[] args) method.
 */
@SuppressWarnings("static-method")
public class ESUtilTest {

	@BeforeClass
	public static void beforeClass()
	{
		deleteAllIndices();
		createAllIndices();
	}

	@Test
	public void testGetElasticsearchId_01()
	{
		int colId = 12345;
		String esId = getElasticsearchId(COL, colId);
		String expected = "12345@" + COL.getCode();
		assertEquals("01", expected, esId);
	}

	@Test
	public void testGetElasticsearchId_02()
	{
		String brahmsId = "L   12345";
		String esId = getElasticsearchId(BRAHMS, brahmsId);
		String expected = brahmsId + '@' + BRAHMS.getCode();
		assertEquals("01", expected, esId);
	}

	@Test
	public void testGetIndexSetting_01()
	{
		InputStream is = ESUtil.class.getResourceAsStream("/es-settings.json");
		Map<String, Object> data = JsonUtil.deserialize(is);
		String setting = "index.refresh_interval";
		/*
		 * NB Elasticsearch always returns settings as strings, but the JSON
		 * file that configures those settings may use other data types (e.g.
		 * integers). So we must stringify the expected value.
		 */
		String expected = JsonUtil.readField(data, setting).toString();
		String actual = getIndexSetting(MULTI_MEDIA_OBJECT, setting);
		assertEquals("01", expected, actual);
	}

	@Test
	public void testGetIndexSetting_02()
	{
		InputStream is = ESUtil.class.getResourceAsStream("/es-settings.json");
		Map<String, Object> data = JsonUtil.deserialize(is);
		String setting = "index.max_result_window";
		String expected = JsonUtil.readField(data, setting).toString();
		String actual = getIndexSetting(MULTI_MEDIA_OBJECT, setting);
		assertEquals("01", expected, actual);
	}

	@Test
	public void testGetIndexSettings_01()
	{
		InputStream is = ESUtil.class.getResourceAsStream("/es-settings.json");
		Map<String, Object> data = JsonUtil.deserialize(is);
		String[] settings = new String[] { "index.refresh_interval", "index.max_result_window" };
		Map<String, String> config = getIndexSettings(TAXON, settings);
		ArrayList<String> keys = new ArrayList<>(config.keySet());
		assertEquals("01", "index.refresh_interval", keys.get(0));
		assertEquals("02", "index.max_result_window", keys.get(1));
		ArrayList<String> values = new ArrayList<>(config.values());
		assertEquals("03", JsonUtil.readField(data, "index.refresh_interval").toString(),
				values.get(0));
		assertEquals("04", JsonUtil.readField(data, "index.max_result_window").toString(),
				values.get(1));
	}

	@Test
	public void testGetDistinctIndices_01()
	{
		Set<IndexInfo> indices = getDistinctIndices();
		assertNotEquals("01", 0, indices.size());
	}

	@Test
	public void testGetDistinctIndices_02()
	{
		Set<IndexInfo> indices = getDistinctIndices(TAXON);
		assertEquals("01", 1, indices.size());
	}

}
