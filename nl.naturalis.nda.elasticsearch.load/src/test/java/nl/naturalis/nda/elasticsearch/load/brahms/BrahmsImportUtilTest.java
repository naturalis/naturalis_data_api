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
	public void testGetDate_happy_path()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String year = "2012";
		String month = "6";
		String day = "12";
		Date date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null", date);
		assertEquals("20120612", sdf.format(date));
	}

	@Test
	public void testGetDate_year_zero()
	{
		String year = "0";
		String month = "6";
		String day = "12";
		Date date = BrahmsImportUtil.getDate(year, month, day);
		assertNull("Date should be null when year is zero", date);
		year = "";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNull("Date should be null when year is empty", date);
	}

	@Test
	public void testGetDate_month_zero()
	{
		String year = "2012";
		String month = "0";
		String day = "12";
		Date date = BrahmsImportUtil.getDate(year, month, day);
		assertNull("Date should be null when month is zero", date);
		month = "";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNull("Date should be null when month is empty", date);
	}

	@Test
	public void testGetDate_day_zero()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String year = "2012";
		String month = "3";
		String day = "0";
		Date date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null when day is zero", date);
		assertEquals("20120301", sdf.format(date));
		day = "";
		date = BrahmsImportUtil.getDate(year, month, day);
		assertNotNull("Date should not be null when day is empty", date);
		assertEquals("20120301", sdf.format(date));
	}


}
