package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SprayableZipOutputStream extends OutputStream {

	private HashMap<String, OutputStream> streams;

	private String mainEntry;
	private ZipOutputStream zip;

	private OutputStream active;

	public SprayableZipOutputStream(OutputStream out, String mainEntry) throws IOException
	{
		this.mainEntry = mainEntry;
		zip = new ZipOutputStream(out);
		zip.putNextEntry(new ZipEntry(mainEntry));
		streams = new HashMap<>();
		streams.put(mainEntry, zip);
	}

	public SprayableZipOutputStream(OutputStream out) throws IOException
	{
		zip = new ZipOutputStream(out);
		streams = new HashMap<>();
	}

	public void addEntry(String name) throws IOException
	{
		if (streams.containsKey(name)) {
			throw new IOException("Duplicate zip entry: " + name);
		}
		CompressedSwapFileOutputStream bucket = CompressedSwapFileOutputStream.newInstance();
		streams.put(name, bucket);
	}

	public void addEntry(String name, int size) throws IOException
	{
		if (streams.containsKey(name)) {
			throw new IOException("Duplicate zip entry: " + name);
		}
		CompressedSwapFileOutputStream bucket = CompressedSwapFileOutputStream.newInstance(size);
		streams.put(name, bucket);
	}

	public void setActiveEntry(String name) throws IOException
	{
		OutputStream bucket = streams.get(name);
		if (bucket == null) {
			bucket = CompressedSwapFileOutputStream.newInstance();
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
		checkActive();
		active.flush();
	}

	@Override
	public void close() throws IOException
	{
		CompressedSwapFileOutputStream bucket;
		for (Map.Entry<String, OutputStream> stream : streams.entrySet()) {
			if (mainEntry != null && stream.getKey().equals(mainEntry)) {
				continue;
			}
			ZipEntry zipEntry = new ZipEntry(stream.getKey());
			/*
			 * We trick the ZipOutputStream into treating this entry as an
			 * uncompressed entry. Then we pour in the compressed data from the
			 * CompressedSwapFileOutputStream.
			 */
			zipEntry.setMethod(ZipEntry.STORED);
			zip.putNextEntry(zipEntry);
			bucket = (CompressedSwapFileOutputStream) stream.getValue();
			bucket.writeAllBytes(zip);
			/* And now we tell it the truth */
			zipEntry.setMethod(ZipEntry.DEFLATED);
		}
	}

	private void checkActive() throws IOException
	{
		if (active == null) {
			throw new IOException("No active zip entry");
		}
	}
}
