package nl.naturalis.nba.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.junit.Test;
import nl.naturalis.nba.utils.convert.Stringifier;
import nl.naturalis.nba.utils.convert.Translator;

/**
 * Test class for ArryUtil.java
 */
@SuppressWarnings({"static-method","cast"})
public class ArrayUtilTest {

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#box(int[])} Tests the conversion of int
     * array to Integer array.
     * 
     * Test to check conversion of primitive int array to Integer array.
     * 
     */

    @Test
    public void testBox_01() {
        int[] inputArray = {1, 2, 3, 4, 5, 6, 7};
        Integer[] expectedResults = {1, 2, 3, 4, 5, 6, 7};
        Integer[] atualResult = ArrayUtil.box(inputArray);

        assertNotNull(atualResult);
        assertEquals(atualResult.length, inputArray.length);
        assertArrayEquals(expectedResults, atualResult);
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#toArray(java.util.Enumeration)}. Test to
     * check the conversion Ennumeration to an Object[].
     * 
     * Test to check the conversion of an enumeration to a Object Array
     */
    @Test
    public void testEnumToArray_01() {

        List<String> list = new ArrayList<>();
        list.add("Sunday");
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        Enumeration<String> enm = Collections.enumeration(list);

        Object[] expected = {"Sunday", "Monday", "Tuesday", "Wednesday"};
        Object[] actualResult = ArrayUtil.toArray(enm);

        assertNotNull(actualResult);
        assertArrayEquals(expected, actualResult);
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#stringify(T[])} assert : not null, true
     * 
     * Test to check the conversion of array of objects to a string array
     */
    @Test
    public void testStringifyTArray_01() {

        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        Integer int3 = new Integer(3);
        Integer int4 = new Integer(4);
        Integer int5 = new Integer(5);

        Object[] inputArray = {int1, int2, int3, int4, int5};
        String[] actualResult = ArrayUtil.stringify(inputArray);
        String[] expectedResult = {"1", "2", "3", "4", "5"};

        assertNotNull(actualResult);
        assertTrue(actualResult instanceof String[]);
        assertArrayEquals(expectedResult, actualResult);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ArrayUtil#stringify(T[], nl.naturalis.nba.utils.convert.Stringifier, java.lang.Object[])}
     * assert : not null, true
     * 
     * Test to check the conversion of array of objects to a string array with additional parameters :
     * Stringifier , an optional Object[]
     */
    @Test
    public void testStringifyTArrayStringifierOptionalObjs() {

        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        Integer int3 = new Integer(3);
        Integer int4 = new Integer(4);
        Integer int5 = new Integer(5);

        Object[] inputArray = {int1, int2, int3, int4, int5};
        String[] expectedResult = {"1", "2", "3", "4", "5"};

        Stringifier<Object> callback = mock(Stringifier.class);

        when(callback.execute(inputArray[0], "")).thenReturn("1");
        when(callback.execute(inputArray[1], "")).thenReturn("2");
        when(callback.execute(inputArray[2], "")).thenReturn("3");
        when(callback.execute(inputArray[3], "")).thenReturn("4");
        when(callback.execute(inputArray[4], "")).thenReturn("5");

        String[] actualResult = ArrayUtil.stringify(inputArray);
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof String[]);
        assertArrayEquals(expectedResult, actualResult);

    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#stringify(java.util.Collection)} assert :
     * not null, true
     * 
     * Test to check the conversion of a collection object to an String array.
     * 
     */
    @Test
    public void testStringifyCollectionOfT() {

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        String[] actualResult = ArrayUtil.stringify(ints);
        String[] expectedResult = {"1", "2", "3", "4", "5", "6", "7"};

        assertNotNull(actualResult);
        assertTrue(actualResult instanceof String[]);
        assertArrayEquals(expectedResult, actualResult);
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ArrayUtil#stringify(java.util.Collection, nl.naturalis.nba.utils.convert.Stringifier, java.lang.Object[])}
     * assert : not null, true
     * 
     * Test to check the conversion of a collection object to an String array with using the specified {@link Stringifier}.
     */
    @Test
    public void testStringifyCollectionOfTStringifierOptionalObjs() {

        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        Integer int3 = new Integer(3);
        Integer int4 = new Integer(4);
        Integer int5 = new Integer(5);

        Object[] inputArray = {int1, int2, int3, int4, int5};
        String[] expectedResult = {"1", "2", "3", "4", "5"};

        Stringifier<Object> callback = mock(Stringifier.class);
        when(callback.execute(inputArray[0], "")).thenReturn("1");
        when(callback.execute(inputArray[1], "")).thenReturn("2");
        when(callback.execute(inputArray[2], "")).thenReturn("3");
        when(callback.execute(inputArray[3], "")).thenReturn("4");
        when(callback.execute(inputArray[4], "")).thenReturn("5");

        String[] actualResult = ArrayUtil.stringify(inputArray, callback, "");

        assertNotNull(actualResult);
        assertTrue(actualResult instanceof String[]);
        assertArrayEquals(expectedResult, actualResult);
    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ArrayUtil#translate(java.lang.String[], nl.naturalis.nba.utils.convert.Translator)}.
     * 
     * Test to check translation of one text to another.
     */
    @Test
    public void testTranslateStringArrayTranslator_01() {

        String[] stringArrayInput = {"Old_1", "Old_2"};
        String[] expected = {"Replaced_1", "Replaced_2"};
        String[] actual = new String[stringArrayInput.length];

        Translator translator = mock(Translator.class);
        when(translator.execute(stringArrayInput[0], "")).thenReturn("Replaced_1");
        when(translator.execute(stringArrayInput[1], "")).thenReturn("Replaced_2");

        actual = ArrayUtil.translate(stringArrayInput, translator, false, "");
        assertArrayEquals(expected, actual);
    }



    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#concat(java.lang.String[][])}.Assert :
     * not null; Equals, Array Equals
     * 
     * Test to check concatination of multiple string arrays into one.
     */
    @Test
    public void testConcatStringArrayArray_01() {

        String[] inputArray_1 = {"This", "is"};
        String[] inputArray_2 = {"a", "contact", "test"};
        String[] expected = {"This", "is", "a", "contact", "test"};
        String[] actualResult = ArrayUtil.concat(inputArray_1, inputArray_2);

        assertNotNull(actualResult);
        assertEquals(5, actualResult.length);
        assertArrayEquals(expected, actualResult);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#concat(java.lang.String[][])}. expect =
     * NullPonterException
     * 
     */
    @Test(expected = NullPointerException.class)
    public void testConcatStringArrayArray_02() {

        String[] inputArray_1 = null;
        String[] arrinputArray_2 = null;
        ArrayUtil.concat(inputArray_1, arrinputArray_2);
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#concat(java.lang.Object[][])}.
     * 
     * Test to check concatination of multiple string arrays into one.
     */
    @Test
    public void testConcatObjectArrayArray_01() {

        Object[] inputArray_1 = {"This", "is"};
        Object[] inputArray_2 = {"a", "contact", "test"};
        Object[] expected = {"This", "is", "a", "contact", "test"};
        Object[] actualResult = ArrayUtil.concat(inputArray_1, inputArray_2);

        assertNotNull(actualResult);
        assertEquals(5, actualResult.length);
        assertArrayEquals(actualResult, expected);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#concat(java.lang.Object[][])}.
     */
    @Test(expected = NullPointerException.class)
    public void testConcatObjectArrayArray_02() {

        Object[] inputArray1 = null;
        Object[] inputArray2 = null;
        ArrayUtil.concat(inputArray1, inputArray2);
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#in(java.lang.Object, T[])}.
     * 
     * Test to check the presence of an obj in a array of objects.
     */
    @Test
    public void testInTTArray_01() {

        String testStringObj_01 = "Test_01";
        String testStringObj_02 = "Test_02";
        String[] inputArray = {testStringObj_01, "teststring1", "testString2"};

        boolean conditionTrue = ArrayUtil.in(testStringObj_01, inputArray);
        assertTrue(conditionTrue);

        boolean condtitionFalse = ArrayUtil.in(testStringObj_02, inputArray);
        assertFalse(condtitionFalse);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#in(char, char[])}.
     * 
     * test to check if a specified character is in the specified array of characters.
     */
    @Test
    public void testInCharCharArray() {

        char testCharObj_01 = 'a';
        char testCharObj_02 = 'b';
        char[] inputArray = {testCharObj_01, 'z', 'm', 't'};

        boolean conditionTrue = ArrayUtil.in(testCharObj_01, inputArray);
        assertTrue(conditionTrue);

        boolean condtitionFalse = ArrayUtil.in(testCharObj_02, inputArray);
        assertFalse(condtitionFalse);
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#has(java.lang.Object, T[])}.
     * 
     * Test to check if a specified object is in the specified array.
     * 
     */
    @Test
    public void testHas() {

        String testStringObj_01 = "Test_01";
        String testStringObj_02 = "Test_02";
        String[] inputArray = {testStringObj_01, "teststring1", "testString2"};

        boolean conditionTrue = ArrayUtil.has(testStringObj_01, inputArray);
        assertNotNull(conditionTrue);
        assertTrue(conditionTrue);

        boolean condtitionFalse = ArrayUtil.has(testStringObj_02, inputArray);
        assertNotNull(conditionTrue);
        assertFalse(condtitionFalse);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#implode(T[])}.
     * 
     * Test to implode the specified array using a comma separator
     */
    @Test
    public void testImplodeTArray() {

        Integer[] testInput = {1, 2, 3, 4, 5, 6, 7};
        String expectedResult = "1,2,3,4,5,6,7";
        String actualResult = ArrayUtil.implode(testInput);

        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#implode(T[], java.lang.String)}.
     * 
     * Test to implode the specified array using a specified separator
     */
    @Test
    public void testImplodeTArraySeparator() {

        String seperator = ",";
        String nullSeperator = null;
        Integer[] testInput = {1, 2, 3, 4, 5, 6, 7};
        Integer[] nullTestInput = null;

        String actualResult;
        String expectedResult_01 = "1,2,3,4,5,6,7";
        String expectedResult_02 = "1null2null3null4null5null6null7";
        String expectedResult_03 = "";

        actualResult = ArrayUtil.implode(testInput, seperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_01));
        assertEquals(expectedResult_01, actualResult);

        actualResult = ArrayUtil.implode(testInput, nullSeperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_02));
        assertEquals(expectedResult_02, actualResult);

        actualResult = ArrayUtil.implode(nullTestInput, nullSeperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_03));
        assertEquals(expectedResult_03, actualResult);

    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ArrayUtil#implode(T[], java.lang.String, nl.naturalis.nba.utils.convert.Stringifier, java.lang.Object[])}.
     * 
     * Test to implode the specified array using a comma separator with additional using the specified {@link Stringifier}.
     * 
     */
    @Test
    public void testImplodeTArrayStringStringifierOfTObjectArray() {

        Integer[] testInput = {1, 2, 3, 4, 5};
        Integer[] nullTestInput = null;
        String seperator = ",";
        String nullSeperator = null;
        String actualResult = null;
        String expectedResult_01 = "1,2,3,4,5";
        String expectedResult_02 = "";
        String expectedResult_03 = "1null2null3null4null5";

        Stringifier<Integer> callback = mock(Stringifier.class);

        when(callback.execute(testInput[0], "")).thenReturn("1");
        when(callback.execute(testInput[1], "")).thenReturn("2");
        when(callback.execute(testInput[2], "")).thenReturn("3");
        when(callback.execute(testInput[3], "")).thenReturn("4");
        when(callback.execute(testInput[4], "")).thenReturn("5");

        actualResult = ArrayUtil.implode(testInput, seperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_01));
        assertEquals(expectedResult_01, actualResult);

        actualResult = ArrayUtil.implode(nullTestInput, seperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_02));
        assertEquals(expectedResult_02, actualResult);

        actualResult = ArrayUtil.implode(testInput, nullSeperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_03));
        assertEquals(expectedResult_03, actualResult);

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.ArrayUtil#deepEquals(byte[], byte[])} to check for
     * equality of byte array.
     * 
     * Test to check equality of to byte arrays.
     */
    @Test
    public void testDeepEquals() {

        byte[] inputArray_1 = {(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] inputArray_2 = {(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] inputArray_3 = {(byte) 0x04, (byte) 0x05, (byte) 0x00, (byte) 0x00};

        boolean byteEquals = ArrayUtil.deepEquals(inputArray_1, inputArray_2);
        assertTrue(byteEquals);

        boolean byteNotEquals = ArrayUtil.deepEquals(inputArray_1, inputArray_3);
        assertFalse(byteNotEquals);

    }
}
