package org.domainobject.util;

import static org.junit.Assert.*;

import org.domainobject.test.foo.Foo;
import org.junit.Test;

public class ArrayUtilTest {
/*

	@Test
	public void test_Stringify__ObjectArray()
	{
		Object[] objs = new Object[] {};
		String[] strings = ArrayUtil.stringify(objs);
		assertNotNull(strings);
		assertEquals(strings.length, 0);
		objs = new Object[] { Foo.APPLE, Foo.ERIC, Foo.FRED, Foo.PAUL, Foo.PEACH };
		strings = ArrayUtil.stringify(objs);
		assertEquals(strings.length, objs.length);
		for (int i = 0; i < strings.length; ++i) {
			assertTrue(objs[i].toString().equals(strings[i]));
		}
	}


	@Test
	public void test_Stringify__ObjectArray__Stringifier__ObjectArray()
	{
		Stringifier toDot = new Stringifier() {
			public String execute(Object object, Object... options)
			{
				return ".";
			}
		};
		Object[] objs = new Object[] { Foo.APPLE, Foo.ERIC, Foo.FRED, Foo.PAUL, Foo.PEACH };
		String[] strings = ArrayUtil.stringify(objs, toDot);
		assertEquals(strings.length, objs.length);
		for (int i = 0; i < strings.length; ++i) {
			assertTrue(strings[i].equals("."));
		}
	}


	@Test
	public void test_Translate__StringArray__Translator()
	{
		final String suffix = "_hallo";
		Translator translator = new Translator() {
			public String execute(String str, Object... options)
			{
				return str + suffix;
			}
		};
		String[] strings = new String[] { "John", "Mary", "Eric", "Marianne" };
		String[] newStrings = ArrayUtil.translate(strings, translator);
		assertEquals(strings.length, newStrings.length);
		for (int i = 0; i < strings.length; ++i) {
			assertTrue(newStrings[i].equals(strings[i] + suffix));
		}
	}


	@Test
	public void test_Translate__StringArray__Translator__Boolean__ObjectArray()
	{
		final String suffix = "_hallo";
		Translator translator = new Translator() {
			public String execute(String str, Object... options)
			{
				return str + suffix;
			}
		};
		String[] strings = new String[] { "John", "Mary", "Eric", "Marianne" };
		String[] newStrings = ArrayUtil.translate(strings, translator, true);
		assertTrue(strings == newStrings);
		assertTrue(strings[0].equals("John" + suffix));
		assertTrue(strings[1].equals("Mary" + suffix));
		assertTrue(strings[2].equals("Eric" + suffix));
		assertTrue(strings[3].equals("Marianne" + suffix));
	}

	@Test
	public void test_Convert__ObjectArray__Converter()
	{
		fail("Not yet implemented");
	}


	@Test
	public void test_Convert__ObjectArray__Converter__ObjectArray()
	{
		Converter converter = new Converter() {
			public Object execute(Object obj, Object... args)
			{
				return new StringBuilder((String) obj);
			}
		};
		String[] strings = new String[] {"A","B","C"};
		StringBuilder[] sbs = (StringBuilder[]) ArrayUtil.convert(strings, converter);
		assertEquals(sbs.length, strings.length);
	}


	@Test
	public void test_Append()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testIn()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testContains()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testImplodeObjectArray()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testImplodeObjectArrayString()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testImplodeObjectArrayStringifierObjectArray()
	{
		fail("Not yet implemented");
	}


	@Test
	public void testImplodeObjectArrayStringStringifierObjectArray()
	{
		fail("Not yet implemented");
	}
*/
}
