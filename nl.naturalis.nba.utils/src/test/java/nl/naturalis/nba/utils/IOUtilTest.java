/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test class for IOUtil.java
 */
public class IOUtilTest {

    /**
     * Test method for {@link nl.naturalis.nba.utils.IOUtil#close(java.io.Closeable[])}.
     * 
     * Test to check if the close() method is called to close the stream object.
     * 
     * @throws IOException
     */
    @Test
    public void testClose_01() throws IOException {

        byte[] data = "Random String".getBytes();
        InputStream input = new ByteArrayInputStream(data);
        InputStream inputStream = Mockito.spy(input);;
        IOUtil.close(inputStream);
        Mockito.verify(inputStream, times(1)).close();
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.IOUtil#readAllBytes(java.io.InputStream)}.
     * 
     * Test to verify the byte read from a specified {@ InputStream}.
     * 
     */
    @Test
    public void testReadAllBytes() {

        URL url = getClass().getResource("TestFile.txt");
        String filePath = url.getPath().toString();
        File file = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] bytes = IOUtil.readAllBytes(is, 1024);

        assertNotNull(bytes);
        assertEquals(10, bytes.length);

        byte[] inputArray_1 = {(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        int expected = inputArray_1.length;
        InputStream byteStream = new ByteArrayInputStream(inputArray_1);
        byte[] actual = IOUtil.readAllBytes(byteStream);

        assertNotNull(actual);
        assertEquals(expected, actual.length);

    }


}
