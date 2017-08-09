package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.VernacularName;

@SuppressWarnings("static-method")
public class PathValueReaderTest {

	/*
	 * Happy flow.
	 */
	@Test
	public void testRead_01() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		String unitID = "ZMA.MAM.123456";
		specimen.setUnitID(unitID);
		PathValueReader pr = new PathValueReader(new Path("unitID"));
		Object value = pr.read(specimen);
		assertEquals("01", unitID, value);
	}

	/*
	 * Test with somewhat more complicated path.
	 */
	@Test
	public void testRead_02() throws InvalidPathException
	{
		String name = "Larus fuscus fuscus";
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(name);
		SpecimenIdentification si = new SpecimenIdentification();
		si.setScientificName(sn);
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path("identifications.0.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		Object value = pr.read(specimen);
		assertEquals("01", name, value);
	}

	/*
	 * Test with very long path.
	 */
	@Test
	public void testRead_03() throws InvalidPathException
	{
		Organization organization = new Organization("Naturalis");
		Person author = new Person("John Smith");
		author.setOrganization(organization);
		Reference reference = new Reference();
		reference.setAuthor(author);
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path(
				"identifications.0.vernacularNames.0.references.0.author.organization.name");
		PathValueReader pr = new PathValueReader(path);
		Object value = pr.read(specimen);
		assertEquals("01", "Naturalis", value);
	}

	/*
	 * Test with invalid path (missing array index after vernacularNames)
	 */
	@Test
	public void testRead_04() throws InvalidPathException
	{
		Organization organization = new Organization("Naturalis");
		Person author = new Person("John Smith");
		author.setOrganization(organization);
		Reference reference = new Reference();
		reference.setAuthor(author);
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path(
				"identifications.0.vernacularNames.references.0.author.organization.name");
		PathValueReader pr = new PathValueReader(path);
		try {
			pr.read(specimen);
			fail("should not be here");
		}
		catch (RuntimeException e) {
			assertTrue("01", e.getMessage().startsWith("Missing array index after"));
		}
	}

	/*
	 * Test that if any element in the path is null, null is returned.
	 */
	@Test
	public void testRead_05() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		Path path = new Path(
				"identifications.0.vernacularNames.0.references.0.author.organization.name");
		PathValueReader pr = new PathValueReader(path);
		assertNull("01", pr.read(specimen));
	}

	/*
	 * Test that if any element in the path is null, null is returned.
	 */
	@Test
	public void testRead_06() throws InvalidPathException
	{
		Reference reference = new Reference();
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path(
				"identifications.0.vernacularNames.0.references.0.author.organization.name");
		PathValueReader pr = new PathValueReader(path);
		assertNull("01", pr.read(specimen));
	}

	/*
	 * Test with incomplete path.
	 */
	@Test
	public void testRead_07() throws InvalidPathException
	{
		Organization organization = new Organization("Naturalis");
		Person author = new Person("John Smith");
		author.setOrganization(organization);
		Reference reference = new Reference();
		reference.setAuthor(author);
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path(
				"identifications.0.vernacularNames.0.references.0.author.organization");
		PathValueReader pr = new PathValueReader(path);
		Object value = pr.read(specimen);
		assertTrue("01", value instanceof Organization);
	}

	/*
	 * Test with incomplete path.
	 */
	@Test
	public void testRead_08() throws InvalidPathException
	{
		Organization organization = new Organization("Naturalis");
		Person author = new Person("John Smith");
		author.setOrganization(organization);
		Reference reference = new Reference();
		reference.setAuthor(author);
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path("identifications.0.vernacularNames.0.references.0");
		PathValueReader pr = new PathValueReader(path);
		Object value = pr.read(specimen);
		assertTrue("01", value instanceof Reference);
	}

	/*
	 * Test with incomplete path.
	 */
	@Test
	public void testRead_09() throws InvalidPathException
	{
		Organization organization = new Organization("Naturalis");
		Person author = new Person("John Smith");
		author.setOrganization(organization);
		Reference reference = new Reference();
		reference.setAuthor(author);
		VernacularName vernacularName = new VernacularName("cat");
		vernacularName.setReferences(Arrays.asList(reference));
		SpecimenIdentification si = new SpecimenIdentification();
		si.setVernacularNames(Arrays.asList(vernacularName));
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path("identifications.0.vernacularNames.0.references");
		PathValueReader pr = new PathValueReader(path);
		Object value = pr.read(specimen);
		assertTrue("01", value instanceof List);
	}
}
