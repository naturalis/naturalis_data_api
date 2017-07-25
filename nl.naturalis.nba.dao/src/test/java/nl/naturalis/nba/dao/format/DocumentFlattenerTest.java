package nl.naturalis.nba.dao.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.EntityObject;

public class DocumentFlattenerTest {

	private Map<String, Object> taxon;
	private Map<String, Object> sourceSystem;
	private Map<String, Object> acceptedName;
	private Map<String, Object> vernacularName0;
	private Map<String, Object> vernacularName1;
	private Map<String, Object> reference0;
	private Map<String, Object> reference1;
	private List<Map<String, Object>> references;
	private List<Map<String, Object>> vernacularNames;

	@Before
	public void init()
	{
		taxon = new HashMap<>();
		taxon.put("sourceSystemId", "1");

		sourceSystem = new HashMap<>();
		sourceSystem.put("code", "NSR");
		sourceSystem.put("name", "Nederlands Soortenregister");
		taxon.put("sourceSystem", sourceSystem);

		acceptedName = new HashMap<>();
		acceptedName.put("fullScientificName", "Larus fuscus");
		taxon.put("acceptedName", acceptedName);

		vernacularName0 = new HashMap<>();
		vernacularName0.put("name", "meeuw");
		vernacularName0.put("language", "nl");

		vernacularName1 = new HashMap<>();
		vernacularName1.put("name", "sea gull");
		vernacularName1.put("language", "en");

		reference0 = new HashMap<>();
		reference0.put("title", "Vogels van Europa");
		reference0.put("author", "Ruud Altenburg");

		reference1 = new HashMap<>();
		reference1.put("title", "The Secret Life of Sea Gulls");
		reference1.put("author", "John Smith");

		references = Arrays.asList(reference0, reference1);
		vernacularName0.put("references", references);
		vernacularName1.put("references", references);

		vernacularNames = Arrays.asList(vernacularName0, vernacularName1);
		taxon.put("vernacularNames", vernacularNames);

	}

	@Test
	public void testFlatten_01()
	{
		DocumentFlattener df = new DocumentFlattener(new Path(new String[0]), 1);
		List<EntityObject> records = df.flatten(taxon);
		assertEquals("01", 1, records.size());
		assertTrue("02", records.get(0).getEntity() == taxon);
		assertTrue("03", records.get(0).getDocument() == taxon);
	}

	@Test
	public void testFlatten_02()
	{
		DocumentFlattener df = new DocumentFlattener(new Path("vernacularNames"), 4);
		List<EntityObject> records = df.flatten(taxon);
		assertEquals("01", 2, records.size());
		assertTrue("02", records.get(0).getEntity() == vernacularName0);
		assertTrue("03", records.get(1).getEntity() == vernacularName1);
		assertTrue("04", records.get(0).getDocument() == taxon);
		assertTrue("05", records.get(1).getDocument() == taxon);
	}

	@Test
	public void testFlatten_03()
	{
		DocumentFlattener df = new DocumentFlattener(new Path("acceptedName"), 1);
		List<EntityObject> records = df.flatten(taxon);
		assertEquals("01", 1, records.size());
		assertTrue("02", records.get(0).getEntity() == acceptedName);
		assertTrue("03", records.get(0).getDocument() == taxon);
	}

	@Test
	public void testFlatten_04()
	{
		DocumentFlattener df = new DocumentFlattener(new Path("vernacularNames.references"), 4);
		List<EntityObject> records = df.flatten(taxon);
		assertEquals("01", 4, records.size());
		assertTrue("02", records.get(0).getEntity() == reference0);
		assertTrue("03", records.get(1).getEntity() == reference1);
		assertTrue("04", records.get(2).getEntity() == reference0);
		assertTrue("05", records.get(3).getEntity() == reference1);
		assertTrue("06", records.get(0).getDocument() == taxon);
		assertTrue("07", records.get(1).getDocument() == taxon);
		assertTrue("06", records.get(2).getDocument() == taxon);
		assertTrue("07", records.get(3).getDocument() == taxon);
	}

}
