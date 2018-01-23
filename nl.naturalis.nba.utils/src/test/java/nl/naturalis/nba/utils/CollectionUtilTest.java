/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import nl.naturalis.nba.utils.convert.Stringifier;

/**
 * Test class for CollectionUtil.java
 */
@SuppressWarnings({"static-method","cast"})
public class CollectionUtilTest {


    /**
     * Test method for {@link nl.naturalis.nba.utils.CollectionUtil#isEmpty(java.util.Collection)}.
     * 
     * Test to check if a collection is empty.
     */

    @Test
    public void testIsEmpty() {

        List<String> list = new ArrayList<>();
        assertTrue(CollectionUtil.isEmpty(list));
        list.add("TestString");
        assertFalse(CollectionUtil.isEmpty(list));

    }

    /**
     * Test method for {@link nl.naturalis.nba.utils.CollectionUtil#hasElements(java.util.Collection)}.
     * 
     * Test to check of a collection has elements or not.
     */
    @Test
    public void testHasElements() {
        List<Integer> list = Arrays.asList(3, 2, 1, 4, 5, 6, 6);
        assertTrue(CollectionUtil.hasElements(list));
        assertFalse(CollectionUtil.hasElements(new ArrayList<>()));
    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.CollectionUtil#stringify(java.util.Collection)}.
     * 
     * Test to verify specified collection of objects to an List of strings.
     */
    @Test
    public void testStringifyCollectionOfT() {

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<String> actualResult = CollectionUtil.stringify(ints);

        assertNotNull(actualResult);
        assertTrue(actualResult instanceof List);
        assertEquals(7, actualResult.stream().count());

    }


    /**
     * Test method for {@link nl.naturalis.nba.utils.CollectionUtil#stringify(java.util.Collection)}.
     * 
     * Test to verify specified collection of objects to an List of strings using the specified
     * {@link Stringifier}.
     */
    @Test
    public void testStringifyCollectionWithOptionObjects() {

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        Stringifier<Integer> callback = mock(Stringifier.class);

        when(callback.execute(ints.get(0), "")).thenReturn("1");
        when(callback.execute(ints.get(1), "")).thenReturn("2");
        when(callback.execute(ints.get(2), "")).thenReturn("3");
        when(callback.execute(ints.get(3), "")).thenReturn("4");
        when(callback.execute(ints.get(4), "")).thenReturn("5");

        List<String> actualResult = CollectionUtil.stringify(ints, callback, "");

        assertNotNull(actualResult);
        assertTrue(actualResult instanceof List);
        assertEquals(5, actualResult.stream().count());

    }



    /**
     * Test method for {@link nl.naturalis.nba.utils.CollectionUtil#implode(java.util.Collection)}.
     * 
     * Test to verfiy implosion of the specified collection using a comma separator
     */
    @Test
    public void testImplodeCollectionOfT() {


        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<String> strings = Arrays.asList("Test1", "Test 2", "Test 3", "Test 4");
        String expectedResult = "1,2,3,4,5,6,7";
        String expectedResult_1 = "Test1,Test 2,Test 3,Test 4";
        String actualResult = CollectionUtil.implode(ints);
        String actualResult_1 = CollectionUtil.implode(strings);

        assertNotNull(actualResult);
        assertNotNull(actualResult_1);
        assertEquals(expectedResult, actualResult);
        assertEquals(actualResult_1, expectedResult_1);
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.CollectionUtil#implode(java.util.Collection, java.lang.String)}.
     * 
     * Test to verfiy implosion of the specified collection using a specified separator
     */
    @Test
    public void testImplodeCollectionWithSeperator() {

        String seperator = ",";
        String nullSeperator = null;
        List<Integer> testInput = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<String> nullTestInput = null;

        String actualResult;
        String expectedResult_01 = "1,2,3,4,5,6,7";
        String expectedResult_02 = "1null2null3null4null5null6null7";
        String expectedResult_03 = "";

        actualResult = CollectionUtil.implode(testInput, seperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_01));
        assertEquals(expectedResult_01, actualResult);

        actualResult = CollectionUtil.implode(testInput, nullSeperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_02));
        assertEquals(expectedResult_02, actualResult);

        actualResult = CollectionUtil.implode(nullTestInput, nullSeperator);
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_03));
        assertEquals(expectedResult_03, actualResult);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.CollectionUtil#implode(java.util.Collection, java.lang.String, nl.naturalis.nba.utils.convert.Stringifier, java.lang.Object[])}.
     * 
     * Test to verify implosion of the specified collection using a specified separator and a specified {@link Stringifier}.
     * 
     */
    @Test
    public void testImplodeCollectionStingifierOptionalObjects() {


        List<Integer> testInput = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> nullTestInput = null;

        String seperator = ",";
        String nullSeperator = null;
        String actualResult = null;
        String expectedResult_01 = "1,2,3,4,5";
        String expectedResult_02 = "";
        String expectedResult_03 = "1null2null3null4null5";

        Stringifier<Integer> callback = mock(Stringifier.class);

        when(callback.execute(testInput.get(0), "")).thenReturn("1");
        when(callback.execute(testInput.get(1), "")).thenReturn("2");
        when(callback.execute(testInput.get(2), "")).thenReturn("3");
        when(callback.execute(testInput.get(3), "")).thenReturn("4");
        when(callback.execute(testInput.get(4), "")).thenReturn("5");

        actualResult = CollectionUtil.implode(testInput, seperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_01));
        assertEquals(expectedResult_01, actualResult);

        actualResult = CollectionUtil.implode(nullTestInput, seperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_02));
        assertEquals(expectedResult_02, actualResult);

        actualResult = CollectionUtil.implode(testInput, nullSeperator, callback, "");
        assertNotNull(actualResult);
        assertThat(actualResult, is(expectedResult_03));
        assertEquals(expectedResult_03, actualResult);
    }

}
