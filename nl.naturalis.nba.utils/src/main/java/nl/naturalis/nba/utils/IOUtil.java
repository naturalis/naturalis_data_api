package nl.naturalis.nba.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

	/**
	 * Null-save closing of one or more {@code Closeable}s. Closes the specified
	 * {@code Closeable}s and wraps any {@code IOException} into a
	 * {@code RuntimeException}. If a {@code Closeable} is {@code null}, it is
	 * ignored.
	 * 
	 * @param closeable
	 */
	public static void close(Closeable... closeables)
	{
		for (Closeable c : closeables) {
			if (c != null) {
				try {
					c.close();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Reads all bytes from the specified input stream and writes. Bytes are
	 * read in and flushed out in chunks of {@code bufsize} length. Neither the
	 * input stream nor the output stream is closed when done.
	 * 
	 * @param is
	 * @param out
	 * @param bufsize
	 */
	public static void pipe(InputStream is, OutputStream out, int bufsize)
	{
		if (!(is instanceof BufferedInputStream || is instanceof ByteArrayInputStream)) {
			is = new BufferedInputStream(is, bufsize);
		}
		if (!(out instanceof BufferedOutputStream || out instanceof ByteArrayOutputStream)) {
			out = new BufferedOutputStream(out, bufsize);
		}
		byte[] data = new byte[bufsize];
		try {
			int n = is.read(data, 0, data.length);
			while (n != -1) {
				out.write(data, 0, n);
				out.flush();
				n = is.read(data, 0, data.length);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readAllBytes(InputStream is)
	{
		return readAllBytes(is, 2048);
	}

	public static byte[] readAllBytes(InputStream is, int bufsize)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bufsize);
		InputStream is2;
		if (is instanceof BufferedInputStream || is instanceof ByteArrayInputStream)
			is2 = is;
		else
			is2 = new BufferedInputStream(is, bufsize);
		byte[] data = new byte[bufsize];
		try {
			int n = is2.read(data, 0, data.length);
			while (n != -1) {
				baos.write(data, 0, n);
				n = is2.read(data, 0, data.length);
			}
			return baos.toByteArray();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
