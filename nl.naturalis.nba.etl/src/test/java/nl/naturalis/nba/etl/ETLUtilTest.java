
package nl.naturalis.nba.etl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for ETLUtil.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ETLUtil.class)
@PowerMockIgnore("javax.management.*")
public class ETLUtilTest {


    @Before
    public void setUp() throws Exception {

        String fileName = "log4j2.xml";
        URL url = getClass().getResource(fileName);
        String filePath = url.getPath().toString();
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        System.setProperty("nba.v2.conf.dir", dirPath);
        System.setProperty("log4j.configurationFile", filePath);
        System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getSpecimenPurl(java.lang.String)}.
     * 
     * Test to verify specimen URL
     */
    @Test
    public void testGetSpecimenPurl() {

        String purl = ETLUtil.getSpecimenPurl("L  0219");

        assertNotNull(purl);
        assertEquals("01", "http://data.biodiversitydata.nl/naturalis/specimen/L%20%200219", purl);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getRootCause(java.lang.Throwable)}.
     * 
     * Test the rootOf() which returns the root cause of the specified throwable
     */
    @Test
    public void testGetRootCause() {

        IOException ioException = new IOException("This is a an IO Exception...");
        Throwable actual = ETLUtil.getRootCause(ioException);
        String expected = "This is a an IO Exception...";

        assertNotNull(actual);
        assertEquals(expected, actual.getMessage());
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.ETLUtil#logDuration(org.apache.logging.log4j.Logger, java.lang.Class, long)}.
     * 
     * Test to verify if logDuration is called.
     * 
     * @throws Exception
     * 
     */
    @SuppressWarnings("static-access")
    @Test
    public void testLogDuration() throws Exception {

        Logger logger = ETLUtil.getLogger(Loader.class);
        long timeStamp = 12585L;
        ETLUtil etlUtil = PowerMockito.mock(ETLUtil.class);// This works only when the constructor in the ETLUtil.java is made public.
        etlUtil.logDuration(logger, logger.getClass(), timeStamp);
        Mockito.verify(etlUtil, times(1)).logDuration(logger, logger.getClass(), timeStamp);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getDuration(long)}.
     * 
     * Test to see if the gerDuration() returns a string in time format 00:00:00
     */
    @Test
    public void testGetDuration() {

        String actual = ETLUtil.getDuration(1512654710171L);
        Pattern patternFormat = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Pattern validFormat = Pattern.compile("[0-2]|[0-9]:[0-5]|[0-9]:[0-5]|[0-9]");
        Matcher matcherFormat = patternFormat.matcher(actual);
        Matcher matcherValidFormat = validFormat.matcher(actual);

        assertTrue(matcherFormat.find());
        assertTrue(matcherValidFormat.find());
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getDuration(long, long)}.
     * 
     * Test to verify log duration format.
     */
    @Test
    public void testGetDurationLong() {

        String actual = ETLUtil.getDuration(1512654710171L, 1512658954629L);
        Pattern patternFormat = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Pattern ifValidFormat = Pattern.compile("[0-2]|[0-9]:[0-5]|[0-9]:[0-5]|[0-9]");
        Matcher matcherFormat = patternFormat.matcher(actual);
        Matcher matcherValidFormat = ifValidFormat.matcher(actual);

        assertTrue(matcherFormat.find());
        assertTrue(matcherValidFormat.find());
    }


    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getLogger(java.lang.Class)}.
     * 
     * Test for Get Logger method.
     */
    @Test
    public void testGetLogger() {

        String expected = "nl.naturalis.nba.etl.Loader";
        Logger actual = ETLUtil.getLogger(Loader.class);

        assertNotNull(actual);
        assertEquals(actual.getName(), expected);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#truncate(nl.naturalis.nba.dao.DocumentType)}.
     */
    @Test
    public void testTruncateDocumentTypeOfT() {}

    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getTestGenera()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the array of genera that is returned by getTestGeneral
     */
    @Test
    public void testGetTestGenera() throws Exception {
        String[] actual = ETLUtil.getTestGenera();
        String[] expected = {"malus", "parus", "larus", "bombus", "rhododendron", "felix", "tulipa", "rosa", "canis", "passer", "trientalis"};

        assertNotNull(expected);
        assertArrayEquals(expected, actual);
    }


    /**
     * Test method for {@link nl.naturalis.nba.etl.ETLUtil#getPurlBuilder()}.
     * 
     * @throws Exception
     * 
     *         Test to verify private getUrlBuilder method returns the correct url
     */
    @SuppressWarnings("static-access")
    @Test
    public void testGetPurlBuilder() throws Exception {

        ETLUtil etlUtilSpy = PowerMockito.spy(new ETLUtil());
        String methodName = "getPurlBuilder";
        String url = "http://data.biodiversitydata.nl";
        URIBuilder purlBuilder = new URIBuilder(url);
        PowerMockito.doReturn(purlBuilder).when(etlUtilSpy, methodName);
        URIBuilder actualBuilder = etlUtilSpy.callGetPurlBuilder();
        assertEquals(purlBuilder.getHost(), actualBuilder.getHost());

    }

}
