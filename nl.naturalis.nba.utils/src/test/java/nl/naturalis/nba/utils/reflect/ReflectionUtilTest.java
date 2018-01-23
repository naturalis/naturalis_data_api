package nl.naturalis.nba.utils.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.Test;

@SuppressWarnings("static-method")
public class ReflectionUtilTest {

  @Test
  public void testNewInstance_01() {
    Person p = ReflectionUtil.newInstance(Person.class, "John Smith");
    assertEquals("01", "John Smith", p.getName());
  }

  @Test
  public void testNewInstance_02() {
    Person p = ReflectionUtil.newInstance(Person.class, new Class<?>[] {String.class, int.class},
        "John Smith", 37);
    assertEquals("01", "John Smith", p.getName());
  }

  @Test
  public void testSet_01() {
    Person p = new Person();
    ReflectionUtil.set(p, "name", "John Smith");
    assertEquals("01", "John Smith", p.getName());
  }

  @Test
  public void testSet_02() {
    Person p = new Person();
    ReflectionUtil.set(p, "age", 39);
    assertEquals("01", 39, p.getAge());
  }

  @Test
  public void testGet() {
    Person p = new Person();
    p.setName("John Smith");
    String s = (String) ReflectionUtil.get(p, "name");
    assertEquals("01", "John Smith", s);
  }

  @Test
  public void testCall_01() {
    Person p = new Person();
    p.setAge(15);
    double d = (double) ReflectionUtil.call(p, "calculateMinimumWage",
        new Class<?>[] {int.class, boolean.class}, 1000, true);
    // age * multiplier + bonus = 15 * 1000 + 1000 = 16000
    assertTrue("01", d == 16000);
  }

  @Test
  public void testCallStatic_01() {
    int i = (int) ReflectionUtil.callStatic(Person.class, "getAverageAge");
    assertTrue("01", 40 == i);
  }

  @Test
  public void testGetField_01() {
    Field f = ReflectionUtil.getField(Person.class, "name");
    assertNotNull("01", f);
  }

  @Test
  public void testGetField_02() {
    Field f = ReflectionUtil.getField(Person.class, "foo");
    assertNull("01", f);
  }

  @Test
  public void testGetMethod_01() {
    Method m =
        ReflectionUtil.getMethod(Person.class, "calculateMinimumWage", int.class, boolean.class);
    assertNotNull("01", m);
  }

}
