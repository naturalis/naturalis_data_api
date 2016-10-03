package nl.naturalis.nba.dao.es.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that first fills up an internal buffer before flushing it
 * and switching over to an underlying output stream. In other words, first all
 * write actions operate on an in-memory buffer. Then, once a write action
 * causes the buffer to fill up or overflow, the buffer is flushed to the
 * underlying output stream, and all subsequent write actions are simply
 * forwarded to the underlying output stream. This class is not meant to provide
 * buffering functionality like a {@link BufferedOutputStream}. Instead, you
 * would use it in situations where you hope or expect that the in-memory buffer
 * will never be swapped out to the underlying output stream. The underlying
 * outputstream is used by way of fall-back, in case the in-memory buffer does
 * flow over, and would most likely write to persistent storage (like a
 * {@link FileOutputStream}). A {@code SwapOutputStream} has no way of
 * collecting all data written to it once the in-memory buffer has flown over.
 * It is up to subclasses of {@code SwapOutputStream} to provide this
 * functionality.
 * 
 * @author Ayco Holleman
 *
 */
public class SwapOutputStream extends OutputStream {

	/**
	 * The buffer to which data is written until the treshold is reached.
	 */
	protected byte buf[];
	/**
	 * The output stream to which data will be written once the treshold has
	 * been reached.
	 */
	protected OutputStream out;
	/**
	 * The number of valid bytes in the buffer.
	 */
	protected int cnt;
	/**
	 * Whether or not the number of bytes written to this
	 * {@code SwapOutputStream} has reached the treshold.
	 */
	protected boolean swapped;

	/**
	 * Creates a {@code SwapOutputStream} that swaps its in-memory buffer to the
	 * specified {@code OutputStream} once the in-memory buffer grows beyond 64
	 * kilobytes.
	 * 
	 * @param destination
	 */
	public SwapOutputStream(OutputStream destination)
	{
		this(destination, 64 * 1024);
	}

	/**
	 * Creates a {@code SwapOutputStream} that swaps its in-memory buffer to the
	 * specified {@code OutputStream} once the in-memory buffer grows beyond the
	 * specified treshold.
	 * 
	 * @param destination
	 *            The output stream to which data is written after the treshold
	 *            has been reached
	 * @param treshold
	 *            The byte size of the in-memory buffer
	 */
	public SwapOutputStream(OutputStream destination, int treshold)
	{
		this.out = destination;
		this.buf = new byte[treshold];
	}

	@Override
	public void write(int b) throws IOException
	{
		if (swapped) {
			out.write((byte) b);
		}
		else if (cnt < buf.length) {
			buf[cnt] = (byte) b;
			cnt += 1;
		}
		else {
			swapped = true;
			out.write(buf);
			out.write((byte) b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		if (swapped) {
			out.write(b, off, len);
		}
		else if (cnt + len <= buf.length) {
			System.arraycopy(b, off, buf, cnt, len);
			cnt += len;
		}
		else {
			swapped = true;
			out.write(buf, 0, cnt);
			out.write(b, off, len);
		}
	}

	/**
	 * Calls {@code flush()} on the underlying {@link OutputStream} if the swap
	 * has taken place. Otherwise calling this method has no effect.
	 */
	@Override
	public void flush() throws IOException
	{
		if (swapped) {
			out.flush();
		}
	}

	/**
	 * Forces a premature swap, that is, even if the in-memory buffer has not
	 * reached full capacity yet. If the in-memory buffer has already been
	 * swapped out to the underlying outputstream (either implicitly because of
	 * a buffer overflow or explicitly because of a previous call to
	 * {@code swap()}), calling this method has no effect.
	 * 
	 * @throws IOException
	 */
	public void swap() throws IOException
	{
		if (swapped) {
			return;
		}
		swapped = true;
		out.write(buf, 0, cnt);
	}

	/**
	 * Calls {@code close()} on the underlying output stream. Note that this
	 * will <i>not</i> induce an implicit swap. If the in-memory buffer has not
	 * overflown yet, the output stream will be closed without a single byte
	 * written to it.
	 */
	@Override
	public void close() throws IOException
	{
		out.close();
	}

	/**
	 * Returns the number of bytes in the in-memory buffer.
	 * 
	 * @return
	 */
	public int size()
	{
		return cnt;
	}

	/**
	 * Returns the contents of the in-memory buffer.
	 * 
	 * @return
	 */
	public byte[] getBuffer()
	{
		byte[] copy = new byte[cnt];
		System.arraycopy(buf, 0, copy, 0, cnt);
		return copy;
	}

	/**
	 * Writes the contents of the in-memory buffer to the specified output
	 * stream. Note that if the buffer has overflown and bytes have been written
	 * to the underlying output stream, this method still only writes out the
	 * bytes in the buffer.
	 * 
	 * @param destination
	 * @throws IOException
	 */
	public void writeBuffer(OutputStream destination) throws IOException
	{
		destination.write(buf, 0, cnt);
	}

	/**
	 * Whether or not the number of bytes written to this
	 * {@code SwapOutputStream} has reached the treshold.
	 */
	public boolean hasSwapped()
	{
		return swapped;
	}

}
