package nl.naturalis.nba.dao.format.calc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FormatUtil;
import nl.naturalis.nba.dao.format.calc.NamePublishedInCalculator;

@SuppressWarnings("static-method")
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
	public void testCalculate_01_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertTrue("01", FormatUtil.EMPTY_STRING == value);
	}

	@Test
	public void testCalculate_02_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects", value);
	}

	@Test
	public void testCalculate_03_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("publicationDate", "2010-09-11");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "(2010/09/11)", value);
	}

	@Test
	public void testCalculate_04_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "(Stephen King)", value);
	}

	@Test
	public void testCalculate_05_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("publicationDate", "2010-09-11");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "(Stephen King, 2010/09/11)", value);
	}

	@Test
	public void testCalculate_06_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		reference.put("publicationDate", "2010-09-11");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (Stephen King, 2010/09/11)", value);
	}

	@Test
	public void testCalculate_07_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (Stephen King)", value);
	}


	@Test
	public void testCalculate_08_synonym() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> reference = new LinkedHashMap<>();
		List<?> references = Arrays.asList(reference);
		data.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		reference.put("publicationDate", "2010-09-11");
		EntityObject entity = new EntityObject(data);
		String value = forSynonym.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (2010/09/11)", value);
	}
	
	

	@Test
	public void testCalculate_01_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertTrue("01", FormatUtil.EMPTY_STRING == value);
	}

	@Test
	public void testCalculate_02_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects", value);
	}

	@Test
	public void testCalculate_03_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("publicationDate", "2010-09-11");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "(2010/09/11)", value);
	}

	@Test
	public void testCalculate_04_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "(Stephen King)", value);
	}

	@Test
	public void testCalculate_05_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("publicationDate", "2010-09-11");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "(Stephen King, 2010/09/11)", value);
	}

	@Test
	public void testCalculate_06_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		reference.put("publicationDate", "2010-09-11");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (Stephen King, 2010/09/11)", value);
	}

	@Test
	public void testCalculate_07_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		Map<String, Object> author = new LinkedHashMap<>();
		reference.put("author", author);
		author.put("fullName", "Stephen King");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (Stephen King)", value);
	}


	@Test
	public void testCalculate_08_acceptedName() throws CalculationException
	{
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> acceptedName = new LinkedHashMap<>();
		data.put("acceptedName", acceptedName);
		Map<String, Object> reference = new LinkedHashMap<>();	
		List<?> references = Arrays.asList(reference);
		acceptedName.put("references", references);
		reference.put("titleCitation", "Super Sized Insects");
		reference.put("publicationDate", "2010-09-11");
		EntityObject entity = new EntityObject(data);
		String value = forAcceptedName.calculateValue(entity).toString();
		assertEquals("01", "Super Sized Insects (2010/09/11)", value);
	}
}
