package nl.naturalis.nba.rest.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.utils.ConfigObject;

public class NDA {

	public static final String SYSPROP_CONFIG_DIR = "nl.naturalis.nda.conf.dir";
	public static final String SESSION_ID_PARAM = "_SESSION_ID";
	
	private static final String CONFIG_FILE_NAME = "nda.properties";

	private static final Logger logger = LogManager.getLogger(NDA.class);

	private final ConfigObject config;


	public NDA()
	{
		config = loadConfig();
	}




	public String getIndexName()
	{
		return config.required("elasticsearch.index.name");
	}


	public ConfigObject getConfig()
	{
		return config;
	}


	private static ConfigObject loadConfig()
	{
		String confDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (confDir != null) {
			logger.debug("Using system property \"" + SYSPROP_CONFIG_DIR + "\" to locate configuration file " + CONFIG_FILE_NAME);
			File dir = new File(confDir);
			if (!dir.isDirectory()) {
				throw new RuntimeException(String.format("Invalid directory specified for property \"%s\": \"%s\"", SYSPROP_CONFIG_DIR, confDir));
			}
			try {
				File file = new File(dir.getCanonicalPath() + "/" + CONFIG_FILE_NAME);
				if (!file.isFile()) {
					throw new RuntimeException(String.format("Configuration file missing: %s", file.getCanonicalPath()));
				}
				logger.debug(String.format("Using configuration file %s", file.getCanonicalPath()));
				return new ConfigObject(file);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		logger.debug("Searching classpath for configuration file " + CONFIG_FILE_NAME);
		try (InputStream is = NDA.class.getResourceAsStream("/" + CONFIG_FILE_NAME)) {
			if (is == null) {
				throw new RuntimeException(String.format("Configuration file missing: %s", CONFIG_FILE_NAME));
			}
			return new ConfigObject(is);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
