package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Arrays.asList;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

@SuppressWarnings("static-method")
public class PathValueComparatorTest {

	private static SpecimenIdentification si_A;
	private static SpecimenIdentification si_B;
	private static SpecimenIdentification si_C;
	private static SpecimenIdentification si_D;
	private static SpecimenIdentification si_E;
	private static SpecimenIdentification si_F;
	private static SpecimenIdentification si_null;

	private static GatheringEvent ge_x;
	private static GatheringEvent ge_y;
	private static GatheringEvent ge_z;
	private static GatheringEvent ge_null;

	@BeforeClass
	public static void before()
	{
		// Full scientific name: A
		si_A = new SpecimenIdentification();
		ScientificName sn_A = new ScientificName();
		sn_A.setFullScientificName("A");
		si_A.setScientificName(sn_A);

		// Full scientific name: B
		si_B = new SpecimenIdentification();
		ScientificName sn_B = new ScientificName();
		sn_B.setFullScientificName("B");
		si_B.setScientificName(sn_B);

		// Full scientific name: C
		si_C = new SpecimenIdentification();
		ScientificName sn_C = new ScientificName();
		sn_C.setFullScientificName("C");
		si_C.setScientificName(sn_C);

		// Full scientific name: D
		si_D = new SpecimenIdentification();
		ScientificName sn_D = new ScientificName();
		sn_D.setFullScientificName("D");
		si_D.setScientificName(sn_D);

		// Full scientific name: E
		si_E = new SpecimenIdentification();
		ScientificName sn_E = new ScientificName();
		sn_E.setFullScientificName("E");
		si_E.setScientificName(sn_E);

		// Full scientific name: F
		si_F = new SpecimenIdentification();
		ScientificName sn_F = new ScientificName();
		sn_F.setFullScientificName("F");
		si_F.setScientificName(sn_F);

		// Full scientific name: null
		si_null = new SpecimenIdentification();
		ScientificName sn_null = new ScientificName();
		sn_null.setFullScientificName(null);
		si_null.setScientificName(sn_null);

		ge_x = new GatheringEvent();
		ge_x.setLocalityText("X");

		ge_y = new GatheringEvent();
		ge_y.setLocalityText("Y");

		ge_z = new GatheringEvent();
		ge_z.setLocalityText("Z");

		ge_null = new GatheringEvent();
		ge_null.setLocalityText(null);

	}

