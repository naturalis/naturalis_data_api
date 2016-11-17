package nl.naturalis.nba.dao;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.exception.InitializationException;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.FileUtil;

/**
 * Class providing centralized access to common resources and services for the
 * DAO module and dependent modules.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoRegistry {

	/**
	 * Name of the main NBA configuration file (&#34;nba.properties&#34;).
	 */
	public static final String CONFIG_FILE_NAME = "nba.properties";
	/**
	 * Name of the system property pointing to the configuration directory
	 * (&#34;nba.v2.conf.dir&#34;). This directory must at least contain
	 * nba.properties, but may contain additional configuration-related
	 * resources.
	 */
	public static final String SYSPROP_CONFIG_DIR = "nba.v2.conf.dir";

	protected static DaoRegistry instance;

	private File cfgDir;
	private File cfgFile;
	private ConfigObject config;

	private Logger logger = getLogger(getClass());

	/**
	 * Returns a {@code DaoRegistry} instance.
	 * 
	 * @return A {@code DaoRegistry} instance.
	 */
	public static DaoRegistry getInstance()
	{
		if (instance == null) {
			instance = new DaoRegistry();
		}
		return instance;
	}

	private DaoRegistry()
	{
		setConfDir();
		loadConfig();
		setupLogging();
	}

	/**
	 * Returns a {@link ConfigObject} for the main configuration file
	 * (nba.properties).
	 * 
	 * @return
	 */
	public ConfigObject getConfiguration()
	{
		return config;
	}

	/**
	 * Returns the directory designated to contain the application's
	 * configuration files. This directory must be specified by a system
	 * property named "nba.v2.conf.dir". This directory must contain at least
	 * nba.properties, and may contain additional files and folders that the
	 * application expects to be there.
	 * 
	 * @return
	 */
	public File getConfigurationDirectory()
	{
		return cfgDir;
	}

	/**
	 * Returns a {@link File} object for the main configuration file
	 * (nba.properties).
	 * 
	 * @return
	 */
	public File getConfigurationFile()
	{
		return cfgFile;
	}

	/**
	 * Returns a {@link File} object for the specified path. The path is assumed
	 * to be relative to the NBA configuration directory. See
	 * {@link #getConfigurationDirectory() getConfigurationDirectory}.
	 * 
	 * @param relativePath
	 *            The path of the file relative to the configuration directory.
	 * @return
	 */
	public File getFile(String relativePath)
	{
		return FileUtil.newFile(cfgDir, relativePath);
	}

	/**
	 * Get a logger for the specified class. All classes should use this method
	 * to get hold of a logger in stead of calling
	 * {@code LogManager.getLogger()} directly.
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("static-method")
	public Logger getLogger(Class<?> cls)
	{
		/*
		 * Currently we just forward the call to the LogManager, but logging
		 * being the configuration nightmare that it is, that might change in
		 * the future.
		 */
		return LogManager.getLogger(cls);
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
			String fmt = "Invalid value for system property \"%s\": \"%s\" (no such directory)";
			String msg = String.format(fmt, SYSPROP_CONFIG_DIR, path);
			throw new InitializationException(msg);
		}
		try {
			cfgDir = dir.getCanonicalFile();
			logger.info("NBA configuration directory: " + cfgDir.getPath());
		}
		catch (IOException e) {
			throw new InitializationException(e);
		}
	}

	private void setupLogging()
	{
		logger = getLogger(getClass());
	}

	private void loadConfig()
	{
		cfgFile = FileUtil.newFile(cfgDir, CONFIG_FILE_NAME);
		logger.info("NBA configuration file: " + cfgFile.getPath());
		if (!cfgFile.isFile()) {
			String msg = String.format("Missing configuration file: %s", cfgFile.getPath());
			throw new InitializationException(msg);
		}
		this.config = new ConfigObject(cfgFile);
	}

}
