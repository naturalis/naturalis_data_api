package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;

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
	 * specified {@code OutputStream} once the in-memory buffer grows beyond 1
	 * MB.
	 * 
	 * @param swapTo
	 */
	public SwapOutputStream(OutputStream swapTo)
	{
		this(swapTo, 1024 * 1024);
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
			dest.write(b);
		}
		else if (cnt + 1 < buf.length) {
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
		else if (cnt + len < buf.length) {
			System.arraycopy(b, off, buf, cnt, len);
			cnt += len;
		}
		else {
			swapped = true;
			dest.write(buf);
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
	 * reached its full capacity yet.
	 * 
	 * @throws IOException
	 */
	public void swap() throws IOException
	{
		if (swapped) {
			throw new IOException("Already swapped");
		}
		swapped = true;
		dest.write(buf);
	}

	/**
	 * Calls {@code close()} on the swap-to {@link OutputStream}. Note that this
	 * will <i>not</i> induce an implicit swap. If there is still data in the
	 * in-memory buffer, the {@code OutputStream} will be closed without a
	 * single byte written to it. Call {@link #swapAndClose()} to first swap and
	 * then close the {@code OutputStream}.
	 */
	@Override
	public void close() throws IOException
	{
		dest.close();
	}
	
	/**
	 * Writes the in-memory buffer to the swap-to {@link OutputStream} and
	 * closes the swap-to {@link OutputStream}.
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
	public int bytesInBuffer()
	{
		return cnt;
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
