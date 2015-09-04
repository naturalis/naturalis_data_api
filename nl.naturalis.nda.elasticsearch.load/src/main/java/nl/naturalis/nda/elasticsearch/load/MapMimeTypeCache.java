package nl.naturalis.nda.elasticsearch.load;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

/**
 * Implementation of {@link MimeTypeCache} that uses a {@link TreeMap} as
 * backbone for the mime type cache.
 * 
 * @author Ayco Holleman
 * @created Aug 5, 2015
 *
 */
public class MapMimeTypeCache extends AbstractMimeTypeCache {

	private static final Logger logger = Registry.getInstance().getLogger(MapMimeTypeCache.class);

	private TreeMap<String, String> cache;


	MapMimeTypeCache(String cacheFileName)
	{
		super(cacheFileName);
	}


	protected int buildCache(File cacheFile)
	{
		cache = new TreeMap<>();
		LineNumberReader lnr = null;
		ZipInputStream zis = null;
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
				cache.put(unitID, mimeType);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			IOUtil.close(lnr,zis);
		}
		return cache.size();
	}


	@Override
	protected void addEntry(String unitID, String mimeType)
	{
		cache.put(unitID, mimeType);
	}


	@Override
	protected String getEntry(String unitID)
	{
		return cache.get(unitID);
	}


	@Override
	protected void saveCache(File cacheFile) throws IOException
	{
		if (cacheFile.isFile()) {
			if (!cacheFile.delete()) {
				throw new IOException("Failed to delete " + cacheFile.getAbsolutePath());
			}
		}
		logger.info("Saving mime type cache to: " + cacheFile.getAbsolutePath());
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)));
			ZipEntry zipEntry = new ZipEntry("mimetypes");
			zos.putNextEntry(zipEntry);
			for (Map.Entry<String, String> entry : cache.entrySet()) {
				zos.write(entry.getKey().getBytes(UTF_8));
				zos.write(NEWLINE_BYTES);
				zos.write(entry.getValue().getBytes(UTF_8));
				zos.write(NEWLINE_BYTES);
			}
		}
		finally {
			IOUtil.close(zos);
		}
	}


	@Override
	protected void closeCache() throws IOException
	{
		cache = null;
	}

}
