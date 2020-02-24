package nl.naturalis.nba.etl;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.utils.FileUtil;
import org.apache.logging.log4j.Logger;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipInputStream;

/**
 * MedialibIdsCache is an ArrayList containing all ids available in the medialibrary.
 * It can be used during the ETL process to check whether an image with a specific id
 * actually exists in the medialibrary.
 *
 */
public class MedialibIdsCache {

    private static final Logger logger = ETLRegistry.getInstance().getLogger(MedialibIdsCache.class);
    private static final String CACHE_FILE_NAME = "medialib_ids_cache.zip";
    private static Set<String> ids;
    private static MedialibIdsCache instance;

    /**
     * MedialibIdsCache provides an ArrayList containing all the ids available in
     * the Naturalis Media Library. The {@link #contains(String)} method provides
     * a simple way to check whether a image with the specified id exists in the
     * media library.
     *
     * NOTE: when the cache cannot be loaded, the {@link #contains(String)} method
     * will always return true.
     */
    private MedialibIdsCache() {
        loadIds();
    }

    public static MedialibIdsCache getInstance()
    {
        if (instance == null) {
            instance = new MedialibIdsCache();
        }
        return instance;
    }

    /**
     * contains(String id) provides a way to check if the given id is available from the medialibrary.
     * @param id The id (String) that needs to be verified
     * @return true when the id is availalble in the cache; false otherwise
     */
    public static boolean contains(String id) {
        if (ids == null) return true;
        return ids.contains(id);
    }

    private void loadIds() {
        File dir = DaoRegistry.getInstance().getConfiguration().getDirectory("medialib.data.dir");
        File cacheFile = FileUtil.newFile(dir, CACHE_FILE_NAME);
        if (!cacheFile.isFile()) {
            String fmt = "Missing medialib ids cache file (%s). You should put it in %s.";
            throw new ETLRuntimeException(String.format(fmt, CACHE_FILE_NAME, dir.getAbsolutePath()));
        }
        logger.info("Initializing MedialibIdsCache");
        try {
            ids = loadCacheFile(cacheFile);
            if (ids != null) {
                logger.info("Finished loading {} ids into cache", ids.size());
            } else {
                logger.info("Medialib ids cache is empty. Medialib ids will NOT be checked!");
            }
        } catch (IOException e) {
            logger.info("Unable to load medialib ids cache. Cache file ({}) was not available.", CACHE_FILE_NAME);
        }
    }

    private static Set<String> loadCacheFile(File cacheFile) throws IOException {
        Set<String> ids = new TreeSet<>();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(cacheFile));
        try (zis; BufferedReader br = new BufferedReader(new InputStreamReader(zis))) {
            zis.getNextEntry();
            while (br.ready()) {
                ids.add(br.readLine());
            }
        } catch (OutOfMemoryError e) {
            logger.error("Cache file is too large. Not using Medialib ids cache!");
            return null;
        }
        return ids;
    }
}
