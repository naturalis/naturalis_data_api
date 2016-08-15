package nl.naturalis.nba.dao.es.util;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.es.DocumentType.TAXON;
import static nl.naturalis.nba.dao.es.util.ESUtil.createAllIndices;
import static nl.naturalis.nba.dao.es.util.ESUtil.deleteAllIndices;
import static nl.naturalis.nba.dao.es.util.ESUtil.disableAutoRefresh;
import static nl.naturalis.nba.dao.es.util.ESUtil.getAutoRefreshInterval;
import static nl.naturalis.nba.dao.es.util.ESUtil.getDistinctIndices;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.dao.es.util.ESUtil.setAutoRefreshInterval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.IndexInfo;

/*
 * We can't test that much here because we very much depend on how Elasticsearch
 * and NBA are configured. So we mainly use the test method to print out stuff
 * for which we would otherwise have created a temporary main(String[] args)
 * method.
 */
@SuppressWarnings("static-method")
public class ESUtilTest {

	static Logger logger = LogManager.getLogger(ESUtilTest.class);

	@Test
	public void testGetElasticsearchId_SourceSystem__int()
	{
		int colId = 12345;
		String esId = getElasticsearchId(COL, colId);
		logger.info("[testGetElasticsearchId_SourceSystem__int] Generated _id: \"{}\"", esId);
		String expected = colId + '@' + COL.getCode();
		assertEquals("01", expected, esId);
	}

	@Test
	public void testGetElasticsearchId_SourceSystem__String()
	{
		String brahmsId = "L   12345";
		String esId = getElasticsearchId(BRAHMS, brahmsId);
		logger.info("[testGetElasticsearchId_SourceSystem__String] Generated _id: \"{}\"", esId);
		String expected = brahmsId + '@' + BRAHMS.getCode();
		assertEquals("01", expected, esId);
	}

	@Test
	public void testGetDistinctIndices()
	{
		Set<IndexInfo> indices = getDistinctIndices();
		assertNotEquals("01", 0, indices.size());
	}

	@Test
	public void testGetDistinctIndices__DocumentTypeArray()
	{
		Set<IndexInfo> indices = getDistinctIndices(TAXON);
		assertEquals("01", 1, indices.size());
	}

	@Test
	public void testDeleteAllIndices()
	{
	}

	@Test
	public void testCreateAllIndices()
	{
	}

	@Test
	public void testDeleteIndex_01()
	{
	}

	@Test
	public void testDeleteIndex_02()
	{
	}

	@Test
	public void testCreateIndex__DocumentType()
	{
	}

	@Test
	public void testCreateIndex__IndexInfo()
	{
	}

	@Test
	public void testRefreshIndex__DocumentType()
	{
	}

	@Test
	public void testRefreshIndex__IndexInfo()
	{
	}

	@Test
	public void testGetAutoRefreshInterval()
	{
		deleteAllIndices();
		/* This picks up the refresh interval from es-settings.json: */
		createAllIndices();
		InputStream is = ESUtil.class.getResourceAsStream("/es-settings.json");
		String setting = String.valueOf(JsonUtil.readField(is, "index.refresh_interval"));
		logger.info("[testGetAutoRefreshInterval] index.refresh_interval is {}", setting);
		Set<IndexInfo> indices = getDistinctIndices();
		IndexInfo first = indices.iterator().next();
		String interval = getAutoRefreshInterval(first);
		logger.info("[testGetAutoRefreshInterval] Refresh interval is {}", interval);
		assertEquals("01", setting, interval);
	}

	@Test
	public void testDisableAutoRefresh()
	{
		deleteAllIndices();
		createAllIndices();
		Set<IndexInfo> indices = getDistinctIndices();
		IndexInfo first = indices.iterator().next();
		String interval0 = getAutoRefreshInterval(first);
		String interval1 = disableAutoRefresh(first);
		assertEquals("01", interval0, interval1);
	}

	@Test
	public void testSetAutoRefreshInterval()
	{
		deleteAllIndices();
		createAllIndices();
		Set<IndexInfo> indices = getDistinctIndices();
		IndexInfo first = indices.iterator().next();
		String origValue = getAutoRefreshInterval(first);
		logger.info("[testSetAutoRefreshInterval] Original refresh interval: {}", origValue);
		String newValue = "4s";
		setAutoRefreshInterval(first, newValue);
		String interval0 = getAutoRefreshInterval(first);
		logger.info("[testSetAutoRefreshInterval] New refresh interval: {}", interval0);
		assertEquals("01", newValue, interval0);
		setAutoRefreshInterval(first, origValue);
		interval0 = getAutoRefreshInterval(first);
		logger.info("[testSetAutoRefreshInterval] Reset refresh interval: {}", interval0);
		assertEquals("01", origValue, interval0);
	}

	@Test
	public void testCreateType()
	{
	}

	@Test
	public void testFind()
	{
	}

}
