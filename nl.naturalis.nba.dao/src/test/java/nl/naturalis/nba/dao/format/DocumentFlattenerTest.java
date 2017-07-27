package nl.naturalis.nba.dao.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;

public class DocumentFlattenerTest {

	private Taxon taxon;
	private SourceSystem sourceSystem;
	private ScientificName acceptedName;
	private VernacularName vernacularName0;
	private VernacularName vernacularName1;
	private Reference reference0;
	private Reference reference1;
	private List<Reference> references;
	private List<VernacularName> vernacularNames;

	@Before
	public void init()
	{
		taxon = new Taxon();
		taxon.setSourceSystemId("1");

		sourceSystem = SourceSystem.NSR;
		taxon.setSourceSystem(sourceSystem);

		acceptedName = new ScientificName();
		acceptedName.setFullScientificName("Larus fuscus");
		taxon.setAcceptedName(acceptedName);

		vernacularName0 = new VernacularName();
		vernacularName0.setName("meeuw");
		vernacularName0.setLanguage("nl");

		vernacularName1 = new VernacularName();
		vernacularName1.setName("sea gull");
		vernacularName1.setLanguage("en");

		reference0 = new Reference();
		reference0.setTitleCitation("Vogels van Europa");
		reference0.setAuthor(new Person("Ruud Altenburg"));

		reference1 = new Reference();
		reference1.setTitleCitation("The Secret Life of Sea Gulls");
		reference1.setAuthor(new Person("John Smith"));

		references = Arrays.asList(reference0, reference1);
		vernacularName0.setReferences(references);
		vernacularName1.setReferences(references);

		vernacularNames = Arrays.asList(vernacularName0, vernacularName1);
		taxon.setVernacularNames(vernacularNames);

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
