/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FileUtil.java
 *
 */
public class FileUtilTest {

    String filePath;
    String dirPath;
    File fileDir;
    File file;

    @Before
    public void setUp() {

        URL url = getClass().getResource("TestFile.txt");
        filePath = url.getPath().toString();
        dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        fileDir = new File(dirPath);
        file = new File(filePath);
    }

    @After
    public void tearDown() {}

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#newFile(java.io.File, java.lang.String)}.
     * 
     * Test the creation of new file based on the supplied file directory and file name
     */
    @Test
    public void testNewFile() {

        File actual = FileUtil.newFile(fileDir, "TestFile.txt");
        assertNotNull(actual);
        assertTrue(actual instanceof File);
        assertEquals(filePath, actual.getAbsolutePath());
        assertEquals("TestFile.txt", actual.getName());

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#newFileInputStream(java.io.File)}.
     * 
     * Test to retrieve the {@ InputStream} of a specified file.
     * 
     */
    @Test
    public void testNewFileInputStream() throws IOException {

        String expected = "Test File.";
        InputStream inputStream = FileUtil.newFileInputStream(file);
        String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());

        assertNotNull(inputStream);
        assertThat(expected, equalTo(text));
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.FileUtil#containsFile(java.io.File, java.lang.String)}.
     * 
     * Test to check if the specified directory path contains the specified file.
     */
    @Test
    public void testContainsFile() {

        boolean contains = FileUtil.containsFile(new File(dirPath), "TestFile.txt");
        boolean notContains = FileUtil.containsFile(new File(dirPath), "test.txt");

        assertTrue(contains);
        assertFalse(notContains);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.FileUtil#containsSubdirectory(java.io.File, java.lang.String)}.
     * 
     * Test to check if the sub-directory name is present in specified directory
     */
    @Test
    public void testContainsSubdirectory() {

        boolean conditionTrue = FileUtil.containsSubdirectory(fileDir, "xml");
        assertTrue(conditionTrue);
        boolean conditionFalse = FileUtil.containsFile(fileDir, "boo");
        assertFalse(conditionFalse);


    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#getSubdirectories(java.io.File)}.
     * 
     * Test to get subDirectories from the specified directory.
     */
    @Test
    public void testGetSubdirectories() {

        String expectedDir = "/xml";
        File[] subDirectories_01 = FileUtil.getSubdirectories(fileDir);
        String actual = subDirectories_01[1].toString().substring(subDirectories_01[0].toString().lastIndexOf("/"));
        assertNotNull(subDirectories_01);
        assertEquals(expectedDir, actual);

        File[] subDirectories = FileUtil.getSubdirectories(new File("test"));
        assertNull(subDirectories);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.FileUtil#convertEncoding(java.io.File, java.io.File, java.lang.String, java.lang.String)}.
     * 
     * Test that verifies the conversion file unicode to UTF-8
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testConvertEncoding() throws IOException {

        String path = dirPath + "/" + "AsciiFile.txt";
        // Creating a file with ISO encoding
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1));
        out.write("This is a test String");
        out.flush();
        out.close();
        String outPutFile = dirPath + "/" + "UTF8File.txt";
        // Change the encoding of a file created above to UTF-8
        FileUtil.convertToUtf8(path, outPutFile, "UTF-8");
        // Get the converted file and verify the new encoding.
        InputStream is = getClass().getResourceAsStream("UTF8File.txt");
        InputStreamReader isr = new InputStreamReader(is);
        assertNotNull(isr);
        assertEquals("UTF8", isr.getEncoding());
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#getContents(java.lang.String)}.
     * 
     * Test to checks the String content returned from a file.
     */
    @Test
    public void testGetFileContentsAsString() {

        String actual = FileUtil.getContents(file);
        assertNotNull(actual);
        assertEquals("Test File.", actual);
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#getByteContents(java.lang.String)}.
     * 
     * Test which verifies the actual bytes after reading a test file.
     */
    @Test
    public void testGetFileByteContents() {

        byte[] actual = FileUtil.getByteContents(filePath);
        assertNotNull(actual);
        assertEquals(10, actual.length);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#getContents(java.io.InputStream)}.
     * 
     * Test to verifies the {@ InputStream} retured after reading a file.
     */
    @Test
    public void testGetContentsInputStream() {
        InputStream is = getClass().getResourceAsStream("TestFile.txt");
        String actual = FileUtil.getContents(is);

        assertNotNull(actual);
        assertEquals("Test File.", actual);

    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.FileUtil#setContents(java.io.File, java.io.InputStream, int, boolean)}.
     * 
     * Test to exhaust the specified input stream and writes the data read from it to the specified
     * file.
     */
    @Test
    public void testSetContentsFileInputStreamIntBoolean() {

        String path = dirPath + "/" + "SetContentFile.txt";
        File file = new File(path);
        byte[] data = "Random String".getBytes();
        InputStream input = new ByteArrayInputStream(data);

        FileUtil.setContents(file, input, 2048, false);// false : will delete the old file and create a new one.

        InputStream is = getClass().getResourceAsStream("SetContentFile.txt");
        String actual = FileUtil.getContents(is);

        assertNotNull(actual);
        assertEquals("Random String", actual);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.FileUtil#log(java.io.File, java.lang.String, java.lang.Object[])}.
     * 
     * Test the creation of a log file with a log message.
     */
    @Test
    public void testLogFileStringObjectArray() {

        String path = dirPath + "/" + "TestLogFile.txt";
        File file = new File(path);
        String message = "Logging: File Util Test....";
        FileUtil.log(file, message);

        InputStream is = getClass().getResourceAsStream("TestLogFile.txt");
        String actual = FileUtil.getContents(is);

        assertNotNull(actual);
        assertTrue(actual.contains(message));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.FileUtil#delete(java.lang.String)}.
     * 
     * Test the deletion of a specified file.
     */
    @Test
    public void testDelete() {

        URL url = getClass().getResource("TestLogFile.txt");
        String filePath = url.getPath().toString();
        FileUtil.delete(filePath);
        InputStream is = getClass().getResourceAsStream("TestLogFile.txt");

        assertNull(is);

    }

}
