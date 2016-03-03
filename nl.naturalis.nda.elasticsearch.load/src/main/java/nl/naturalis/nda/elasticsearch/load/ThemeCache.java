package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import nl.naturalis.nda.domain.SourceSystem;

import org.slf4j.Logger;

/**
 * A cache that maps UnitIDs to "themes". Themes are predefined categories of
 * specimens, for example "Extinct Birds". If a specimen is an extinct bird, its
 * UnitID is associated with that theme. A specimen can belong to more than one
 * theme.
 * 
 * @author Ayco Holleman
 *
 */
public class ThemeCache {

	private class Theme {

		String code;
		String file;
		String identifier;
		List<DocumentType> types;
		List<String> ids;
		List<SourceSystem> systems;
		int matches = 0;
	}

	private static final String SYSPROP_CONFIG_DIR = "ndaConfDir";
	private static final Logger logger = Registry.getInstance().getLogger(ThemeCache.class);
	private static ThemeCache instance;

	private final ArrayList<Theme> themes = new ArrayList<>();

	/**
	 * Returns a {@code ThemeCache} object.
	 * 
	 * @return
	 */
	public static ThemeCache getInstance()
	{
		if (instance == null) {
			instance = new ThemeCache();
		}
		return instance;
	}

	private ThemeCache()
	{
		loadThemes();
	}

	/**
	 * Returns the themes that the object (specimen, multimedia object, etc.)
	 * with the specified id belongs to.
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<String> lookup(String id, DocumentType type, SourceSystem system)
	{
		if (id == null) {
			return null;
		}
		List<String> identifiers = null;
		for (Theme theme : themes) {
			if (type != null && theme.types != null && !theme.types.contains(type))
				continue;
			if (system != null && theme.systems != null && !theme.systems.contains(system))
				continue;
			if (Collections.binarySearch(theme.ids, id) >= 0) {
				if (logger.isDebugEnabled()) {
					String fmt = "Found match for ID %s in theme %s (%s)";
					String msg = String.format(fmt, id, theme.code, theme.file);
					logger.debug(msg);
				}
				++theme.matches;
				if (identifiers == null) {
					identifiers = new ArrayList<>(2);
				}
				identifiers.add(theme.identifier);
			}
		}
		return identifiers;
	}

	public void resetMatchCounters()
	{
		for (Theme theme : themes) {
			theme.matches = 0;
		}
	}

	public void logMatchInfo()
	{
		for (Theme theme : themes) {
			logger.info(String.format("Number of indexed documents for theme \"%s\": %s",
					theme.code, theme.matches));
		}
	}

	private void loadThemes()
	{
		File thematicSearchDir = getThematicSearchDir();
		Properties props = loadConfig(thematicSearchDir);
		if (props != null) {
			for (Object prop : props.keySet()) {
				String s = (String) prop;
				int x = s.indexOf('.');
				String code = x == -1 ? s : s.substring(0, x);
				if (isThemeLoaded(code)) {
					continue;
				}
				logger.info(String.format("Retrieving information for theme \"%s\"", code));
				Theme theme = new Theme();
				themes.add(theme);
				theme.code = code;
				String type = props.getProperty(code + ".type");
				if (type != null && type.length() != 0) {
					String[] types = type.split(",");
					List<DocumentType> documentTypes = new ArrayList<>(types.length);
					for (String t : types) {
						documentTypes.add(DocumentType.forName(t.trim()));
					}
					theme.types = documentTypes;
				}
				theme.file = props.getProperty(code + ".file");
				if (theme.file == null || theme.file.length() == 0) {
					theme.file = thematicSearchDir.getAbsolutePath() + "/" + theme.code + ".txt";
				}
				theme.identifier = props.getProperty(code + ".identifier");
				if (theme.identifier == null || theme.identifier.length() == 0) {
					theme.identifier = theme.code;
				}
				String systemProperty = props.getProperty(code + ".systems");
				if (systemProperty != null && systemProperty.trim().length() != 0) {
					String[] systemCodes = systemProperty.split(",");
					theme.systems = new ArrayList<>(systemCodes.length);
					for (String systemCode : systemCodes) {
						switch (systemCode.trim().toUpperCase()) {
							case "CRS":
								theme.systems.add(SourceSystem.CRS);
								break;
							case "BRAHMS":
								theme.systems.add(SourceSystem.BRAHMS);
								break;
							case "NSR":
								theme.systems.add(SourceSystem.NSR);
								break;
							case "COL":
								theme.systems.add(SourceSystem.COL);
								break;
							default: {
								String fmt = "Unknown system in \"%s.systems\": \"%s\" (allowed: CRS, BRAHMS, NSR, COL)";
								throw new ETLRuntimeException(String.format(fmt, code, systemCode));
							}
						}
					}
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
			String fmt = "Missing file \"%s\"";
			String msg = String.format(fmt, file.getAbsolutePath(), theme.code);
			throw new ETLRuntimeException(msg);
		}
		List<String> ids = new ArrayList<>(1000);
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
			throw new ETLRuntimeException(e);
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
		File propertyFile = new File(thematicSearchDir.getAbsolutePath()
				+ "/thematic-search.properties");
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
			throw new ETLRuntimeException(e);
		}
	}

	private static File getThematicSearchDir()
	{
		String confDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (confDir == null) {
			String msg = String.format("Missing system property \"%s\"", SYSPROP_CONFIG_DIR);
			throw new ETLRuntimeException(msg);
		}
		File dir = new File(confDir);
		if (!dir.isDirectory()) {
			String fmt = "Invalid directory specified for system property \"%s\": \"%s\"";
			String msg = String.format(fmt, SYSPROP_CONFIG_DIR, confDir);
			throw new ETLRuntimeException(msg);
		}
		try {
			return new File(dir.getAbsolutePath() + "/thematic-search").getCanonicalFile();
		}
		catch (IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

}
