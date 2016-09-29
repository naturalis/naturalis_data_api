package nl.naturalis.nba.dao.es.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import org.domainobject.util.IOUtil;

/**
 * A {@link SwapOutputStream} that swaps out to a {@link File file}. A
 * {@link DeflaterOutputStream} is used to compress the data. The
 * {@link FileOutputStream} created from the {@link File} object is already
 * wrapped into a {@link BufferedOutputStream}. Therefore it is pointless to
 * wrap instances of {@code SwapFileOutputStream} into a
 * {@code BufferedOutputStream}.
 * 
 * @author Ayco Holleman
 *
 */
public class CompressedSwapFileOutputStream extends SwapOutputStream {

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See {@link SwapOutputStream#SwapOutputStream(OutputStream)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public static CompressedSwapFileOutputStream newInstance() throws IOException
	{
		return new CompressedSwapFileOutputStream(tempFile());
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}.
	 * 
	 * @param treshold
	 * @return
	 * @throws IOException
	 */
	public static CompressedSwapFileOutputStream newInstance(int treshold) throws IOException
	{
		return new CompressedSwapFileOutputStream(tempFile(), treshold);
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer to an
	 * auto-generated temp file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}. The
	 * {@code bufSize} argument determines the buffer size of the
	 * {@code BufferedOutputStream} that wraps the {@link FileOutputStream}
	 * created for the swap file (it is passed on to the
	 * {@link BufferedOutputStream#BufferedOutputStream(OutputStream, int)
	 * constructor} of {@code BufferedOutputStream}).
	 * 
	 * @param bufSize
	 * @param treshold
	 * @return
	 * @throws IOException
	 */
	public static CompressedSwapFileOutputStream newInstance(int treshold, int bufSize)
			throws IOException
	{
		return new CompressedSwapFileOutputStream(tempFile(), treshold, bufSize);
	}

	private File swapFile;

	/**
	 * Creates a new instance that swaps its in-memory buffer to an
	 * auto-generated temp file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public CompressedSwapFileOutputStream(File swapFile) throws IOException
	{
		super(stream(swapFile, 256));
		this.swapFile = swapFile;
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer to the specified
	 * swap file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}.
	 * 
	 * @param swapFile
	 * @param treshold
	 * @throws IOException
	 */
	public CompressedSwapFileOutputStream(File swapFile, int treshold) throws IOException
	{
		super(stream(swapFile, 256), treshold);
		this.swapFile = swapFile;
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer to the specified
	 * swap file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}. The
	 * {@code treshold} argument determines the size of the in-memory buffer.
	 * {@code bufSize} argument determines the buffer size of the
	 * {@code BufferedOutputStream} that wraps the {@link FileOutputStream}
	 * created for the swap file. In other words, once the in-memory buffer
	 * overflows, we switch to a {@code BufferedOutputStream} wrapping a
	 * {@code FileOutputStream} that writes to the swap file.
	 * 
	 * @param swapFile
	 * @param bufSize
	 * @param treshold
	 * @throws IOException
	 */
	public CompressedSwapFileOutputStream(File swapFile, int treshold, int bufSize)
			throws IOException
	{
		super(stream(swapFile, bufSize), treshold);
		this.swapFile = swapFile;
	}

	/**
	 * Writes all bytes written to this output stream to the specified output
	 * stream and deletes the swap file. This method is meant to make it
	 * transparent to the client whether or not the in-memory buffer has been
	 * swapped out to the swap file. If the buffer did not overflow, it simply
	 * writes the buffer to the specified output stream. Otherwise it writes the
	 * contents of the swap file to the output stream. Note that this method
	 * writes the compressed data to the output stream. Use
	 * {@link #uncompress(OutputStream) uncompress} to write the uncompressed
	 * data to the output stream.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void finish(OutputStream out) throws IOException
	{
		dest.close();
		if (swapped) {
			FileInputStream in = new FileInputStream(swapFile);
			IOUtil.pipe(in, out, 2048);
			in.close();
		}
		else {
			out.write(buf, 0, cnt);
		}
		swapFile.delete();
	}

	/**
	 * Uncompresses the data written to this instance and then writes it to the
	 * specified output stream. See {@link #finish(OutputStream)}.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void uncompress(OutputStream out) throws IOException
	{
		finish(new InflaterOutputStream(out, new Inflater(true)));
	}

	private static OutputStream stream(File swapFile, int bufSize) throws IOException
	{
		if (swapFile.exists()) {
			String msg = "Cannot reuse existing swap file: " + swapFile.getAbsolutePath();
			throw new IOException(msg);
		}
		FileOutputStream fos = new FileOutputStream(swapFile);
		Deflater def = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		DeflaterOutputStream dos = new DeflaterOutputStream(fos, def);
		return new BufferedOutputStream(dos, bufSize);
	}

	private static File tempFile() throws IOException
	{
		String prefix = CompressedSwapFileOutputStream.class.getName().toLowerCase();
		return File.createTempFile(prefix, ".swp");
	}

}
