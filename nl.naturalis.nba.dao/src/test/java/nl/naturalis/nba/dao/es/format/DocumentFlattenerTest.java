package nl.naturalis.nba.dao.es.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

@SuppressWarnings("static-method")
public class DocumentFlattenerTest {

	@Test
	public void testFlatten_01()
	{
		Map<String, Object> taxon = new HashMap<>();
		taxon.put("sourceSystemId", "1");

		Map<String, Object> sourceSystem = new HashMap<>();
		sourceSystem.put("code", "NSR");
		sourceSystem.put("name", "Nederlands Soortenregister");
		taxon.put("sourceSystem", sourceSystem);

		Map<String, Object> acceptedName = new HashMap<>();
		acceptedName.put("fullScientificName", "Larus fuscus");
		taxon.put("acceptedName", acceptedName);

		Map<String, Object> vernacularName0 = new HashMap<>();
		vernacularName0.put("name", "meeuw");
		vernacularName0.put("language", "nl");

		Map<String, Object> vernacularName1 = new HashMap<>();
		vernacularName0.put("name", "sea gull");
		vernacularName0.put("language", "en");

		Map<String, Object> reference0 = new HashMap<>();
		reference0.put("title", "Vogels van Europa");
		reference0.put("author", "Ruud Altenburg");

		Map<String, Object> reference1 = new HashMap<>();
		reference1.put("title", "The Secret Life of Sea Gulls");
		reference1.put("author", "John Smith");

		List<Map<String, Object>> references = Arrays.asList(reference0, reference1);
		vernacularName0.put("references", references);
		vernacularName1.put("references", references);

		List<Map<String, Object>> vernacularNames = Arrays.asList(vernacularName0, vernacularName1);
		taxon.put("vernacularNames", vernacularNames);

		DocumentFlattener df = new DocumentFlattener(new String[] { "vernacularNames" }, 4);
		List<Map<String, Object>> records = df.flatten(taxon);
		assertEquals("01", 2, records.size());
		assertTrue("02", records.get(0) == vernacularName0);
		assertTrue("03", records.get(1) == vernacularName1);
		assertTrue("04", records.get(0).get(DocumentFlattener.PARENT_DOCUMENT_FIELD_NAME) == taxon);
		assertTrue("05", records.get(1).get(DocumentFlattener.PARENT_DOCUMENT_FIELD_NAME) == taxon);

	}

}