	@Test
	public void compare_01()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setUnitID("A");
		Specimen specimen2 = new Specimen();
		specimen2.setUnitID("B");
		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		int i = "A".compareTo("B");
		assertEquals("01", i, pvc.compare(specimen1, specimen2));
	}

	@Test
	public void compare_02()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setUnitID("A");
		Specimen specimen2 = new Specimen();
		specimen2.setUnitID("B");
		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);
		int i = "B".compareTo("A");
		assertEquals("01", i, pvc.compare(specimen1, specimen2));
	}

	@Test
	public void compare_03()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setIdentifications(asList(si_A));
		Specimen specimen2 = new Specimen();
		specimen2.setIdentifications(asList(si_B));
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		int i = pvc.compare(specimen1, specimen2);
		assertTrue("01", i < 0);
	}

	@Test
	public void compare_04()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setIdentifications(asList(si_B));
		Specimen specimen2 = new Specimen();
		specimen2.setIdentifications(asList(si_E));
		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);
		int i = pvc.compare(specimen1, specimen2);
		assertTrue("01", i > 0);
	}

	@Test
	public void compare_10()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setUnitID("X");
		Specimen specimen2 = new Specimen();
		specimen2.setUnitID("B");
		Specimen specimen3 = new Specimen();
		specimen3.setUnitID("K");
		Specimen specimen4 = new Specimen();
		specimen4.setUnitID("C");
		Specimen specimen5 = new Specimen();
		specimen5.setUnitID("A");
		Specimen specimen6 = new Specimen();
		specimen6.setUnitID(null);
		Specimen specimen7 = new Specimen();
		specimen7.setUnitID("Z");
		Specimen specimen8 = new Specimen();
		specimen8.setUnitID("Z");

		List<Specimen> list = asList(specimen1, specimen2, specimen3, specimen4, specimen5,
				specimen6, specimen7, specimen8);

		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, false);

		Collections.sort(list, pvc);
		assertTrue("01", list.get(0).getUnitID().equals("A"));
		assertTrue("02", list.get(1).getUnitID().equals("B"));
		assertTrue("03", list.get(2).getUnitID().equals("C"));
		assertTrue("04", list.get(3).getUnitID().equals("K"));
		assertTrue("05", list.get(4).getUnitID().equals("X"));
		assertTrue("06", list.get(5).getUnitID().equals("Z"));
		assertTrue("07", list.get(6).getUnitID().equals("Z"));
		assertTrue("08", list.get(7).getUnitID() == null);
		Collections.shuffle(list);
		Collections.sort(list, pvc);
		assertTrue("01a", list.get(0).getUnitID().equals("A"));
		assertTrue("02a", list.get(1).getUnitID().equals("B"));
		assertTrue("03a", list.get(2).getUnitID().equals("C"));
		assertTrue("04a", list.get(3).getUnitID().equals("K"));
		assertTrue("05a", list.get(4).getUnitID().equals("X"));
		assertTrue("06a", list.get(5).getUnitID().equals("Z"));
		assertTrue("07a", list.get(6).getUnitID().equals("Z"));
		assertTrue("08a", list.get(7).getUnitID() == null);
		Collections.shuffle(list);
		Collections.sort(list, pvc);
		assertTrue("01b", list.get(0).getUnitID().equals("A"));
		assertTrue("02b", list.get(1).getUnitID().equals("B"));
		assertTrue("03b", list.get(2).getUnitID().equals("C"));
		assertTrue("04b", list.get(3).getUnitID().equals("K"));
		assertTrue("05b", list.get(4).getUnitID().equals("X"));
		assertTrue("06b", list.get(5).getUnitID().equals("Z"));
		assertTrue("07b", list.get(6).getUnitID().equals("Z"));
		assertTrue("08b", list.get(7).getUnitID() == null);
	}

	@Test
	public void compare_11()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setUnitID("X");
		Specimen specimen2 = new Specimen();
		specimen2.setUnitID("B");
		Specimen specimen3 = new Specimen();
		specimen3.setUnitID("K");
		Specimen specimen4 = new Specimen();
		specimen4.setUnitID("C");
		Specimen specimen5 = new Specimen();
		specimen5.setUnitID("A");
		Specimen specimen6 = new Specimen();
		specimen6.setUnitID(null);
		Specimen specimen7 = new Specimen();
		specimen7.setUnitID("Z");
		Specimen specimen8 = new Specimen();
		specimen8.setUnitID("Z");

		List<Specimen> list = asList(specimen1, specimen2, specimen3, specimen4, specimen5,
				specimen6, specimen7, specimen8);

		Path path = new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);

		Collections.sort(list, pvc);

		assertTrue("01", list.get(0).getUnitID().equals("Z"));
		assertTrue("02", list.get(1).getUnitID().equals("Z"));
		assertTrue("03", list.get(2).getUnitID().equals("X"));
		assertTrue("04", list.get(3).getUnitID().equals("K"));
		assertTrue("05", list.get(4).getUnitID().equals("C"));
		assertTrue("06", list.get(5).getUnitID().equals("B"));
		assertTrue("07", list.get(6).getUnitID().equals("A"));
		assertTrue("08", list.get(7).getUnitID() == null);

		Collections.shuffle(list);
		Collections.sort(list, pvc);

		assertTrue("01a", list.get(0).getUnitID().equals("Z"));
		assertTrue("02a", list.get(1).getUnitID().equals("Z"));
		assertTrue("03a", list.get(2).getUnitID().equals("X"));
		assertTrue("04a", list.get(3).getUnitID().equals("K"));
		assertTrue("05a", list.get(4).getUnitID().equals("C"));
		assertTrue("06a", list.get(5).getUnitID().equals("B"));
		assertTrue("07a", list.get(6).getUnitID().equals("A"));
		assertTrue("08a", list.get(7).getUnitID() == null);

		Collections.shuffle(list);
		Collections.sort(list, pvc);

		assertTrue("01b", list.get(0).getUnitID().equals("Z"));
		assertTrue("02b", list.get(1).getUnitID().equals("Z"));
		assertTrue("03b", list.get(2).getUnitID().equals("X"));
		assertTrue("04b", list.get(3).getUnitID().equals("K"));
		assertTrue("05b", list.get(4).getUnitID().equals("C"));
		assertTrue("06b", list.get(5).getUnitID().equals("B"));
		assertTrue("07b", list.get(6).getUnitID().equals("A"));
		assertTrue("08b", list.get(7).getUnitID() == null);
	}

	@Test
	public void compare_12()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setIdentifications(asList(si_F));
		Specimen specimen2 = new Specimen();
		specimen2.setIdentifications(asList(si_A));
		Specimen specimen3 = new Specimen();
		specimen3.setIdentifications(asList(si_null));
		Specimen specimen4 = new Specimen();
		specimen4.setIdentifications(asList(si_C));
		Specimen specimen5 = new Specimen();
		specimen5.setIdentifications(asList(si_E));
		Specimen specimen6 = new Specimen();
		specimen6.setIdentifications(asList(si_D));
		Specimen specimen7 = new Specimen();
		specimen7.setIdentifications(asList(si_null));
		Specimen specimen8 = new Specimen();
		specimen8.setIdentifications(asList(si_A));

		List<Specimen> list = Arrays.asList(specimen1, specimen2, specimen3, specimen4, specimen5,
				specimen6, specimen7, specimen8);

		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, false);

		Collections.sort(list, pvc);
		assertTrue("01", list.get(0) == specimen2 || list.get(0) == specimen8); // A
		assertTrue("02", list.get(1) == specimen2 || list.get(1) == specimen8); // A
		assertTrue("03", list.get(0) != list.get(1));
		assertTrue("04", list.get(2) == specimen4); // C
		assertTrue("05", list.get(3) == specimen6); // D
		assertTrue("06", list.get(4) == specimen5); // E
		assertTrue("07", list.get(5) == specimen1); // F
		assertTrue("08", list.get(6) == specimen3 || list.get(6) == specimen7); // null
		assertTrue("09", list.get(7) == specimen3 || list.get(7) == specimen7); // null
		assertTrue("10", list.get(6) != list.get(7));

		Collections.shuffle(list);
		Collections.sort(list, pvc);

	}

	@Test
	public void compare_13()
	{
		Specimen specimen1 = new Specimen();
		specimen1.setIdentifications(asList(si_F));
		Specimen specimen2 = new Specimen();
		specimen2.setIdentifications(asList(si_A));
		Specimen specimen3 = new Specimen();
		specimen3.setIdentifications(asList(si_null));
		Specimen specimen4 = new Specimen();
		specimen4.setIdentifications(asList(si_C));
		Specimen specimen5 = new Specimen();
		specimen5.setIdentifications(asList(si_E));
		Specimen specimen6 = new Specimen();
		specimen6.setIdentifications(asList(si_D));
		Specimen specimen7 = new Specimen();
		specimen7.setIdentifications(asList(si_null));
		Specimen specimen8 = new Specimen();
		specimen8.setIdentifications(asList(si_A));

		List<Specimen> list = Arrays.asList(specimen1, specimen2, specimen3, specimen4, specimen5,
				specimen6, specimen7, specimen8);

		Path path = new Path("identifications.scientificName.fullScientificName");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path, true);

		Collections.sort(list, pvc);
		assertTrue("01", list.get(0) == specimen1); // F
		assertTrue("02", list.get(1) == specimen5); // E
		assertTrue("03", list.get(2) == specimen6); // D
		assertTrue("04", list.get(3) == specimen4); // C
		assertTrue("05", list.get(4) == specimen2 || list.get(4) == specimen8); // A
		assertTrue("06", list.get(5) == specimen2 || list.get(5) == specimen8); // A
		assertTrue("07", list.get(4) != list.get(5));
		assertTrue("08", list.get(6) == specimen3 || list.get(6) == specimen7); // null
		assertTrue("09", list.get(7) == specimen3 || list.get(7) == specimen7); // null
		assertTrue("10", list.get(6) != list.get(7));
		
		Collections.shuffle(list);
		Collections.sort(list, pvc);

		assertTrue("01", list.get(0) == specimen1); // F
		assertTrue("02", list.get(1) == specimen5); // E
		assertTrue("03", list.get(2) == specimen6); // D
		assertTrue("04", list.get(3) == specimen4); // C
		assertTrue("05", list.get(4) == specimen2 || list.get(4) == specimen8); // A
		assertTrue("06", list.get(5) == specimen2 || list.get(5) == specimen8); // A
		assertTrue("07", list.get(4) != list.get(5));
		assertTrue("08", list.get(6) == specimen3 || list.get(6) == specimen7); // null
		assertTrue("09", list.get(7) == specimen3 || list.get(7) == specimen7); // null
		assertTrue("10", list.get(6) != list.get(7));
	}

}
