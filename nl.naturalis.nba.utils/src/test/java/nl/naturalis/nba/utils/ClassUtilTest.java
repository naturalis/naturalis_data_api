/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.AbstractCollection;
import java.util.ArrayList;
import org.junit.Test;

/**
 * Test class for ClassUtil.java
 */
public class ClassUtilTest {

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isA(java.lang.Class, java.lang.Class)}.
     * 
     * Test to check of whether or not the 1st class is, or extends, or implements of the 2nd class.
     */

    @Test
    public void testIsAClassOrInterface() {

        assertTrue(ClassUtil.isA(String.class, Object.class));
        assertTrue(ClassUtil.isA(Integer.class, Number.class));
        assertTrue(ClassUtil.isA(NullPointerException.class, Exception.class));
        assertTrue(ClassUtil.isA(ArrayList.class, AbstractCollection.class));
        assertFalse(ClassUtil.isA(Object.class, String.class));
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isPrimitiveNumber(java.lang.Object)}.
     * 
     * Test to check whether or not the specified object is a primitive number.
     */
    @Test
    public void testIsPrimitiveNumberObject() {

        Integer number = 1;
        String str = "";
        assertFalse(ClassUtil.isPrimitiveNumber(number));
        assertFalse(ClassUtil.isPrimitiveNumber(str));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isPrimitiveNumber(java.lang.Class)}.
     * 
     * Test to check whether or not the specified {@code Class} is one of the primitive number types
     * (int.class, float.class, etc.)
     */
    @Test
    public void testIsPrimitiveNumber() {

        assertTrue(ClassUtil.isPrimitiveNumber(int.class));
        assertFalse(ClassUtil.isPrimitiveNumber(String.class));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isNumber(java.lang.Object)}.
     * 
     * Test to check weather or not the specified object is a primitive number. Of course, an object
     * never is a primitive
     */
    @Test
    public void testIsNumberObject() {

        assertTrue(ClassUtil.isNumber(1));
        assertFalse(ClassUtil.isPrimitiveNumber(""));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isNumber(java.lang.Class)}.
     * 
     * Test to check whether or not the specified object is a primitive number or a {@link Number}.
     */
    @Test
    public void testIsNumberClassOfQ() {

        assertTrue(ClassUtil.isNumber(5));
        assertFalse(ClassUtil.isNumber("5"));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isNumeric(java.lang.Object)}.
     * 
     * Test to check whether or not the specified object is a number, or a character or string
     * representing a number
     */
    @Test
    public void testIsNumeric() {

        assertTrue(ClassUtil.isNumeric("1"));
        assertTrue(ClassUtil.isNumeric('1'));
        assertTrue(ClassUtil.isNumeric(1));
        assertFalse(ClassUtil.isNumeric("1a"));
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ClassUtil#isWrapper(java.lang.Class)}.
     * 
     * Test to check whether or not the specified object is a number, or a character or string
     * representing a number
     */
    @Test
    public void testIsWrapper() {

        assertTrue(ClassUtil.isWrapper(Boolean.class));
        assertTrue(ClassUtil.isWrapper(Integer.class));
        assertTrue(ClassUtil.isWrapper(Byte.class));
        assertTrue(ClassUtil.isWrapper(Character.class));
        assertTrue(ClassUtil.isWrapper(Long.class));
        assertTrue(ClassUtil.isWrapper(Float.class));
        assertTrue(ClassUtil.isWrapper(Double.class));
        assertTrue(ClassUtil.isWrapper(Short.class));
        assertFalse(ClassUtil.isWrapper(String.class));
        assertFalse(ClassUtil.isWrapper(Exception.class));


    }

}
