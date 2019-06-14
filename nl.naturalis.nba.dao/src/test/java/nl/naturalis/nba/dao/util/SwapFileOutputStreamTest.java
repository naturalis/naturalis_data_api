package nl.naturalis.nba.dao.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import nl.naturalis.nba.dao.util.SwapFileOutputStream;
import nl.naturalis.nba.utils.ArrayUtil;

public class SwapFileOutputStreamTest {

	@Test
	public void testSwapFileOutputStreamFile_01() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file);
		assertTrue("01", file.isFile());
		sfos.close();
	}

	@Test
	public void testSwapFileOutputStreamFile_02() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos1 = new SwapFileOutputStream(file);
		try {
			SwapFileOutputStream sfos2 = new SwapFileOutputStream(file);
			sfos2.close();
			fail("Expected IOException");
		}
		catch (IOException e) {
			assertTrue("01", e.getMessage().startsWith("Cannot reuse existing swap file"));
		}
		sfos1.close();
	}

	@Test
	public void testCollect_01() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file, 10);
		byte[] data = new byte[] { 3, 7, 1, 5 };
		sfos.write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sfos.collect(baos);
		assertTrue("01", ArrayUtil.deepEquals(data, baos.toByteArray()));
		sfos.close();
	}

	@Test
	public void testCollect_02() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file, 10);
		byte[] data = new byte[] { 3, 7, 1, 5, 2, 2, 4, 0, 0, 1, 3, 5, 6, 7, 7, 7, 4, 5 };
		sfos.write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sfos.collect(baos);
		assertTrue("01", ArrayUtil.deepEquals(data, baos.toByteArray()));
		sfos.close();
	}

	@Test
	public void testCollect_03() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file, 10);
		byte[] data = new byte[] { 3, 7, 1, 5 };
		sfos.write(data);
		sfos.write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sfos.collect(baos);
		byte[] expected = new byte[] { 3, 7, 1, 5, 3, 7, 1, 5 };
		assertTrue("01", ArrayUtil.deepEquals(expected, baos.toByteArray()));
		sfos.close();
	}

	@Test
	public void testCollect_04() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file, 10);
		byte[] data = new byte[] { 3, 7, 1, 5 };
		sfos.write(data);
		sfos.write(data);
		sfos.write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sfos.collect(baos);
		byte[] expected = new byte[] { 3, 7, 1, 5, 3, 7, 1, 5, 3, 7, 1, 5 };
		assertTrue("01", ArrayUtil.deepEquals(expected, baos.toByteArray()));
		sfos.close();
	}

	@Test
	public void testCollect_05() throws IOException
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpDir + "/SwapFileOutputStreamTest.swp");
		if (file.exists()) {
			file.delete();
		}
		SwapFileOutputStream sfos = new SwapFileOutputStream(file, 10);
		byte[] data = new byte[] { 3, 7, 1, 5 };
		sfos.write(data);
		sfos.write(data);
		sfos.write(data);
		sfos.write(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sfos.collect(baos);
		byte[] expected = new byte[] { 3, 7, 1, 5, 3, 7, 1, 5, 3, 7, 1, 5, 3, 7, 1, 5 };
		assertTrue("01", ArrayUtil.deepEquals(expected, baos.toByteArray()));
		sfos.close();
	}
}
