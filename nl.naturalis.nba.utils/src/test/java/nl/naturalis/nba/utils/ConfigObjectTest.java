package nl.naturalis.nba.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import nl.naturalis.nba.utils.ConfigObject.InvalidValueException;
import nl.naturalis.nba.utils.ConfigObject.MissingPropertyException;
import nl.naturalis.nba.utils.ConfigObject.PropertyNotSetException;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import static org.hamcrest.MatcherAssert.assertThat;
/*import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;*/

/**
 * Test class for ConfigObject.java
 */
@SuppressWarnings({"static-method","cast"})
public class ConfigObjectTest {

    ConfigObject configObject;
    Properties properties;
    URL url;

    @Before
    public void setUp() {

        properties = new Properties();
        properties.setProperty("prop_1", "1");
        properties.setProperty("prop_2", "1");
        properties.setProperty("prop_3", "2");
        properties.setProperty("prop_4", "0");
        configObject = new ConfigObject(properties);
    }

    @After
    public void tearDown() {
        configObject = null;
        properties = null;
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isTrueValue(java.lang.String)}. Check
     * for "true", "1", "yes", "on", "ok"
     * 
     * Test to check {@code isTrueValue(value, false)}.
     */
    @Test
    public void testIsTrueValueString() {

        assertTrue(ConfigObject.isTrueValue("true"));
        assertTrue(ConfigObject.isTrueValue("1"));
        assertTrue(ConfigObject.isTrueValue("yes"));
        assertTrue(ConfigObject.isTrueValue("on"));
        assertTrue(ConfigObject.isTrueValue("ok"));
        assertFalse(ConfigObject.isTrueValue("OK"));
        assertFalse(ConfigObject.isTrueValue("TRUE"));
        assertFalse(ConfigObject.isTrueValue("testvalue"));
        assertFalse(ConfigObject.isTrueValue(null));

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ConfigObject#isTrueValue(java.lang.String, boolean)}. Check for
     * "true", "1", "yes", "on", "ok"
     * 
     * Test to check whether the specified value is a true-ish string, with a default value specified.
     */
    @Test
    public void testIsTrueValueStringBoolean() {

        assertTrue(ConfigObject.isTrueValue("true", true));
        assertTrue(ConfigObject.isTrueValue("1", true));
        assertTrue(ConfigObject.isTrueValue("yes", false));
        assertTrue(ConfigObject.isTrueValue("on", false));
        assertTrue(ConfigObject.isTrueValue("ok", true));
        assertTrue(ConfigObject.isTrueValue("", true));
        assertFalse(ConfigObject.isTrueValue(null, false));
        assertFalse(ConfigObject.isTrueValue("testvalue", true));
        assertFalse(ConfigObject.isTrueValue("TRUE", true));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isEnabled(java.lang.String)}.
     * 
     * Test to check if the specified property in the specified {@code Properties} object has a true-ish
     * value.
     */
    @Test
    public void testIsEnabledString() {

        Properties config = mock(Properties.class);
        when(config.getProperty("testProperty")).thenReturn("true");
        assertTrue(ConfigObject.isEnabled(config, "testProperty"));

        when(config.getProperty("testProperty")).thenReturn("");
        assertTrue(ConfigObject.isEnabled(config, "testProperty"));

        when(config.getProperty("testProperty")).thenReturn("false");
        assertFalse(ConfigObject.isEnabled(config, "testProperty"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isEnabled(java.lang.String, boolean)}.
     * 
     * Test to check if the specified property in the specified {@code Properties} object has a true-ish
     * value with addition additional default boolean parameter which is returned property does not
     * exist or is an empty string
     */
    @Test
    public void testIsEnabledStringBoolean_01() {

        Properties config = mock(Properties.class);
        String test_prop = "testProperty";

        when(config.getProperty(test_prop)).thenReturn("true");
        assertTrue(ConfigObject.isEnabled(config, test_prop, true));

        when(config.getProperty(test_prop)).thenReturn("false");
        assertFalse(ConfigObject.isEnabled(config, test_prop, false));


        when(config.getProperty(test_prop)).thenReturn("false");
        assertTrue(ConfigObject.isEnabled(config, null, true));

        when(config.getProperty(test_prop)).thenReturn("false");
        assertFalse(ConfigObject.isEnabled(config, null, false));

        when(config.getProperty(test_prop)).thenReturn("false");
        assertFalse(ConfigObject.isEnabled(config, test_prop, true));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isEnabled(java.lang.String, boolean)}.
     * expected = NullPointerException when Properties object is null;
     * 
     * 
     */
    @Test(expected = NullPointerException.class)
    public void testIsEnabledStringBoolean_02() {
        ConfigObject.isEnabled(null, "prop_1", true);
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#hasProperty(java.lang.String)}.
     * 
     * Test to verify whether or not there is property with the specified name.
     */
    @Test
    public void testHasProperty() {

        assertTrue(configObject.hasProperty("prop_1"));
        assertFalse(configObject.hasProperty("randomProperty"));
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getPropertyNames()}.
     */
    @Test
    @Ignore
    public void testGetPropertyNames() {

        List<String> expectedList = Arrays.asList("prop_1", "prop_2", "prop_3", "prop_4");
        List<String> actual = configObject.getPropertyNames();

        assertEquals(4, actual.size());
        //assertEquals(expectedList, containsInAnyOrder(actual) );
        assertThat(actual, containsInAnyOrder(expectedList.toArray(new String[expectedList.size()])));
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getProperties()}.
     * 
     * Test to check if getProperties retures the properties object
     */
    @Test
    public void testGetProperties() {

        Properties prop = configObject.getProperties();
        assertNotNull(prop);
        assertEquals("1", prop.get("prop_1"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#get(java.lang.String)}.
     * 
     * Test to get a value based on a property key,
     */
    @Test
    public void testGetString() {

        String actual = configObject.get("prop_1");
        assertNotNull(actual);
        assertEquals("1", actual);
        assertNull(configObject.get("random"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getInt(java.lang.String)}.
     * 
     * Test to check the the integer value returned based on property value supplied.
     */
    @Test
    public void testGetInt_01() {

        assertNotNull(configObject.getInt("prop_3"));
        assertEquals(2, configObject.getInt("prop_3"));
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getInt(java.lang.String)}.
     * 
     * Test to check if the methoed throws an InvalidValueException if the property doesnt exits.
     */
    @Test(expected = InvalidValueException.class)
    public void testGetInt_02() {
        configObject.getInt("random_property");
    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ConfigObject#get(java.lang.String, java.lang.String)}.
     * 
     * Test to get the value of the specified property
     * 
     */
    @Test
    public void testGetStringString() {

        properties.setProperty("prop_5", "");
        assertNotNull(configObject.get("prop_1", null, true));
        assertEquals("1", configObject.get("prop_1", null, true));
        assertEquals("1", configObject.get("prop_1", null, false));
        assertEquals("defaultValue", configObject.get("prop_5", "defaultValue", true));
        assertEquals("", configObject.get("prop_5", "", true));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isTrue(java.lang.String)}.
     * 
     * Test the value of the specified boolean property
     */
    @Test
    public void testIsTrue() {

        assertTrue(configObject.isTrue("prop_1"));
        assertTrue(configObject.isTrue("prop_2"));
        assertFalse(configObject.isTrue("prop_3"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#isTrue(java.lang.String, boolean)}.
     * 
     * Test to check if the property exist if not returns a default value
     */
    @Test
    public void testIsTrueStringDefault() {

        assertTrue(configObject.isTrue("prop", true));
        assertTrue(configObject.isTrue("prop_1", false));
        assertFalse(configObject.isTrue("prop", false));
        assertFalse(configObject.isTrue("prop_3", false));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String)}.
     * 
     * Test to get the value of the specified property.
     */
    @Test
    public void testRequiredString_01() {

        assertNotNull(configObject.required("prop_1"));
        assertEquals("1", configObject.required("prop_1"));
        assertEquals("2", configObject.required("prop_3"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String)}. throws
     * PropertyNotSetException
     * 
     * Test to check if property does not exist a {@link PropertyNotSetException} is thrown.
     */
    @Test(expected = PropertyNotSetException.class)
    public void testRequiredString_02() {
        properties.setProperty("prop_6", " ");
        configObject.required("prop_6");

    }



    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String)}. throws
     * MissingPropertyException
     * 
     * Test to check if property does not exist a {@link MissingPropertyException} is thrown.
     */
    @Test(expected = ConfigObject.MissingPropertyException.class)
    public void testRequiredString_03() {
        configObject.required("prop_7");

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String, java.lang.Class)}.
     * 
     * Test to get the value of the specified property, cast to the specified type.
     */
    @Test
    public void testRequiredStringClassOfT_01() {

        Integer valInteger = new Integer(1);
        String valString = "1";
        Double valDouble = 1.0;
        Short valueShort = 1;
        Byte valueByte = Byte.valueOf("1");
        Character valueChar = '1';

        assertTrue(configObject.required("prop_1", Integer.class) instanceof Integer);
        assertTrue(configObject.required("prop_1", String.class) instanceof String);
        assertTrue(configObject.required("prop_1", Double.class) instanceof Double);
        assertTrue(configObject.required("prop_1", Short.class) instanceof Short);
        assertTrue(configObject.required("prop_1", Byte.class) instanceof Byte);
        assertTrue(configObject.required("prop_1", Character.class) instanceof Character);
        assertTrue(configObject.required("prop_1", Boolean.class) instanceof Boolean);

        assertEquals(valInteger, configObject.required("prop_1", Integer.class));
        assertEquals(valString, configObject.required("prop_1", String.class));
        assertEquals(valDouble, configObject.required("prop_1", Double.class));
        assertEquals(valueShort, configObject.required("prop_1", Short.class));
        assertEquals(valueByte, configObject.required("prop_1", Byte.class));
        assertEquals(valueChar, configObject.required("prop_1", Character.class));
        assertTrue(configObject.required("prop_1", Boolean.class));
        assertFalse(configObject.required("prop_3", Boolean.class));
    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String, java.lang.Class)}. expected
     * = PropertyNotSetException
     * 
     * Test to check property doesn't not exist a {@link PropertyNotSetException} is thrown.
     */
    @Test(expected = PropertyNotSetException.class)
    public void testRequiredStringClassOfT_02() {
        properties.setProperty("prop_6", " ");
        configObject.required("prop_6", Integer.class);
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ConfigObject#required(java.lang.String, java.lang.Class)}. expected
     * = ConfigObject.MissingPropertyException
     * 
     * Test to check if property does not exist a {@link MissingPropertyException} is thrown.
     */
    @Test(expected = ConfigObject.MissingPropertyException.class)
    public void testRequiredStringClassOfT_03() {
        configObject.required("prop_7", Integer.class);
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getFile(java.lang.String)}.
     * 
     * Test to get a {@code File} instance corresponding to the specified property
     */
    @Test
    public void testGetFile_01() {

        URL url = getClass().getResource("TestFile.txt");
        String filePath = url.getPath().toString();
        properties.setProperty("prop_8", filePath);

        File file = configObject.getFile("prop_8");

        assertTrue(file instanceof File);
        assertEquals("TestFile.txt", file.getName());
        assertEquals(filePath, file.getAbsolutePath());

    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getFile(java.lang.String)}. expected =
     * PropertyNotSetException
     * 
     * Test to check if property doesn't not exist a {@link PropertyNotSetException} is thrown.
     */
    @Test(expected = PropertyNotSetException.class)
    public void testGetFile_02() {

        properties.setProperty("prop_8", "");
        configObject.getFile("prop_8");
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getFile(java.lang.String)}. expected =
     * ConfigObject.MissingPropertyException
     */
    @Test(expected = ConfigObject.MissingPropertyException.class)
    public void testGetFile_03() {
        configObject.getFile("prop_8");
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getDirectory(java.lang.String)}.
     */
    @Test
    public void testGetDirectory_01() {


        URL url = getClass().getResource("TestFile.txt");
        String filePath = url.getPath().toString();
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        properties.setProperty("prop_8", dirPath);

        File file = configObject.getDirectory("prop_8");

        assertTrue(file instanceof File);
        assertTrue(file.isDirectory());

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getDirectory(java.lang.String)}.
     * expected = InvalidValueException
     */
    @Test(expected = ConfigObject.InvalidValueException.class)
    public void testGetDirectory_02() {

        URL url = getClass().getResource("TestFile.txt");
        String filePath = url.getPath().toString();
        properties.setProperty("prop_8", filePath);
        configObject.getDirectory("prop_8");

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getDirectory(java.lang.String)}.
     * expected = PropertyNotSetException
     * 
     * Test to check if property doesn't not exist a {@link PropertyNotSetException} is thrown.
     */
    @Test(expected = ConfigObject.PropertyNotSetException.class)
    public void testGetDirectory_03() {

        properties.setProperty("prop_8", "");
        configObject.getDirectory("prop_8");

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getDirectory(java.lang.String)}.
     * expected = MissingPropertyException
     * 
     * Test to check if property doesn't not exist a {@link MissingPropertyException} is thrown.
     */
    @Test(expected = ConfigObject.MissingPropertyException.class)
    public void testGetDirectory_04() {

        configObject.getDirectory("prop_8");

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getPath(java.lang.String)}.
     * 
     * Test to get the {@code Path} instance corresponding to the specified property.
     */
    @Test
    public void testGetPath_01() {

        URL url = getClass().getResource("TestFile.txt");
        String filePath = url.getPath().toString();
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        properties.setProperty("prop_8", dirPath);
        Path path = configObject.getPath("prop_8");

        assertTrue(path instanceof Path);
        assertTrue(path.isAbsolute());
        assertEquals(dirPath, path.toString());
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getPath(java.lang.String)}. throws =
     * InvalidValueException
     * 
     * Test to check if the correct {@ Path} value is not correct then a {@link PropertyNotSetException}
     * is thrown.
     */
    @Test(expected = ConfigObject.InvalidValueException.class)
    public void testGetPath_02() {

        properties.setProperty("prop_8", "Random_Path");
        configObject.getPath("prop_8");
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getPath(java.lang.String)}. throws =
     * PropertyNotSetException
     * 
     * Test to check if property doesn't not exist a {@link PropertyNotSetException} is thrown.
     */
    @Test(expected = ConfigObject.PropertyNotSetException.class)
    public void testGetPath_03() {

        properties.setProperty("prop_8", "");
        configObject.getPath("prop_8");
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getPath(java.lang.String)}. throws =
     * MissingPropertyException
     * 
     * Test to check if property doesn't not exist a {@link MissingPropertyException} is thrown.
     */
    @Test(expected = ConfigObject.MissingPropertyException.class)
    public void testGetPath_04() {

        configObject.getPath("prop_8");
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getArray(java.lang.String)}.
     * 
     * Test to check to get an Array of Strings from a commma(,) separated string value
     */
    @Test
    public void testGetArrayString() {

        properties.setProperty("prop_8", "This,is,an,array,split,test");
        String[] expected = {"This", "is", "an", "array", "split", "test"};
        String[] actual = configObject.getArray("prop_8");

        assertNotNull(actual);
        assertArrayEquals(expected, actual);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getArray(java.lang.String, char)}.
     * 
     * Test to check to get an Array of Strings from a specified string separated by a separator
     * separated value
     */
    @Test
    public void testGetArrayStringWishSpecialSeperator() {

        properties.setProperty("prop_8", "This:is:an:array:split:test");
        String[] expected = {"This", "is", "an", "array", "split", "test"};
        String[] actual = configObject.requiredArray("prop_8", ':');

        assertNotNull(actual);
        assertArrayEquals(expected, actual);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getSection(java.lang.String)}.
     * 
     * Test to check if getSection() returns a new {@code ConfigObject} instance containing only those
     * properties whose name start with the specified prefix followed by a dot.
     * 
     */
    @Test
    public void testGetSection() {

        properties.setProperty("prop.test1", "1");
        properties.setProperty("prop.test2", "2");
        properties.setProperty("prop_test3", "3");
        properties.setProperty("prop_test4", "4");
        properties.setProperty("prop.test5", "5");

        ConfigObject actual = configObject.getSection("prop");
        assertNotNull(actual);
        assertEquals(3, actual.getProperties().size());
        assertEquals("2", actual.get("test2"));
        assertNull(configObject.getSection(""));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#hasSection(java.lang.String)}.
     * 
     * Test to check if there is at least one property starting with the specified prefix followed by a
     * dot
     */
    @Test
    public void testHasSection() {

        properties.setProperty("prop.test1", "1");
        properties.setProperty("prop.test2", "2");
        assertTrue(configObject.hasSection("prop"));
        assertFalse(configObject.hasSection("test"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getSubsections()}.
     * 
     * Test to get all the perfix subsections of a property name.
     */
    @Test
    @Ignore
    public void testGetSubsections() {

        properties.clear();
        properties.setProperty("prefix1.test", "1");
        properties.setProperty("prefix2.test", "2");
        properties.setProperty("prefix3.test", "3");
        properties.setProperty("prefix4.test", "4");

        //String[] expected = {"prefix4", "prefix3", "prefix2", "prefix1"};
        //String[] actual = configObject.getSubsections();
        
        List<String> expected = Arrays.asList("prefix4", "prefix2", "prefix3", "prefix1");
        List<String> actual = Arrays.asList(configObject.getSubsections());
        assertNotNull(actual);
        assertThat(actual, containsInAnyOrder(expected.toArray(new String[expected.size()])));
        //assertArrayEquals(expected, actual);
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getSubsections(java.lang.String)}.
     * 
     * Test to get the subsection of the specified parent section.
     */
    @Test
    @Ignore
    public void testGetSubsectionsFromParent_01() {

        properties.clear();
        properties.setProperty("system.db.user", "test_user");
        properties.setProperty("system.db.password", "password");
        properties.setProperty("system.host.port", "8080");
        properties.setProperty("system.host.ip", "180.35.35.32");
        List<String> expected = Arrays.asList("db", "host");;
        List<String> actual = Arrays.asList(configObject.getSubsections("system"));

        assertNotNull(actual);
        //assertArrayEquals(expected, actual);
        assertThat(actual, containsInAnyOrder(expected.toArray(new String[expected.size()])));

    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ConfigObject#getSubsections(java.lang.String)}.
     * Test to check if property subsection doesn't not exist a
     * {@link ConfigObject.ConfigObjectException} is thrown.
     */
    @Test(expected = ConfigObject.ConfigObjectException.class)
    public void testGetSubsectionsFromParent() {
        configObject.getSubsections("System");
    }

}
