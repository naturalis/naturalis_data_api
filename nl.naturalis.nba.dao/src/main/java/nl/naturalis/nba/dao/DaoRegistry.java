package nl.naturalis.nba.dao;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.dao.exception.InitializationException;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * Class providing centralized access to common resources and services for the DAO module and
 * dependent modules.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoRegistry {

  /*
   * The system property specifying the full path to the main configuration file (usually named
   * nba.properties)
   */
  private static final String SYSPROP_CONF_FILE = "nba.conf.file";

  private static DaoRegistry instance;

  private final File cfgFile;
  private final ConfigObject config;

  private Logger logger = getLogger(getClass());

  /**
   * Returns a {@code DaoRegistry} instance.
   * 
   * @return A {@code DaoRegistry} instance.
   */
  public static DaoRegistry getInstance() {
    if (instance == null) {
      instance = new DaoRegistry();
    }
    return instance;
  }

  private DaoRegistry() {
    String path = System.getProperty(SYSPROP_CONF_FILE);
    if (path == null) {
      String msg = String.format("Missing system property: %s", SYSPROP_CONF_FILE);
      throw new InitializationException(msg);
    }
    cfgFile = new File(path);
    if (!cfgFile.isFile()) {
      String msg = String.format("Missing configuration file: %s", cfgFile.getPath());
      throw new InitializationException(msg);
    }
    logger.info("NBA configuration file: " + cfgFile.getPath());
    config = new ConfigObject(cfgFile);
    String encoding = System.getProperty("file.encoding");
    if (encoding == null || !encoding.equals("UTF-8")) {
      String msg = "NBA boot failure. Java VM must be started with "
          + "-Dfile.encoding=UTF-8. When running within Widlfly,"
          + "this system property can also be set within the "
          + "<system-properties> XML element of standalone.xml." + "Actual character encoding is: "
          + encoding;
      logger.fatal(msg);
      throw new InitializationException(msg);
    }
  }

  /**
   * Returns a {@link ConfigObject} for the main configuration file (nba.properties).
   * 
   * @return
   */
  public ConfigObject getConfiguration() {
    return config;
  }

  /**
   * Returns the top directory of the various assets used by the application. Currently this always
   * is the directory containing nba.properties.
   * 
   * @return
   */
//  public File getConfigurationDirectory() {
//    return config.getDirectory("nba.api.install.dir");
//  }

  public File getEtlInstallDirectory() {
    return config.getDirectory("nba.etl.install.dir");
  }

  /**
   * Returns a {@link File} object for the main configuration file (nba.properties).
   * 
   * @return
   */
  public File getConfigurationFile() {
    return cfgFile;
  }

  /**
   * Returns a {@link File} object for the specified path. The path is assumed to be relative to the
   * NBA configuration directory. See {@link #getConfigurationDirectory()
   * getConfigurationDirectory}.
   * 
   * @param relativePath The path of the file relative to the configuration directory.
   * @return
   */
//  public File getFile(String relativePath) {
//    return FileUtil.newFile(getConfigurationDirectory(), relativePath);
//  }

  /**
   * Get a logger for the specified class. All classes should use this method to get hold of a
   * logger in stead of calling {@code LogManager.getLogger()} directly.
   * 
   * @param cls
   * @return
   */
  @SuppressWarnings("static-method")
  public Logger getLogger(Class<?> cls) {
    /*
     * Currently we just forward the call to the LogManager, but logging being the configuration
     * nightmare that it is, that might change in the future.
     */
    return LogManager.getLogger(cls);
  }

}
