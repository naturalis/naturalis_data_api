package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

@SuppressWarnings("static-method")
public class PathReaderTest {

	@Test
	public void testReadValue_01() throws InvalidPathException
	{
		Specimen specimen = new Specimen();
		String unitID = "ZMA.MAM.123456";
		specimen.setUnitID(unitID);
		PathReader pr = new PathReader(new Path("unitID"));
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
		PathReader pr = new PathReader(path);
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
		PathReader pr = new PathReader(path);
		List<Object> value = pr.readValue(specimen);
		assertEquals("01", 2, value.size());
		assertEquals("02", name0, value.get(0));
		assertEquals("02", name1, value.get(1));
	}

}
