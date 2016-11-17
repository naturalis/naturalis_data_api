package org.domainobject.util;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.naturalis.nba.utils.StringUtil;

public class StringUtilTest {

	@Test
	public void testLtrim()
	{
		assertEquals("01 Should be empty.", "", StringUtil.ltrim(null, 'a'));
		assertEquals("02 Should be empty.", "", StringUtil.ltrim("", 'a'));
		assertEquals("03 Should be empty.", "", StringUtil.ltrim("a", 'a'));
		assertEquals("04 Should be empty.", "", StringUtil.ltrim("aa", 'a'));
		assertEquals("05 Should be empty.", "", StringUtil.ltrim("aaa", 'a'));
		assertEquals("06 Should be \"b\".", "b", StringUtil.ltrim("b", 'a'));
		assertEquals("07 Should be \"b\".", "b", StringUtil.ltrim("aaab", 'a'));
		assertEquals("08 Should be \"bb\".", "bb", StringUtil.ltrim("aaabb", 'a'));
		assertEquals("09 Should be \"bb\".", "bb", StringUtil.ltrim("bb", 'a'));
		assertEquals("10 Should be \"bba\".", "bba", StringUtil.ltrim("bba", 'a'));
		assertEquals("11 Should be \"bbaa\".", "bbaa", StringUtil.ltrim("bbaa", 'a'));
	}

	@Test
	public void testLtrimWord()
	{
		assertEquals("01", null, StringUtil.lchop(null, null));
		assertEquals("02", "whatever", StringUtil.lchop("whatever", null));
		assertEquals("03", "", StringUtil.lchop("whatever", "whatever"));
		assertEquals("04", "A", StringUtil.lchop("whateverA", "whatever"));
		assertEquals("05", "AA", StringUtil.lchop("whateverAA", "whatever"));
		assertEquals("06", "BAA", StringUtil.lchop("BAA", "whatever"));
		assertEquals("07", "BAA", StringUtil.lchop("BAA", "AA"));
		assertEquals("08", "whatever", StringUtil.lchop("ABABwhatever", "ABAB"));
		assertEquals("09", "whatever", StringUtil.lchop("ABCABCwhatever", "ABC"));
	}

	@Test
	public void testRtrim()
	{
		assertEquals("01 Should be empty.", "", StringUtil.ltrim(null, 'a'));
		assertEquals("02 Should be empty.", "", StringUtil.rtrim("", 'a'));
		assertEquals("03 Should be empty.", "", StringUtil.rtrim("a", 'a'));
		assertEquals("04 Should be empty.", "", StringUtil.rtrim("aa", 'a'));
		assertEquals("05 Should be empty.", "", StringUtil.rtrim("aaa", 'a'));
		assertEquals("06 Should be \"b\".", "b", StringUtil.rtrim("b", 'a'));
		assertEquals("07 Should be \"b\".", "b", StringUtil.rtrim("baaa", 'a'));
		assertEquals("08 Should be \"bb\".", "bb", StringUtil.rtrim("bbaaa", 'a'));
		assertEquals("09 Should be \"bb\".", "bb", StringUtil.rtrim("bb", 'a'));
		assertEquals("10 Should be \"abb\".", "abb", StringUtil.rtrim("abb", 'a'));
		assertEquals("11 Should be \"aabb\".", "aabb", StringUtil.rtrim("aabb", 'a'));
	}

	@Test
	public void testSplit()
	{
		String delim = "[^]";
		String s = null;
		String[] chunks = StringUtil.split(s, delim);
		assertNull(chunks);

		s = "abc" + delim;
		chunks = StringUtil.split(s, delim);
		assertEquals("01", 1, chunks.length);
		assertEquals("02", "abc", chunks[0]);

		s = "abc" + delim + "def";
		chunks = StringUtil.split(s, delim);
		assertEquals("03", 2, chunks.length);
		assertEquals("04", "abc", chunks[0]);
		assertEquals("05", "def", chunks[1]);

		s = "abc" + delim + "def" + delim;
		chunks = StringUtil.split(s, delim);
		assertEquals("06", 2, chunks.length);
		assertEquals("07", "abc", chunks[0]);
		assertEquals("08", "def", chunks[1]);

		s = "abc" + delim + "def" + delim + "g";
		chunks = StringUtil.split(s, delim);
		assertEquals("09", 3, chunks.length);
		assertEquals("10", "abc", chunks[0]);
		assertEquals("11", "def", chunks[1]);
		assertEquals("12", "g", chunks[2]);

		s = delim;
		chunks = StringUtil.split(s, delim);
		assertEquals("13", 1, chunks.length);

		s = delim + "abc";
		chunks = StringUtil.split(s, delim);
		assertEquals("14", 2, chunks.length);
		assertEquals("15", "", chunks[0]);
		assertEquals("16", "abc", chunks[1]);

		s = delim + delim + delim;
		chunks = StringUtil.split(s, delim);
		assertEquals("17", 3, chunks.length);
		assertEquals("18", "", chunks[0]);
		assertEquals("19", "", chunks[1]);
		assertEquals("20", "", chunks[2]);

	}

}
