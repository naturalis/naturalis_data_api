package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * An {@link OutputStream} that compresses data written to it according to the
 * zip file format. Contrary to a regulat {@link ZipOutputStream}, this class
 * lets you write multiple zip entries in parallel.
 * 
 * @author Ayco Holleman
 *
 */
public class RandomEntryZipOutputStream extends OutputStream {

	private static final int DEFAULT_SWAP_TRESHOLD = 1024 * 1024;

	private HashMap<String, OutputStream> streams;
	private String mainEntry;
	private ZipOutputStream zipStream;
	private OutputStream active;

	public RandomEntryZipOutputStream(OutputStream out, String mainEntry) throws IOException
	{
		this.mainEntry = mainEntry;
		zipStream = new ZipOutputStream(out);
		zipStream.putNextEntry(new ZipEntry(mainEntry));
		streams = new HashMap<>();
		streams.put(mainEntry, zipStream);
		active = zipStream;
	}

	public RandomEntryZipOutputStream(OutputStream out) throws IOException
	{
		zipStream = new ZipOutputStream(out);
		streams = new HashMap<>();
	}

	public void addEntry(String name) throws IOException
	{
		addEntry(name, DEFAULT_SWAP_TRESHOLD);
	}

	public void addEntry(String name, int size) throws IOException
	{
		if (streams.containsKey(name)) {
			throw new IOException("Duplicate zip entry: " + name);
		}
		CompressedSwapFileOutputStream bucket = new CompressedSwapFileOutputStream(size);
		streams.put(name, bucket);
	}

	public void setActiveEntry(String name) throws IOException
	{
		OutputStream bucket = streams.get(name);
		if (bucket == null) {
			bucket = new CompressedSwapFileOutputStream(DEFAULT_SWAP_TRESHOLD);
			streams.put(name, bucket);
		}
		active = bucket;
	}

	@Override
	public void write(int b) throws IOException
	{
		checkActive();
		active.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		checkActive();
		active.write(b, off, len);
	}

	@Override
	public void flush() throws IOException
	{
		for (OutputStream stream : streams.values()) {
			stream.flush();
		}
	}

	public ZipOutputStream mergeEntries() throws IOException
	{
		CompressedSwapFileOutputStream bucket;
		for (Map.Entry<String, OutputStream> stream : streams.entrySet()) {
			if (mainEntry != null && stream.getKey().equals(mainEntry)) {
				continue;
			}
			ZipEntry zipEntry = new ZipEntry(stream.getKey());
			zipStream.putNextEntry(zipEntry);
			bucket = (CompressedSwapFileOutputStream) stream.getValue();
			bucket.collect(zipStream, true);
			bucket.cleanUpAndClose();
		}
		return zipStream;
	}

	@Override
	public void close() throws IOException
	{
		String msg = "Not supported. Call close on ZipOutputStream produced by merge method";
		throw new IOException(msg);
	}

	private void checkActive() throws IOException
	{
		if (active == null) {
			throw new IOException("No active zip entry");
		}
	}
}
