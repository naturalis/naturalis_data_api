package nl.naturalis.nba.utils;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import nl.naturalis.nba.utils.debug.BeanPrinter;

public class BeanPrinterTest {
  
  @Test
  public void test_dump_01() {  
    StringWriter sw = new StringWriter(2048);
    BeanPrinter beanPrinter = new BeanPrinter(new PrintWriter(sw));
    String str = "Test";
    Object obj = str;
    beanPrinter.dump(obj);
    assertEquals("01", "(String) \"Test\"\n", sw.toString());  
  }
  
  @Test
  public void test_dump_02() {
    StringWriter sw = new StringWriter(2048);
    BeanPrinter beanPrinter = new BeanPrinter(new PrintWriter(sw));
    Number i = Integer.valueOf(11);
    Object obj = i;
    beanPrinter.dump(obj);
    String expected = "(Integer) {\n\tsuper: (Number) {\n\t}\n\tvalue: (int) 11\n}\n";
    assertEquals("02", expected, sw.toString());
  }
  
  @Test
  public void test_dump_03() {
    StringWriter sw = new StringWriter(2048);
    BeanPrinter beanPrinter = new BeanPrinter(new PrintWriter(sw));
    beanPrinter.setShowClassNames(false);
    Long l = Long.valueOf(16L);
    Object obj = l;
    beanPrinter.dump(obj);
    String expected = "{\n\tsuper: {\n\t}\n\tvalue: 16\n}\n";
    assertEquals("03", expected, sw.toString());
  }

}
