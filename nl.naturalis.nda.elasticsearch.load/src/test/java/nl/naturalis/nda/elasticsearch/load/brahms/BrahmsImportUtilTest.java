package nl.naturalis.nda.elasticsearch.load.brahms;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class BrahmsImportUtilTest {

	@BeforeClass
	public static void init()
	{
	}

	@Test
	public void testGetDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String year = "2012";
		String month = "6";
		String day = "12";
		Date date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null", date);
		assertEquals("20120612", sdf.format(date));

		year = "";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNull("Date should be null when year is empty", date);
		
		year = "2012";
		month = "89";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null with out-of-bounds months numbers", date);
		assertEquals("20120112", sdf.format(date));
		
		year = "2012";
		month = "-5";
		day = "12";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null with out-of-bounds months numbers", date);
		assertEquals("20120112", sdf.format(date));
		
		year = "2012";
		month = "02";
		day = "31";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null with out-of-bounds months numbers (feb!)", date);
		assertEquals("20120302", sdf.format(date));
	}

}
