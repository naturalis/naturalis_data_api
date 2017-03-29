package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

public class PathValueComparatorTest {

	private Specimen specimen1;
	private Specimen specimen2;

	@Before
	public void before()
	{
		specimen1 = new Specimen();
		specimen2 = new Specimen();

		specimen1.setUnitID("A");
		specimen2.setUnitID("B");

		SpecimenIdentification si1 = new SpecimenIdentification();
		ScientificName sn1 = new ScientificName();
		sn1.setFullScientificName("A");
		si1.setScientificName(sn1);

		SpecimenIdentification si2 = new SpecimenIdentification();
		ScientificName sn2 = new ScientificName();
		sn2.setFullScientificName("B");
		si2.setScientificName(sn2);

		SpecimenIdentification si3 = new SpecimenIdentification();
		ScientificName sn3 = new ScientificName();
		sn3.setFullScientificName("C");
		si3.setScientificName(sn3);

		SpecimenIdentification si4 = new SpecimenIdentification();
		ScientificName sn4 = new ScientificName();
		sn4.setFullScientificName("D");
		si4.setScientificName(sn4);

		SpecimenIdentification si5 = new SpecimenIdentification();
		ScientificName sn5 = new ScientificName();
		sn5.setFullScientificName(null);
		si5.setScientificName(sn5);

		specimen1.addIndentification(si1);
		specimen1.addIndentification(si2);

		specimen2.addIndentification(si2);
		specimen2.addIndentification(si3);
		specimen2.addIndentification(si4);
		specimen2.addIndentification(si5);

	}

	@Test
	public void compare_01()
	{
		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		int i = "A".compareTo("B");
		assertEquals("01", i, pvc.compare(specimen1, specimen2));
	}

	@Test
	public void compare_02()
	{
		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);
		int i = "B".compareTo("A");
		assertEquals("01", i, pvc.compare(specimen1, specimen2));
	}

	@Test
	public void compare_03()
	{
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		int i = pvc.compare(specimen1, specimen2);
		assertTrue("01", i < 0);
	}

	@Test
	public void compare_04()
	{
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);
		int i = pvc.compare(specimen1, specimen2);
		assertTrue("01", i > 0);
	}

	@Test
	public void compare_10()
	{
		Specimen specimen3 = null;
		Specimen specimen4 = new Specimen();
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		List<Specimen> list = Arrays.asList(specimen1, specimen2, specimen3, specimen4);
		Collections.sort(list, pvc);
		/*
		 * 3 should come last because it is null; 4 should come last but one
		 * because it has no value for
		 * identifications.scientificName.fullScientificName
		 */
		assertTrue("01", list.get(0) == specimen1);
		assertTrue("02", list.get(1) == specimen2);
		assertTrue("03", list.get(2) == specimen4);
		assertTrue("04", list.get(3) == specimen3);
		Collections.shuffle(list);
		Collections.sort(list, pvc);
		assertTrue("05", list.get(0) == specimen1);
		assertTrue("06", list.get(1) == specimen2);
		assertTrue("07", list.get(2) == specimen4);
		assertTrue("08", list.get(3) == specimen3);
	}

	@Test
	public void compare_11()
	{
		Specimen specimen3 = null;
		Specimen specimen4 = new Specimen();
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);
		List<Specimen> list = Arrays.asList(specimen1, specimen2, specimen3, specimen4);
		Collections.sort(list, pvc);
		assertTrue("01", list.get(0) == specimen2);
		assertTrue("02", list.get(1) == specimen1);
		assertTrue("03", list.get(2) == specimen4);
		assertTrue("04", list.get(3) == specimen3);
		Collections.shuffle(list);
		Collections.sort(list, pvc);
		assertTrue("05", list.get(0) == specimen2);
		assertTrue("06", list.get(1) == specimen1);
		assertTrue("07", list.get(2) == specimen4);
		assertTrue("08", list.get(3) == specimen3);
	}

}
