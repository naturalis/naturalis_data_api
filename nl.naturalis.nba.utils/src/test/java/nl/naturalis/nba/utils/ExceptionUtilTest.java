/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.util.NoSuchElementException;
import org.junit.Test;

/**
 * Test class for ExceptionUtil.java
 */
@SuppressWarnings({"static-method"})
public class ExceptionUtilTest {

    /**
     * Test method for {@link nl.naturalis.nba.utils.ExceptionUtil#rootOf(java.lang.Throwable)}. 
     * 
     * Test the rootOf() which returns the root cause of the specified throwable
     */
    @Test
    public void testRootOf() {

        IOException ioException = new IOException("This is a an IO Exception...");
        Throwable actual = ExceptionUtil.rootOf(ioException);
        String expected = "This is a an IO Exception...";
        assertNotNull(actual);
        assertEquals(expected, actual.getMessage());

    }
    /**
     * Test method for {@link nl.naturalis.nba.utils.ExceptionUtil#rootStackTrace(java.lang.Throwable)}.
     * 
     * Test for rootStackTrace() which creates a stack trace for the passed throwable class.
     */
    @Test
    public void testRootStackTrace() {

        ArrayStoreException arithmeticException = new ArrayStoreException("This is a an ArrayStore Exception");
        String actual = ExceptionUtil.rootStackTrace(arithmeticException);
        assertNotNull(actual);
        assertTrue(actual.contains("This is a an ArrayStore Exception"));
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ExceptionUtil#smash(java.lang.Throwable)}. This
     * 
     * Test checks if the Exception is wrapped into RuntimeException if its not already a Runtime
     * Exception.
     */
    @Test
    public void testSmash() {

        RuntimeException actual_1 = ExceptionUtil.smash(new WrongMethodTypeException());
        RuntimeException actual_2 = ExceptionUtil.smash(new ClassNotFoundException());
        RuntimeException actual_3 = ExceptionUtil.smash(new NoSuchElementException());
        RuntimeException actual_4 = ExceptionUtil.smash(new NoSuchFieldException());

        assertNotNull(actual_1);
        assertEquals("class java.lang.invoke.WrongMethodTypeException", actual_1.getClass().toString());
        assertNotNull(actual_2);
        assertEquals("class java.lang.RuntimeException", actual_2.getClass().toString());
        assertNotNull(actual_3);
        assertEquals("class java.util.NoSuchElementException", actual_3.getClass().toString());
        assertNotNull(actual_4);
        assertEquals("class java.lang.RuntimeException", actual_4.getClass().toString());

    }

}
