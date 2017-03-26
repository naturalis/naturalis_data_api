package nl.naturalis.nba.common;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.Specimen;


@SuppressWarnings("static-method")
public class PathValueComparatorTest {
	
	private static final Specimen specimen1 = new Specimen();
	private static final Specimen specimen2 = new Specimen();
	
	@BeforeClass
	public static void beforeClass() {
		specimen1.setUnitID("A");
		specimen2.setUnitID("B");		
	}

	@Test
	public void compare_01()
	{
		Path path=new Path("unitID");
		PathValueComparator<Specimen> pvc = new PathValueComparator<>(path);
		int i = "A".compareTo("B");
		assertEquals("01", i,pvc.compare(specimen1, specimen2));
	}

}
