package nl.naturalis.nba.etl;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.utils.FileUtil;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * MedialibIdsCache is an ArrayList containing all ids available in the medialibrary.
 * It can be used during the ETL process to check whether an image with a specific id
 * actually exists in the medialibrary.
 *
 */
public class MedialibIdsCache {

    private static final Logger logger = ETLRegistry.getInstance().getLogger(MedialibIdsCache.class);

    private static ArrayList<String> ids = new ArrayList<>();
    private static String CACHE_FILE_NAME = "medialib_ids_cache.zip";

    private static MedialibIdsCache instance;

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

    public static boolean contains(String id) {
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
            logger.info("Finished loading {} ids into cache", ids.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> loadCacheFile(File cacheFile) throws IOException {
        ArrayList<String> ids = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(cacheFile));
        ZipEntry zipEntry = zis.getNextEntry();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(zis))) {
            while (br.ready()) {
                ids.add(br.readLine());
            }
        }
        zis.close();
        return ids;
    }
}
