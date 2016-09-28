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
 * underlying output stream, and from that moment all write actions are forwared
 * to that output stream. This class is not meant to provide buffering
 * functionality like a {@link BufferedOutputStream}. Instead, you would use it
 * in situations where you hope or expect that the swap never takes places and
 * all write actions take place in-memory. The underlying outputstream is only
 * used by way of fall-back, in case the in-memory buffer does flow over, and
 * would most likely write to persistent storage (like a
 * {@link FileOutputStream}).
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
	 * The output stream to which data is written once the treshold has been
	 * reached.
	 */
	protected OutputStream dest;
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
	 * specified {@code OutputStream} once the in-memory buffer grows beyond 128
	 * kilobytes.
	 * 
	 * @param destination
	 */
	public SwapOutputStream(OutputStream destination)
	{
		this(destination, 128 * 1024);
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
		this.dest = destination;
		this.buf = new byte[treshold];
	}

	@Override
	public void write(int b) throws IOException
	{
		if (swapped) {
			dest.write((byte) b);
		}
		else if (cnt < buf.length) {
			buf[cnt] = (byte) b;
			cnt += 1;
		}
		else {
			swapped = true;
			dest.write(buf);
			dest.write((byte) b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		if (swapped) {
			dest.write(b, off, len);
		}
		else if (cnt + len <= buf.length) {
			System.arraycopy(b, off, buf, cnt, len);
			cnt += len;
		}
		else {
			swapped = true;
			dest.write(buf, 0, cnt);
			dest.write(b, off, len);
		}
	}

	/**
	 * Calls {@code flush()} on the swap-to {@link OutputStream} <i>if</i> the
	 * swap has taken place. Otherwise calling this method has no effect.
	 */
	@Override
	public void flush() throws IOException
	{
		if (swapped) {
			dest.flush();
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
		dest.write(buf, 0, cnt);
	}

	/**
	 * Calls {@code close()} on the swap-to output stream. Note that this will
	 * not induce an implicit swap. If the swap has not taken place yet, the
	 * output stream will be closed without a single byte written to it. Call
	 * {@link #swapAndClose()} to first swap and then close the output stream.
	 */
	@Override
	public void close() throws IOException
	{
		dest.close();
	}

	/**
	 * Flushes in-memory buffer to the swap-to output stream and then closes the
	 * output stream.
	 * 
	 * @throws IOException
	 */
	public void swapAndClose() throws IOException
	{
		swap();
		dest.close();
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
	 * Whether or not the number of bytes written to this
	 * {@code SwapOutputStream} has reached the treshold.
	 */
	public boolean hasSwapped()
	{
		return swapped;
	}

}
