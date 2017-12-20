/**
 * 
 */
package nl.naturalis.nba.utils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

/**
 * Test class for ObjectUtilTest.java
 */
@SuppressWarnings("static-method")
public class ObjectUtilTest {
    /**
     * Test method for
     * {@link nl.naturalis.nba.utils.ObjectUtilTest#compare(java.lang.Comparable, java.lang.Comparable)}.
     * 
     * Test to compare two objects
     */
    @Test
    public void testCompare() {

        int check;
        String testString_1 = new String("TestString_01");
        String testString_2 = new String("TestString_01");
        String testString_3 = new String("TestString_02");

        check = ObjectUtil.compare(testString_1, testString_2);
        assertThat(check, is(equalTo(0)));

        check = ObjectUtil.compare(testString_1, testString_3);
        assertThat(check, is(lessThan(0)));

        check = ObjectUtil.compare(testString_3, testString_1);
        assertThat(check, is(greaterThan(0)));

        check = ObjectUtil.compare(null, null);
        assertNotNull(check);

    }

}
