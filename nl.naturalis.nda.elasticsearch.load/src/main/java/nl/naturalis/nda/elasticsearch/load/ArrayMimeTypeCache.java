package nl.naturalis.nda.elasticsearch.load;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipInputStream;

import org.domainobject.util.IOUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.DecimalFormat;

public class ArrayMimeTypeCache extends AbstractMimeTypeCache {

	private static final Logger logger = LoggerFactory.getLogger(ArrayMimeTypeCache.class);
	private static final int DEFAULT_MAX_ENTRIES = 10000000;
	private static final String SYSPROP_PACK = "mimetypecache.pack";

	private static final Comparator<String[]> ENTRY_COMPARATOR = new Comparator<String[]>() {
		@Override
		public int compare(String[] o1, String[] o2)
		{
			return o1[0].compareTo(o2[0]);
		}
	};

	private static final Comparator<String[]> NULL_SAFE_ENTRY_COMPARATOR_ = new Comparator<String[]>() {
		@Override
		public int compare(String[] o1, String[] o2)
		{
			if (o1 == null) {
				return o2 == null ? 0 : 1;
			}
			return o2 == null ? -1 : o1[0].compareTo(o2[0]);
		}
	};

	private String[][] cache;


	ArrayMimeTypeCache(String cacheFileName)
	{
		super(cacheFileName);
	}


	@Override
	protected int buildCache(File cacheFile)
	{
		String[][] tempCache = createTempCache();
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

	private static final String[] scratchpad = new String[2];


	@Override
	protected String getEntry(String unitID)
	{
		scratchpad[0] = unitID;
		int index = Arrays.<String[]> binarySearch(cache, scratchpad, ENTRY_COMPARATOR);
		return cache[index][1];
	}


	@Override
	protected void addEntry(String unitID, String mimeType)
	{
		//TODO
	}


	@Override
	protected void saveCache(File cacheFile) throws IOException
	{
		//TODO
	}


	@Override
	protected void closeCache() throws IOException
	{
		cache = null;
	}


	private static String[][] pack(String[][] tempCache, int shrinkToSize)
	{
		if (packCache()) {
			logger.info("Packing cache (increases performance, but eats more memory)");
			String fmt = "To disable packing, extend JAVA_OPTS in include.sh: -D%s=false";
			logger.info(String.format(fmt, SYSPROP_PACK));
			String[][] cache = new String[shrinkToSize][];
			System.arraycopy(tempCache, 0, cache, 0, shrinkToSize);
			double d = ((double) (cache.length * 100)) / ((double) tempCache.length);
			String pct = new DecimalFormat("0.00").format(d);
			logger.info(String.format("Cache shrunk by %s%%", pct));
			return cache;
		}
		return tempCache;
	}


	private static void sort(String[][] cache)
	{
		logger.info("Sorting cache");
		if (packCache()) {
			Arrays.<String[]> sort(cache, ENTRY_COMPARATOR);
		}
		else {
			Arrays.<String[]> sort(cache, NULL_SAFE_ENTRY_COMPARATOR_);
		}
		logger.info("Sort completed");
	}


	private static boolean packCache()
	{
		return StringUtil.isTrue(System.getProperty(SYSPROP_PACK, "1"));
	}


	private static int loadCacheFile(File cacheFile, String[][] cache) throws IOException
	{
		logger.info("Loading cache file");
		LineNumberReader lnr = null;
		ZipInputStream zis = null;
		int numEntries = 0;
		try {
			zis = new ZipInputStream(new FileInputStream(cacheFile));
			zis.getNextEntry();
			InputStreamReader isr = new InputStreamReader(zis, UTF_8);
			lnr = new LineNumberReader(isr, 1024 * 4);
			String unitID;
			String mimeType;
			while ((unitID = lnr.readLine()) != null) {
				mimeType = lnr.readLine();
				if (mimeType == null) {
					throw new RuntimeException("Unexpected end of cache file");
				}
				cache[numEntries++] = new String[] { unitID, mimeType };
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


	private static String[][] createTempCache()
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
		return new String[maxEntries][];
	}

}
