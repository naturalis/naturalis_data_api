package nl.naturalis.nba.dao.util;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.utils.IOUtil;

/**
 * A {@link SwapOutputStream} that swaps to file. The {@link FileOutputStream}
 * created from the {@link File} object is already wrapped into a
 * {@link BufferedOutputStream}, so there's no advantage to be had from wrapping
 * instances of this class into a {@code BufferedOutputStream}.
 * 
 * @author Ayco Holleman
 *
 */
public class SwapFileOutputStream extends SwapOutputStream {

	private static final Logger logger = getLogger(SwapFileOutputStream.class);

	/**
	 * Creates a new instance that swaps its in-memory buffer an auto-generated
	 * temp file. See {@link #SwapFileOutputStream(File)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public static SwapFileOutputStream newInstance() throws IOException
	{
		return new SwapFileOutputStream(tempFile());
	}

	/**
	 * Creates a new instance that swaps its in-memory buffer to an
	 * auto-generated temp file. See {@link #SwapFileOutputStream(File, int)}.
	 * 
	 * @param treshold
	 * @return
	 * @throws IOException
	 */
	public static SwapFileOutputStream newInstance(int treshold) throws IOException
	{
		return new SwapFileOutputStream(tempFile(), treshold);
	}

	private File swapFile;
	private boolean closed;

	/**
	 * Creates a new instance that swaps its in-memory buffer to an
	 * auto-generated temp file. See
	 * {@link SwapOutputStream#SwapOutputStream(OutputStream)}.
	 * 
	 * @param swapFile
	 * @throws IOException
	 */
	public SwapFileOutputStream(File swapFile) throws IOException
	{
		super(streamTo(swapFile, 512));
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
		super(streamTo(swapFile, 512), treshold);
		this.swapFile = swapFile;
	}

	/**
	 * Collects all bytes written to this {@code SwapFileOutputStream} to the
	 * specified output stream. This method makes it transparent to the client
	 * whether the in-memory buffer has been swapped to file or not. If no swap
	 * has taken place yet, it writes the in-memory buffer to the specified
	 * output stream. Otherwise it reads the contents of the swap file and
	 * writes it to the output stream. If you call {@code collect()} after the
	 * in-memory buffer has been swapped out to the swap file, this method
	 * implicitly closes the {@link FileOutputStream} created for the swap file
	 * and any subsequent write action will cause an IOException.
	 * 
	 * @param bucket
	 * @throws IOException
	 */
	public void collect(OutputStream bucket) throws IOException
	{
		if (swapped) {
			if (!swapFile.exists()) {
				String msg = "Swap file no longer exists: " + swapFile.getPath();
				throw new IOException(msg);
			}
			close();
			FileInputStream fis = new FileInputStream(swapFile);
			IOUtil.pipe(fis, bucket, 2048);
			fis.close();
		}
		else {
			bucket.write(buf, 0, cnt);
		}
	}

	@Override
	public void close() throws IOException
	{
		if (!closed) {
			out.close();
			closed = true;
		}
	}

	/**
	 * Deletes the swap file and closes the {@code SwapFileOutputStream}.
	 * 
	 * @throws IOException
	 */
	public void cleanupAndClose() throws IOException
	{
		close();
		if (swapFile.exists()) {
			long size = swapFile.length() / 1024;
			logger.info("Deleting swap file {} ({} kB)", swapFile.getPath(), size);
			swapFile.delete();
		}
	}

	private static OutputStream streamTo(File swapFile, int bufSize) throws IOException
	{
		if (swapFile.exists()) {
			String msg = "Cannot reuse existing swap file: " + swapFile.getPath();
			throw new IOException(msg);
		}
		FileOutputStream fos = new FileOutputStream(swapFile);
		return new BufferedOutputStream(fos, bufSize);
	}

	/*
	 * NB Creating a temp file using File.createTempFile is not satisfactory as
	 * the swap files may be created in rapid succession while
	 * File.createTempFile seems to use System.currentTimeMillis() to invent a
	 * file name.
	 */
	private static File tempFile()
	{
		StringBuilder sb = new StringBuilder(64);
		sb.append(System.getProperty("java.io.tmpdir"));
		sb.append('/');
		sb.append(System.identityHashCode(new Object()));
		sb.append('.');
		sb.append(System.currentTimeMillis());
		sb.append('.');
		sb.append(SwapFileOutputStream.class.getSimpleName().toLowerCase());
		sb.append(".swp");
		return new File(sb.toString());
	}
}
