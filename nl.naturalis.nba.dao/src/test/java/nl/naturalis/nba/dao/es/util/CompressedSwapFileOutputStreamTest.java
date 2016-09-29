package nl.naturalis.nba.dao.es.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import org.domainobject.util.ArrayUtil;
import org.junit.Test;

@SuppressWarnings("static-method")
public class CompressedSwapFileOutputStreamTest {

	@Test
	public void testCompressedSwapFileOutputStream() throws IOException
	{
		File f = new File("/tmp/CompressedSwapFileOutputStreamTest-01.swp");
		if (f.isFile()) {
			f.delete();
		}
		CompressedSwapFileOutputStream csfos = new CompressedSwapFileOutputStream(f);
		assertTrue(f.isFile());
		f.delete();
		csfos.close();
	}

	@Test
	public void testUncompress_01() throws IOException
	{
		File f = new File("/tmp/CompressedSwapFileOutputStreamTest-01.swp");
		if (f.isFile()) {
			f.delete();
		}
		CompressedSwapFileOutputStream csfos = new CompressedSwapFileOutputStream(f, 3);
		byte[] data = new byte[] { 3, 4, 5, 6 };
		csfos.write(data);
//		assertFalse("01", csfos.hasSwapped());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		csfos.uncompress(baos);
		assertTrue("02", ArrayUtil.deepEquals(data, baos.toByteArray()));
		f.delete();
		csfos.close();
	}

	@Test
	public void testUncompress_02() throws IOException
	{
		File f = new File("/tmp/CompressedSwapFileOutputStreamTest-01.swp");
		if (f.isFile()) {
			f.delete();
		}
		CompressedSwapFileOutputStream csfos = new CompressedSwapFileOutputStream(f, 8);
		byte[] data = new byte[] { 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 1, 1, 1, 1, 1 };
		csfos.write(data);
		assertTrue("01", csfos.hasSwapped());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		csfos.uncompress(baos);
		assertTrue("02", ArrayUtil.deepEquals(data, baos.toByteArray()));
		f.delete();
		csfos.close();
	}
}
