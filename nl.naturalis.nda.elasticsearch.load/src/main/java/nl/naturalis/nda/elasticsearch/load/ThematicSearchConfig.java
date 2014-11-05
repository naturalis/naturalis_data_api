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

	private class Theme {
		String code;
		String file;
		DocumentType type;
		String identifier;
		List<String> ids;
		int matches = 0;
	}

	private static final String SYSPROP_CONFIG_DIR = "ndaConfDir";
	private static final Logger logger = LoggerFactory.getLogger(ThematicSearchConfig.class);
	private static ThematicSearchConfig instance;

	private final ArrayList<Theme> themes = new ArrayList<>();


	public static ThematicSearchConfig getInstance()
	{
		if (instance == null) {
			instance = new ThematicSearchConfig();
		}
		return instance;
	}


	private ThematicSearchConfig()
	{
		loadThemes();
	}


	/**
	 * Establish which theme the specified object is included in.
	 * 
	 * @param id The
	 * @param type
	 * @return
	 */
	public List<String> getThemesForDocument(String id, DocumentType type)
	{
		List<String> identifiers = null;
		for (Theme theme : themes) {
			if (theme.type != type) {
				continue;
			}
			if (Collections.binarySearch(theme.ids, id) >= 0) {
				++theme.matches;
				if (identifiers == null) {
					identifiers = new ArrayList<String>(themes.size());
				}
				identifiers.add(theme.identifier);
			}
		}
		return identifiers;
	}


	public void logMatches()
	{
		for (Theme theme : themes) {
			logger.info("Number of indexed documents for theme \"%s\": %s", theme.identifier, theme.matches);
		}
	}


	private void loadThemes()
	{
		File thematicSearchDir = getThematicSearchDir();
		Properties props = loadConfig(thematicSearchDir);
		if (props != null) {
			for (Object prop : props.keySet()) {
				String s = (String) prop;
				String code = s.substring(0, s.indexOf('.'));
				if (isThemeLoaded(code)) {
					continue;
				}
				logger.info(String.format("Retrieving information for theme \"%s\"", code));
				Theme theme = new Theme();
				themes.add(theme);
				String type = props.getProperty(code + ".type");
				if (type == null) {
					throw new RuntimeException(String.format("Missing property \"%s.type\"", code));
				}
				theme.code = code;
				theme.type = DocumentType.forName(type);
				theme.file = props.getProperty(code + ".file");
				if (theme.file == null || theme.file.length() == 0) {
					theme.file = thematicSearchDir.getAbsolutePath() + "/" + theme.code + ".txt";
				}
				theme.identifier = props.getProperty(code + ".identifier");
				if (theme.identifier == null || theme.identifier.length() == 0) {
					theme.identifier = theme.code;
				}
				loadIdsForTheme(theme);
			}
		}
	}


	private boolean isThemeLoaded(String themeCode)
	{
		for (Theme theme : themes) {
			if (theme.code.equals(themeCode)) {
				return true;
			}
		}
		return false;
	}


	private static void loadIdsForTheme(Theme theme)
	{
		logger.info(String.format("Caching IDs for theme \"%s\"", theme.code));
		File file = new File(theme.file);
		if (!file.isFile()) {
			throw new RuntimeException(String.format("Missing file \"%s\"", file.getAbsolutePath(), theme.code));
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
		logger.info("Number of IDs cached: " + ids.size());
		theme.ids = ids;
	}


	private static Properties loadConfig(File thematicSearchDir)
	{
		logger.info("Loading configuration for thematic search");
		if (!thematicSearchDir.isDirectory()) {
			String fmt = "Missing directory \"%s\". Themes will not be indexed!";
			String msg = String.format(fmt, thematicSearchDir.getAbsolutePath());
			logger.warn(msg);
			return null;
		}
		File propertyFile = new File(thematicSearchDir.getAbsolutePath() + "/thematic-search.properties");
		if (!propertyFile.isFile()) {
			String fmt = "Missing file \"%s\". Themes will not be indexed!";
			String msg = String.format(fmt, propertyFile.getAbsolutePath());
			logger.warn(msg);
			return null;
		}
		logger.info("Configuration file: " + propertyFile.getAbsolutePath());
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
			throw new RuntimeException(msg);
		}
		File dir = new File(confDir);
		if (!dir.isDirectory()) {
			String msg = String.format("Invalid directory specified for system property \"%s\": \"%s\"", SYSPROP_CONFIG_DIR, confDir);
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
