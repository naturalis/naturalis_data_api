package nl.naturalis.nba.utils;

import org.hamcrest.collection.IsEmptyCollection;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import nl.naturalis.nba.utils.ConfigObject.MissingPropertyException;




public class ConfigObjectTest {

	static final String resource = "testbuild.v2.properties";
	
	private String getFileWithUtil(String fileName) {

		String result = "";

		ClassLoader classLoader = getClass().getClassLoader();
		try {
		    result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	  }

	
	@Test
	public void testForResource()
	{
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		assertNotNull("01", prop.getProperty("elasticsearch.cluster.name"));
		assertNotNull("02", prop.getProperty("elasticsearch.transportaddress.host"));
		assertNotNull("03", prop.getProperty("elasticsearch.transportaddress.port"));
	}

	@Test
	public void testIsTrueValueString()
	{
		List<String> actual =  Arrays.asList("true", "1", "yes", "on", "ok");
        List<String> expected = Arrays.asList("true", "1", "yes", "on", "ok");
        
		//All passed / true

        //1. Test equal.
        assertThat(actual, is(expected));

        //2. If List has this value?
        assertThat(actual, hasItems("yes"));

        //3. Check List Size
        assertThat(actual, hasSize(5));

        assertThat(actual.size(), is(5));

        //4.  List order

        // Ensure Correct order
        assertThat(actual, contains("true", "1", "yes", "on", "ok"));

        // Can be any order
        assertThat(actual, containsInAnyOrder("ok", "on", "yes", "1", "true"));

        //5. check empty list
        assertThat(actual, not(IsEmptyCollection.empty()));

        assertThat(new ArrayList<>(), IsEmptyCollection.empty());

	}
	
	private enum TRUE_VALUES
	{ 
		TRUE, ONE, YES, ON, OK
	}
	
	private static final String[] TRUE_VALUESi = new String[] { "true", "1", "yes", "on", "ok" };
	

	@Test
	public void testIsTrueValueStringBoolean()
	{
		Set<String> expected = new HashSet<> (Arrays.asList("TRUE", "ONE", "YES", "ON", "OK" ));
		Set<String> actual = new HashSet<>();
		for (TRUE_VALUES e : TRUE_VALUES.values())
		  actual.add(e.name());
		assertEquals("is gelijk", expected, actual);
		//assertTrue(expected.contains("1"));
		assertTrue(expected.contains("OK"));
		boolean b = String.valueOf(TRUE_VALUESi) != null;
		assertTrue(ConfigObject.isTrueValue(String.valueOf("true"), b));
		assertTrue(ConfigObject.isTrueValue(String.valueOf("1"), b));
		assertTrue(ConfigObject.isTrueValue(String.valueOf("yes"), b));
		assertTrue(ConfigObject.isTrueValue(String.valueOf("on"), b));
		assertTrue(ConfigObject.isTrueValue(String.valueOf("ok"), b));
	}

	@Test
	public void testIsEnabledString()
	{
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		assertTrue(ConfigObject.isEnabled(prop, prop.getProperty("elasticsearch.cluster.name")));
	}

	@Test(expected=MissingPropertyException.class)
	public void testIsEnabledPropertiesString()
	{
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		assertTrue(ConfigObject.isEnabled(prop, prop.getProperty("test.string.property.2")));
		
	}

	@Test
	public void testIsEnabledStringBoolean()
	{
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		assertTrue(ConfigObject.isEnabled(prop, prop.getProperty("test.boolean.property.1")));
	}

	@Test(expected=MissingPropertyException.class)
	public void testIsEnabledPropertiesStringBoolean()
	{
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		assertTrue(ConfigObject.isEnabled(prop, prop.getProperty("test.boolean.property.3")));
	}


	
	@Test
	public void testConfigObjectFile()
	{
		Properties prop = new Properties();
		ConfigObject config;
		File cfgFile;
		
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
		final String PATH = prop.getProperty("nl.naturalis.nba.test.config.path");
		File cfgDir = new File(PATH);
		
		cfgFile = FileUtil.newFile(cfgDir, resource);
		if (!cfgFile.isFile()) {
			String msg = String.format("Missing configuration file: %s", cfgFile.getPath());
			throw new RuntimeException(msg);
		}
		config = new ConfigObject(cfgFile);
		assertNotNull(config);
	}

	@Test
	public void testConfigObjectInputStream()
	{
		ConfigObject config;
		InputStream input;
		input = ConfigObjectTest.class.getResourceAsStream(resource);
		config = new ConfigObject(input);
		assertNotNull(config);
	}

	@Test
	public void testConfigObjectString()
	{
		ConfigObject config;
		final String path =  ConfigObjectTest.class.getResource(resource).getPath();
		config = new ConfigObject(path);
		assertNotNull(config);
	}

	@Test
	public void testConfigObjectProperties()
	{
		Properties prop = new Properties();
		ConfigObject config = new ConfigObject(prop);
		assertNotNull(config);
	}

	@Test
	public void testHasProperty()
	{
		ConfigObject config = null;
		InputStream input;
		input = ConfigObjectTest.class.getResourceAsStream(resource);
		config = new ConfigObject(input);
		assertFalse(config.hasProperty("test.boolean.property.4"));
		assertTrue(config.hasProperty("test.boolean.property.3"));
	}

	@Test
	public void testGetPropertyNames()
	{
		ConfigObject config = null;
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		config = new ConfigObject(prop);
		List<String> actual = config.getPropertyNames();
		
		assertThat(actual, not(IsEmptyCollection.empty()));
	}

	@Test
	public void testGetProperties()
	{
		ConfigObject config = null;
		Properties prop = new Properties();
		InputStream input;
		try {
			input = ConfigObjectTest.class.getResourceAsStream(resource);
			prop.load(input);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		config = new ConfigObject(prop);
		List<String> actual = new ArrayList<>(); 
		actual.add(config.getProperties().toString());
		assertThat(actual, not(IsEmptyCollection.empty()));
	}

	@Test
	public void testGetString()
	{
		String descriptionNotPassed = "Property does not exists";
		String descriptionPassed = "Property does exists";
		ConfigObject config = null;
		InputStream input;
		input = ConfigObjectTest.class.getResourceAsStream(resource);
		config = new ConfigObject(input);
		try
		{
			assertNull(descriptionNotPassed,  config.get("test.boolean.property.4"));
			assertNotNull(descriptionPassed, config.get("test.boolean.property.3"));
			System.out.println(descriptionPassed + " - passed");
	     }catch(AssertionError e){
	          System.out.println(descriptionNotPassed + " - failed");
	        throw e;
	     }
	}


}
