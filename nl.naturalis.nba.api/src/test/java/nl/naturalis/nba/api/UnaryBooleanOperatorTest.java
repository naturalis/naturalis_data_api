package nl.naturalis.nba.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class UnaryBooleanOperatorTest {

  @Test
  public void test_parse_01() {
    assertNull("01", UnaryBooleanOperator.parse(""));
  }

  @Test
  public void test_parse_02() {
    assertEquals("02", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("NOT"));
    assertEquals("02", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("not"));
    assertEquals("02", UnaryBooleanOperator.NOT, UnaryBooleanOperator.parse("!"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_parse_03() {
    UnaryBooleanOperator.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_parse_04() {
    UnaryBooleanOperator.parse("wrong");
  }
}
