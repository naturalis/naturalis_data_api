package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThematicSearchConfig {

	private class ThematicCollection {
		String code;
		String file;
		String type;
		String identifier;
		List<String> ids;
	}

	private static final String SYSPROP_CONFIG_DIR = "nl.naturalis.nda.conf.dir";
	private static final Logger logger = LoggerFactory.getLogger(ThematicSearchConfig.class);
	private static ThematicSearchConfig instance;

	private final ArrayList<ThematicCollection> collections = new ArrayList<>();


	public static ThematicSearchConfig getInstance()
	{
		if (instance == null) {
			instance = new ThematicSearchConfig();
		}
		return instance;
	}


	private ThematicSearchConfig()
	{
		loadCollections();
	}


	public List<String> getCollectionsIdentifiersForObject(String objectId, String objectType)
	{
		List<String> identifiers = new ArrayList<String>(collections.size());
		for (ThematicCollection collection : collections) {
			if (!collection.type.equalsIgnoreCase(objectType)) {
				continue;
			}
			if (Collections.binarySearch(collection.ids, objectId) >= 0) {
				identifiers.add(collection.identifier);
			}
		}
		return identifiers.size() == 0 ? null : identifiers;
	}


	private void loadCollections()
	{
		File thematicSearchDir = getThematicSearchDir();
		Properties props = loadConfig(thematicSearchDir);
		for (Object prop : props.keySet()) {
			String s = (String) prop;
			String collectionCode = s.substring(0, s.indexOf('.'));
			if (isCollectionLoaded(collectionCode)) {
				continue;
			}
			ThematicCollection map = new ThematicCollection();
			collections.add(map);
			String type = props.getProperty(collectionCode + ".type");
			if (type == null) {
				throw new RuntimeException(String.format("Missing property \"%s.type\"", collectionCode));
			}
			map.code = collectionCode;
			map.type = type;
			map.file = props.getProperty(collectionCode + ".file");
			map.identifier = props.getProperty(collectionCode + ".identifier");
			loadIdsForCollection(map, thematicSearchDir);
		}
	}


	private boolean isCollectionLoaded(String collectionCode)
	{
		for (ThematicCollection collection : collections) {
			if (collection.code.equals(collectionCode)) {
				return true;
			}
		}
		return false;
	}


	private static void loadIdsForCollection(ThematicCollection collection, File thematicSearchDir)
	{
		String fileName = collection.file;
		if (fileName == null) {
			fileName = thematicSearchDir.getAbsolutePath() + "/" + collection.code + ".txt";
		}
		File file = new File(fileName);
		if (!file.isFile()) {
			String fmt = "Missing file \"%s\". Thematic search information for collection \"%s\" will not be indexed";
			logger.error(String.format(fmt, file.getAbsolutePath(), collection.code));
		}
		List<String> ids = new ArrayList<String>(255);
		try {
			FileReader fr = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr);
			String line;
			while ((line = lnr.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				ids.add(line);
			}
			lnr.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		Collections.sort(ids);
		collection.ids = ids;
	}


	private static Properties loadConfig(File thematicSearchDir)
	{
		if (!thematicSearchDir.isDirectory()) {
			String fmt = "Missing directory \"%s\". Thematic search information will not be indexed";
			String msg = String.format(fmt, thematicSearchDir.getAbsolutePath());
			logger.warn(msg);
			return null;
		}
		File propertyFile = new File(thematicSearchDir.getAbsolutePath() + "/thematic-search.properties");
		Properties props = new Properties();
		try (FileReader fr = new FileReader(propertyFile)) {
			props.load(fr);
			return props;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private static File getThematicSearchDir()
	{
		String confDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (confDir == null) {
			String msg = String.format("Missing system property \"%s\"", SYSPROP_CONFIG_DIR);
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		File dir = new File(confDir);
		if (!dir.isDirectory()) {
			String msg = String.format("Invalid directory specified for system property \"%s\": \"%s\"", SYSPROP_CONFIG_DIR, confDir);
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		try {
			return new File(dir.getAbsolutePath() + "/thematic-search").getCanonicalFile();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
