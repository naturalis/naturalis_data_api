package nl.naturalis.nba.dao.es.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.domainobject.util.IOUtil;

/**
 * A {@link SwapOutputStream} that swaps out to a {@link File file}. The
 * {@link FileOutputStream} created from the {@link File} object is already
 * wrapped into a {@link BufferedOutputStream}. Therefore it is pointless to
 * wrap instances of {@code SwapFileOutputStream} into a
 * {@code BufferedOutputStream}.
 * 
 * @author Ayco Holleman
 *
 */
public class SwapFileOutputStream extends SwapOutputStream {

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See {@link SwapOutputStream#SwapOutputStream(OutputStream)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public static SwapFileOutputStream newInstance() throws IOException
	{
		String prefix = SwapFileOutputStream.class.getName().toLowerCase();
		File tempFile = File.createTempFile(prefix, ".swp");
		return new SwapFileOutputStream(tempFile);
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
	public static SwapFileOutputStream newInstance(int treshold) throws IOException
	{
		String prefix = SwapFileOutputStream.class.getName().toLowerCase();
		File tempFile = File.createTempFile(prefix, ".swp");
		return new SwapFileOutputStream(tempFile, treshold);
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See
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
	public static SwapFileOutputStream newInstance(int treshold, int bufSize) throws IOException
	{
		String prefix = SwapFileOutputStream.class.getName().toLowerCase();
		File tempFile = File.createTempFile(prefix, ".swp");
		return new SwapFileOutputStream(tempFile, treshold, bufSize);
	}

	private File swapFile;

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public SwapFileOutputStream(File swapFile) throws IOException
	{
		super(new BufferedOutputStream(new FileOutputStream(swapFile)));
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
	public SwapFileOutputStream(File swapFile, int treshold) throws IOException
	{
		super(new BufferedOutputStream(new FileOutputStream(swapFile)), treshold);
		this.swapFile = swapFile;
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer to the specified
	 * swap file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream, int)}. The
	 * {@code bufSize} argument determines the buffer size of the
	 * {@code BufferedOutputStream} that wraps the {@link FileOutputStream}
	 * created for the swap file (it is passed on to the
	 * {@link BufferedOutputStream#BufferedOutputStream(OutputStream, int)
	 * constructor} of {@code BufferedOutputStream}).
	 * 
	 * @param swapFile
	 * @param bufSize
	 * @param treshold
	 * @throws IOException
	 */
	public SwapFileOutputStream(File swapFile, int treshold, int bufSize) throws IOException
	{
		super(new BufferedOutputStream(new FileOutputStream(swapFile), bufSize), treshold);
		this.swapFile = swapFile;
	}

	/**
	 * Writes all bytes written to this output stream to the specified output
	 * stream. This method is meant to make it transparent to the client whether
	 * or not the in-memory buffer has been swapped out to the swap file. If no
	 * swap has taken place yet, it simply writes the in-memory buffer to the
	 * specified output stream. Otherwise it reads the contents of the swap
	 * file, writes it to the output stream, and deletes the swap file.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void writeAllBytes(OutputStream out) throws IOException
	{
		dest.close();
		if (swapped) {
			FileInputStream fis = new FileInputStream(swapFile);
			IOUtil.pipe(fis, out, 4096);
			fis.close();
		}
		else {
			out.write(buf, 0, cnt);
		}
		swapFile.delete();
	}

}
