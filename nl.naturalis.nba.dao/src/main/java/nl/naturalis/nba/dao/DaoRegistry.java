package nl.naturalis.nba.dao;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.dao.exception.InitializationException;

/**
 * Class providing centralized access to common resources and services for the
 * DAO module and dependent modules.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoRegistry {

	/**
	 * Name of the main configuration file: &#34;nba.properties&#34;.
	 */
	public static final String CONFIG_FILE_NAME = "nba.properties";
	/**
	 * Name of the system property pointing to the configuration directory:
	 * &#34;nba.v2.conf.dir&#34;. This directory must at least contain
	 * nba.properties.
	 */
	public static final String SYSPROP_CONFIG_DIR = "nba.v2.conf.dir";
	/**
	 * Name of the system property pointing to the configuration directory for
	 * integration tests: &#34;nba-test.v2.conf.dir&#34;. This directory must at
	 * least contain nba.properties. If this property is present it takes
	 * precedence over {@link #SYSPROP_CONFIG_DIR}.
	 */
	public static final String SYSPROP_CONFIG_DIR_TEST = "nba-test.v2.conf.dir";

	protected static DaoRegistry instance;

	private File cfgDir;
	private File cfgFile;
	private ConfigObject config;

	@SuppressWarnings("unused")
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

	protected DaoRegistry()
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
	 * Returns a {@link File} instance for the main configuration file
	 * (nba.properties). If this method is called from within a unit test, this
	 * file will be in {@link #SYSPROP_CONFIG_DIR_TEST}, otherwise the file be
	 * in {@link #SYSPROP_CONFIG_DIR}.
	 * 
	 * @return
	 */
	public File getConfigurationFile()
	{
		return cfgFile;
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
		String usedProperty = SYSPROP_CONFIG_DIR_TEST;
		String path = System.getProperty(usedProperty);
		if (path == null) {
			usedProperty = SYSPROP_CONFIG_DIR;
			path = System.getProperty(usedProperty);
			if (path == null) {
				String msg = String.format("Missing system property \"%s\"", usedProperty);
				throw new InitializationException(msg);
			}
		}
		File dir = new File(path);
		if (!dir.isDirectory()) {
			String fmt = "Invalid value for system property \"%s\": \"%s\" (no such directory)";
			String msg = String.format(fmt, usedProperty, path);
			throw new InitializationException(msg);
		}
		try {
			cfgDir = dir.getCanonicalFile();
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
		if (!cfgFile.isFile()) {
			String msg = String.format("Configuration file missing: %s", cfgFile.getPath());
			throw new InitializationException(msg);
		}
		this.config = new ConfigObject(cfgFile);
	}

}