package nl.naturalis.nba.dao.es;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.dao.es.exception.InitializationException;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESTaxon;
import nl.naturalis.nba.dao.es.types.ESType;

/**
 * Class providing centralized access to core services such as logging and
 * elasticsearch. If anything goes wrong while configuring those services an
 * {@link InitializationException} is thrown and it probably doesn't make much
 * sense to let the program continue. Therefore one of the first things an
 * import program should do is retrieve an instance of the {@code Registry}
 * class.
 * 
 * @author Ayco Holleman
 *
 */
public class Registry {

	/**
	 * Name of the main configuration file: "nba.properties".
	 */
	public static final String CONFIG_FILE_NAME = "nba.properties";
	/**
	 * Name of the system property pointing to the configuration directory:
	 * "nba.v2.conf.dir". This directory must at least contain nba.properties.
	 */
	public static final String SYSPROP_CONFIG_DIR = "nba.v2.conf.dir";

	protected static Registry instance;

	private File confDir;
	private ConfigObject config;
	private ESClientFactory clientFactory;

	@SuppressWarnings("unused")
	private Logger logger = getLogger(getClass());

	/**
	 * Returns a {@code Registry} instance.
	 * 
	 * @return A {@code Registry} instance.
	 */
	public static Registry getInstance()
	{
		if (instance == null) {
			instance = new Registry();
		}
		return instance;
	}

	protected Registry()
	{
		setConfDir();
		loadConfig();
		setupLogging();
	}

	/**
	 * Get a {@link ConfigObject} for the main configuration file
	 * (nba.properties).
	 * 
	 * @return
	 */
	public ConfigObject getConfig()
	{
		return config;
	}

	/**
	 * Get the directory designated to contain the application's configuration
	 * files. This directory must be specified by a system property named
	 * "nba.v2.conf.dir". This directory must contain at least nba.properties,
	 * and may contain additional files that the application expects to be
	 * there.
	 * 
	 * @return
	 */
	public File getConfDir()
	{
		return confDir;
	}

	/**
	 * Returns a file with a path relative to the configuration directory.
	 * 
	 * @param relativePath
	 *            The path of the file relative to the configuration directory.
	 * @return
	 */
	public File getFile(String relativePath)
	{
		return FileUtil.newFile(confDir, relativePath);
	}

	/**
	 * Get a logger for the specified class.
	 * 
	 * @param cls
	 * @return
	 */
	public Logger getLogger(Class<?> cls)
	{
		return LogManager.getLogger(cls);
	}

	/**
	 * Returns a factory for Elasticsearch clients.
	 * 
	 * @return
	 */
	public ESClientFactory getESClientFactory()
	{
		if (clientFactory == null) {
			clientFactory = ESClientFactory.getInstance(config);
		}
		return clientFactory;
	}

	/**
	 * Returns the names of the Elasticsearch index storing the specified type.
	 * 
	 * @param type
	 * @return
	 */
	public String getIndex(Class<? extends ESType> type)
	{
		return config.get("elasticsearch.index.default");
	}

	/**
	 * Returns the name of the Elasticsearch type corresponding to the specified
	 * Java type.
	 * 
	 * @param type
	 * @return
	 */
	public String getType(Class<? extends ESType> type)
	{
		// TODO: soft code
		if (type == ESSpecimen.class)
			return "Specimen";
		if (type == ESTaxon.class)
			return "Taxon";
		if (type == ESMultiMediaObject.class)
			return "MultiMediaObject";
		assert (false);
		return null;
	}

	private void setConfDir()
	{
		String path = System.getProperty(SYSPROP_CONFIG_DIR);
		if (path == null) {
			String msg = String.format("Missing system property \"%s\"", SYSPROP_CONFIG_DIR);
			throw new InitializationException(msg);
		}
		File dir = new File(path);
		if (!dir.isDirectory()) {
			String msg = String.format(
					"Invalid value for system property \"%s\": \"%s\" (no such directory)",
					SYSPROP_CONFIG_DIR, path);
			throw new InitializationException(msg);
		}
		try {
			confDir = dir.getCanonicalFile();
		}
		catch (IOException e) {
			throw new InitializationException(e);
		}
	}

	/*
	 * Currently, nothing special happens here, but this might change in the
	 * future.
	 */
	private void setupLogging()
	{
		logger = getLogger(getClass());
	}

	private void loadConfig()
	{
		File file = FileUtil.newFile(confDir, CONFIG_FILE_NAME);
		if (!file.isFile()) {
			String msg = String.format("Configuration file missing: %s", file.getPath());
			throw new InitializationException(msg);
		}
		this.config = new ConfigObject(file);
	}

}
