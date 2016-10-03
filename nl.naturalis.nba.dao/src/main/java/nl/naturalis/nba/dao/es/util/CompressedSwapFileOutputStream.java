package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class CompressedSwapFileOutputStream extends DeflaterOutputStream {

	public CompressedSwapFileOutputStream() throws IOException
	{
		super(SwapFileOutputStream.newInstance());
	}

	public CompressedSwapFileOutputStream(int treshold) throws IOException
	{
		super(SwapFileOutputStream.newInstance(treshold));
	}

	public void collect(OutputStream destination) throws IOException
	{
		collect(destination, false);
	}

	public void collect(OutputStream destination, boolean uncompress) throws IOException
	{
		close();
		SwapFileOutputStream sfos = (SwapFileOutputStream) out;
		if (uncompress) {
			sfos.collect(new InflaterOutputStream(destination));
		}
		else {
			sfos.collect(destination);
		}
	}

	public void cleanUpAndClose() throws IOException
	{
		close();
		SwapFileOutputStream sfos = (SwapFileOutputStream) out;
		sfos.cleanupAndClose();
	}

}
