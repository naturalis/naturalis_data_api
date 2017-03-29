package nl.naturalis.nba.common;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

@SuppressWarnings("static-method")
public class PathValueReaderTest {

	@Test
	public void testReadValue_01() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		String unitID = "ZMA.MAM.123456";
		specimen.setUnitID(unitID);
		PathValueReader pr = new PathValueReader(new Path("unitID"));
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 1, value.size());
		assertEquals("02", unitID, value.get(0));
	}

	@Test
	public void testReadValue_02() throws InvalidPathException
	{
		String name = "Larus fuscus fuscus";
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(name);
		SpecimenIdentification si = new SpecimenIdentification();
		si.setScientificName(sn);
		Specimen specimen = new Specimen();
		specimen.addIndentification(si);
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 1, value.size());
		assertEquals("02", name, value.get(0));
	}

	@Test
	public void testReadValue_03() throws InvalidPathException
	{
		Specimen specimen = new Specimen();

		String name0 = "Larus fuscus fuscus";
		ScientificName sn = new ScientificName();
		sn.setFullScientificName(name0);
		SpecimenIdentification si = new SpecimenIdentification();
		si.setScientificName(sn);
		specimen.addIndentification(si);

		String name1 = "Parus major";
		sn = new ScientificName();
		sn.setFullScientificName(name1);
		si = new SpecimenIdentification();
		si.setScientificName(sn);
		specimen.addIndentification(si);			
		
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 2, value.size());
		assertEquals("02", name0, value.get(0));
		assertEquals("02", name1, value.get(1));
	}

	@Test
	public void testReadValue_04() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		specimen.setUnitID(null);
		PathValueReader pr = new PathValueReader(new Path("unitID"));
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 1, value.size());
		assertNull("02",value.get(0));
	}

	@Test
	public void testReadValue_05() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 0, value.size());
	}

	@Test
	public void testReadValue_06() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		SpecimenIdentification si = new SpecimenIdentification();
		specimen.addIndentification(si);			
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 0, value.size());
	}

	@Test
	public void testReadValue_07() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		SpecimenIdentification si = new SpecimenIdentification();
		specimen.addIndentification(si);			
		ScientificName sn = new ScientificName();
		si.setScientificName(sn);
		sn.setFullScientificName(null);
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueReader pr = new PathValueReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 1, value.size());
		assertNull("02",value.get(0));
	}

}
