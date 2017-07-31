package nl.naturalis.nba.dao.format.calc;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.dao.format.CalculatorInitializationException;

public class NamePublishedInCalculatorTest {

	private static NamePublishedInCalculator forAcceptedName;
	private static NamePublishedInCalculator forSynonym;

	@BeforeClass
	public static void init() throws CalculatorInitializationException
	{
		forAcceptedName = new NamePublishedInCalculator();
		Map<String, String> args = new HashMap<>();
		args.put("type", "accepted name");
		forAcceptedName.initialize(args);
		forSynonym = new NamePublishedInCalculator();
		args = new HashMap<>();
		args.put("type", "synonym");
		forSynonym.initialize(args);
	}

	@Test
	public void testCalculate_01_synonym()
	{
	}

	@Test
	public void testCalculate_01_acceptedName()
	{
	}

}
