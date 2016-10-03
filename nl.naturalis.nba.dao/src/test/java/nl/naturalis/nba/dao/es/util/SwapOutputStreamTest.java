package nl.naturalis.nba.dao.es.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.domainobject.util.ArrayUtil;
import org.junit.Test;

@SuppressWarnings("static-method")
public class SwapOutputStreamTest {

	@Test
	public void testSwap_01() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 1, 5 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 8);
		sos.write(data);
		sos.swap();
		sos.close();
		byte[] echo = baos.toByteArray();
		assertTrue("01", ArrayUtil.deepEquals(data, echo));
	}

	@Test
	public void testSwap_02() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 1, 5, 6, 8, 0, 2 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		/* Make in-memory buffer smaller than amount of data */
		SwapOutputStream sos = new SwapOutputStream(baos, 3);
		sos.write(data);
		sos.swap();
		byte[] echo = baos.toByteArray();
		assertTrue("01", ArrayUtil.deepEquals(data, echo));
		sos.close();
	}

	@Test
	public void testSwap_03() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 1, 5, 6, 8, 0, 2 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		/* Make in-memory buffer greater than amount of data */
		SwapOutputStream sos = new SwapOutputStream(baos, 64);
		sos.write(data);
		sos.swap();
		byte[] echo = baos.toByteArray();
		assertTrue("01", ArrayUtil.deepEquals(data, echo));
		sos.close();
	}

	@Test
	public void testSize_01() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(7);
		assertEquals("01", 1, sos.size());
		sos.write(4);
		assertEquals("02", 2, sos.size());
		sos.write(6);
		assertEquals("03", 3, sos.size());
		sos.write(6);
		assertEquals("04", 4, sos.size());
		sos.write(5);
		assertEquals("05", 4, sos.size());
		sos.write(3);
		assertEquals("06", 4, sos.size());
		sos.write(2);
		assertEquals("07", 4, sos.size());
		sos.close();
	}

	@Test
	public void testSize_02() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 1 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		assertEquals("01", 3, sos.size());
		sos.write(data);
		assertEquals("02", 3, sos.size());
		sos.close();
	}

	@Test
	public void testHasSwapped_01() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(0);
		assertFalse("01", sos.hasSwapped());
		sos.write(1);
		assertFalse("02", sos.hasSwapped());
		sos.write(2);
		assertFalse("03", sos.hasSwapped());
		sos.write(3);
		assertFalse("04", sos.hasSwapped());
		sos.write(4);
		assertTrue("05", sos.hasSwapped());
		sos.close();
	}

	@Test
	public void testHasSwapped_02() throws IOException
	{
		byte[] data = new byte[] { 3, 7 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		assertFalse("01", sos.hasSwapped());
		sos.write(data);
		assertFalse("02", sos.hasSwapped());
		sos.write(data);
		assertTrue("03", sos.hasSwapped());
		sos.close();
	}

	@Test
	public void testHasSwapped_03() throws IOException
	{
		byte[] data = new byte[] { 3, 7 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		assertFalse("01", sos.hasSwapped());
		sos.write(data);
		assertFalse("02", sos.hasSwapped());
		sos.write(0);
		assertTrue("03", sos.hasSwapped());
		sos.close();
	}

	@Test
	public void testGetBuffer_01() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 0 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		byte[] buffer = sos.getBuffer();
		assertTrue("01", ArrayUtil.deepEquals(data, buffer));
		sos.write(2);
		buffer = sos.getBuffer();
		assertTrue("02", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 2 }, buffer));
		sos.write(7);
		buffer = sos.getBuffer();
		assertTrue("03", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 2 }, buffer));
		sos.write(1);
		buffer = sos.getBuffer();
		assertTrue("04", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 2 }, buffer));
		sos.close();
	}

	@Test
	public void testGetBuffer_02() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 0 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		byte[] buffer = sos.getBuffer();
		assertTrue("01", ArrayUtil.deepEquals(data, buffer));
		sos.write(data);
		buffer = sos.getBuffer();
		assertTrue("02", ArrayUtil.deepEquals(data, buffer));
		sos.close();
	}

	@Test
	public void testWriteBuffer_01() throws IOException
	{
		byte[] data = new byte[] { 3, 7, 0 };
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SwapOutputStream sos = new SwapOutputStream(baos, 4);
		sos.write(data);
		ByteArrayOutputStream bucket = new ByteArrayOutputStream();
		sos.writeBuffer(bucket);
		assertTrue("01", ArrayUtil.deepEquals(data, bucket.toByteArray()));
		sos.write(0);
		bucket = new ByteArrayOutputStream();
		sos.writeBuffer(bucket);
		assertTrue("02", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 0 }, bucket.toByteArray()));
		sos.write(2);
		bucket = new ByteArrayOutputStream();
		sos.writeBuffer(bucket);
		assertTrue("03", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 0 }, bucket.toByteArray()));
		sos.write(5);
		bucket = new ByteArrayOutputStream();
		sos.writeBuffer(bucket);
		assertTrue("04", ArrayUtil.deepEquals(new byte[] { 3, 7, 0, 0 }, bucket.toByteArray()));
		sos.close();
	}

}
