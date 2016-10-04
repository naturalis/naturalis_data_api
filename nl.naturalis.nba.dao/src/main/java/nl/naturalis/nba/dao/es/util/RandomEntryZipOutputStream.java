package nl.naturalis.nba.dao.es.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An {@link OutputStream} that compresses data written to it according to the
 * zip file format. Contrary to a regular {@link ZipOutputStream}, this class
 * lets you write zip entries in a non-serial fashion. This allows you to
 * populate multiple zip entries while iterating over a single data source.
 * Otherwise you would have to start a new iteration for each zip entry. In
 * general you would create a {@code RandomEntryZipOutputStream} with one zip
 * entry (called the main entry) being compressed and written directly to the
 * underlying output stream while data for the other zip entries is written to
 * intermediate storage. Once you are done writing data for the entries managed
 * by the {@code RandomEntryZipOutputStream}, you call {@link #mergeEntries()}.
 * This will produce a regular {@code ZipOutputStream} that you can use as you
 * see fit (e.g. to start writing zip entries in a serial fashion again). Most
 * notably, {@code RandomEntryZipOutputStream} will not have called
 * {@link ZipOutputStream#close() close} or {@link ZipOutputStream#finish()
 * finish} on the {@code ZipOutputStream}. These tasks are explicitly left to
 * the client. {@code RandomEntryZipOutputStream} internally uses a
 * {@link CompressedSwapFileOutputStream} as intermediate storage for zip
 * entries (for each zip entry a separate {@code CompressedSwapFileOutputStream}
 * is created). This class will try to keep data for a zip entry in memory and
 * swap to a temporary file when the entry grows too big.
 * 
 * @author Ayco Holleman
 *
 */
public class RandomEntryZipOutputStream extends OutputStream {

	/**
	 * The default byte size of the memory buffer created for a zip entry (1
	 * megabyte). Once zip entries grow beyond this size they will be swapped
	 * out to file.
	 */
	public static final int DEFAULT_SWAP_TRESHOLD = 1024 * 1024;

	private static Logger logger = LogManager.getLogger(RandomEntryZipOutputStream.class);

	private HashMap<String, OutputStream> streams;
	private String mainEntry;
	private ZipOutputStream zipStream;
	private OutputStream active;

	/**
	 * Creates a {@code RandomEntryZipOutputStream} with a zip entry that will
	 * be written directly to the specified output stream rather than being held
	 * in intermediate storage.
	 * 
	 * @param out
	 * @param mainEntry
	 * @throws IOException
	 */
	public RandomEntryZipOutputStream(OutputStream out, String mainEntry) throws IOException
	{
		this.mainEntry = mainEntry;
		zipStream = new ZipOutputStream(out);
		zipStream.putNextEntry(new ZipEntry(mainEntry));
		streams = new HashMap<>();
		streams.put(mainEntry, zipStream);
		active = zipStream;
	}

	/**
	 * Creates a {@code RandomEntryZipOutputStream} without any main entry.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public RandomEntryZipOutputStream(OutputStream out) throws IOException
	{
		zipStream = new ZipOutputStream(out);
		streams = new HashMap<>();
	}

	/**
	 * Adds a new zip entry with the specified name. The in-memory buffer for
	 * the entry is swapped to file once it grows beyond
	 * {@link #DEFAULT_SWAP_TRESHOLD}.
	 * 
	 * @param name
	 * @throws IOException
	 */
	public void addEntry(String name) throws IOException
	{
		addEntry(name, DEFAULT_SWAP_TRESHOLD);
	}

	/**
	 * Adds a new zip entry with the specified name. The in-memory buffer for
	 * the entry is swapped to file once it grows beyond the specified
	 * {@code size} (in bytes).
	 * 
	 * @param name
	 * @param size
	 * @throws IOException
	 */
	public void addEntry(String name, int size) throws IOException
	{
		if (streams.containsKey(name)) {
			throw new IOException("Duplicate zip entry: " + name);
		}
		CompressedSwapFileOutputStream bucket = new CompressedSwapFileOutputStream(size);
		streams.put(name, bucket);
	}

	/**
	 * Sets the zip entry for subsequent calls to any of the {@code write}
	 * methods. If the specified entry does not exist, it will be created as
	 * though by calling {@link #addEntry(String) addEntry}.
	 * 
	 * @param name
	 * @throws IOException
	 */
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

	/**
	 * Produces a {@link ZipOutputStream} containing all the zip entries managed
	 * by this {@code RandomEntryZipOutputStream}.
	 * 
	 * @return
	 * @throws IOException
	 */
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
			/*
			 * Unfortunately, ZipOutputStream does not let you write
			 * already-compressed data to it. Therefore we must uncompress the
			 * data in the buckets (passing [true] to bucket.collect) and then
			 * let the ZipOutputStream compress the data again. The alternative
			 * would be to not compress the data in the buckets, but that would
			 * significantly increase the chance that they overflow and thus
			 * start swapping.
			 */
			bucket.collect(zipStream, true);
			bucket.cleanUpAndClose();
			if (bucket.hasSwapped()) {
				String msg = "Data for zip entry \"{}\" could not be retained "
						+ "in memory and was swapped to a temporary file";
				logger.warn(msg);
			}
		}
		return zipStream;
	}

	/**
	 * Deletes all swap files created by the
	 * {@code CompressedSwapFileOutputStream}s maintained by this instance. You
	 * only need to call this method in case an error prevented you from calling
	 * {@link #mergeEntries(). {@code mergeEntries} will also implicitly the
	 * delete the swap files once it is done collecting data for the zip
	 * entries.
	 * 
	 * @throws IOException
	 */
	public void cleanup() throws IOException
	{
		CompressedSwapFileOutputStream bucket;
		for (Map.Entry<String, OutputStream> stream : streams.entrySet()) {
			if (mainEntry != null && stream.getKey().equals(mainEntry)) {
				continue;
			}
			bucket = (CompressedSwapFileOutputStream) stream.getValue();
			bucket.cleanUpAndClose();
		}
	}

	/**
	 * Do not call this method. It will always throw an {@link IOException}.
	 * Call {@link #mergeEntries()} and then perform any book-keeping actions
	 * like {@link ZipOutputStream#close() close} or
	 * {@link ZipOutputStream#finish() finish} on the returned
	 * {@link ZipOutputStream}. If some error situation prevented you from
	 * calling {@link #mergeEntries()}, call {@link #cleanup()}
	 */
	@Override
	public void close() throws IOException
	{
		throw new IOException("Not supported");
	}

	private void checkActive() throws IOException
	{
		if (active == null) {
			throw new IOException("No active zip entry");
		}
	}

}
