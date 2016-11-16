package nl.naturalis.nba.etl;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.IOUtil;

/**
 * Implementation of {@link MimeTypeCache} that uses a sorted array as backbone
 * for the mime type cache. This implementation may be a bit more memory
 * efficient than the {@link MapMimeTypeCache original implementation}, which
 * uses a {@link TreeMap}, but it's also somewhat slower. Therefore it's not the
 * default implementation used by the import programs. The make them use an
 * {@code ArrayMimeTypeCache}, specify {@code -Dmimetypecache.type=array} on the
 * command line.
 * 
 * @author Ayco Holleman
 *
 */
class ArrayMimeTypeCache extends AbstractMimeTypeCache {

	private static final Logger logger = ETLRegistry.getInstance().getLogger(ArrayMimeTypeCache.class);
	private static final int DEFAULT_MAX_ENTRIES = 10000000;
	private static final String SYSPROP_PACK = "mimetypecache.pack";

	private static class Entry {
		int key;
		String unitID;
		final String mimeType;

		Entry(int key, String unitID, String mimeType)
		{
			this.key = key;
			this.unitID = unitID;
			this.mimeType = mimeType;
		}
	}

	private static final Comparator<Entry> ENTRY_COMPARATOR = new Comparator<Entry>() {
		@Override
		public int compare(Entry o1, Entry o2)
		{
			if (o1.key < o2.key) {
				return -1;
			}
			if (o1.key > o2.key) {
				return 1;
			}
			return o1.unitID.compareTo(o2.unitID);
		}
	};

	private Entry[] cache;

	ArrayMimeTypeCache(String cacheFileName)
	{
		super(cacheFileName);
	}

	@Override
	protected int buildCache(File cacheFile)
	{
		Entry[] tempCache = createTempCache();
		int numEntries;
		try {
			numEntries = loadCacheFile(cacheFile, tempCache);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		cache = pack(tempCache, numEntries);
		sort(cache);
		return numEntries;
	}

	private static final Entry scratchpad = new Entry(0, null, null);

	@Override
	protected String getEntry(String unitID)
	{
		scratchpad.key = unitID.hashCode();
		scratchpad.unitID = unitID;
		int index = Arrays.<Entry> binarySearch(cache, scratchpad, ENTRY_COMPARATOR);
		if (index == -1) {
			return null;
		}
		return cache[index].mimeType;
	}

	@Override
	protected void addEntry(String unitID, String mimeType)
	{
		// TODO
	}

	@Override
	protected void saveCache(File cacheFile) throws IOException
	{
		// TODO
	}

	@Override
	protected void closeCache() throws IOException
	{
		cache = null;
	}

	private static Entry[] pack(Entry[] tempCache, int shrinkToSize)
	{
		logger.info("Packing cache (increases performance, but eats more memory)");
		String fmt = "To disable packing, extend JAVA_OPTS in include.sh: -D%s=false";
		logger.info(String.format(fmt, SYSPROP_PACK));
		Entry[] cache = new Entry[shrinkToSize];
		System.arraycopy(tempCache, 0, cache, 0, shrinkToSize);
		double d = ((double) (cache.length * 100)) / ((double) tempCache.length);
		String pct = new DecimalFormat("0.00").format(d);
		logger.info(String.format("Cache shrunk by %s%%", pct));
		return cache;
	}

	private static void sort(Entry[] cache)
	{
		logger.info("Sorting cache");
		Arrays.<Entry> sort(cache, ENTRY_COMPARATOR);
		if (logger.isDebugEnabled()) {
			int collissions = 0;
			for (int i = 1; i < cache.length; ++i) {
				if (cache[i].key == cache[i - 1].key && (!cache[i].unitID.equals(cache[i - 1].unitID))) {
					++collissions;
					String fmt = "Hash collition for UnitIDs \"%s\" and \"%s\"";
					logger.debug(String.format(fmt, cache[i].unitID, cache[i - 1].unitID));
				}
			}
			logger.debug("Number of hash collitions: " + collissions);
		}
		logger.info("Sort completed");
	}

	private static int loadCacheFile(File cacheFile, Entry[] cache) throws IOException
	{
		logger.info("Loading cache file");
		LineNumberReader lnr = null;
		ZipInputStream zis = null;
		int numEntries = 0;
		try {
			zis = new ZipInputStream(new FileInputStream(cacheFile));
			zis.getNextEntry();
			InputStreamReader isr = new InputStreamReader(zis, UTF_8);
			lnr = new LineNumberReader(isr, READ_BUFFER_SIZE);
			String unitID;
			String mimeType;
			while ((unitID = lnr.readLine()) != null) {
				mimeType = lnr.readLine();
				if (mimeType == null) {
					throw new RuntimeException("Unexpected end of cache file");
				}
				mimeType = mimeType.equals(JPEG) ? JPEG : mimeType.intern();
				cache[numEntries++] = new Entry(unitID.hashCode(), unitID, mimeType);
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			String msg = "Number of entries in cache file exceeded maximum number of entries";
			throw new RuntimeException(msg);
		}
		finally {
			IOUtil.close(lnr);
			IOUtil.close(zis);
		}
		return numEntries;
	}

	private static Entry[] createTempCache()
	{
		logger.info("Reserving memory");
		String propName = "mimetypecache.maxentries";
		String propVal = System.getProperty(propName);
		int maxEntries = propVal == null ? DEFAULT_MAX_ENTRIES : Integer.valueOf(propVal);
		logger.info("Setting maximum number of entries in mimetype cache to " + maxEntries);
		if (propVal == null) {
			String fmt = "To change this, extend JAVA_OPTS in include.sh: -D%s=<integer>";
			logger.info(String.format(fmt, propName));
		}
		return new Entry[maxEntries];
	}

}
