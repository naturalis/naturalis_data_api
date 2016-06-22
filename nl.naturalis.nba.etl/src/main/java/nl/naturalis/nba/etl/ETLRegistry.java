package nl.naturalis.nba.etl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

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
public class ETLRegistry {

	/*
	 * System property that we are going to set and that tells log4j how to name
	 * the log file.
	 */
	private static final String SYSPROP_ETL_LOGFILE = "nba.v2.etl.logfile";

	private static ETLRegistry instance;

	@SuppressWarnings("unused")
	private Logger logger;

	/**
	 * Return a {@code Registry} instance. Will call {@link #initialize()}
	 * first.
	 * 
	 * @return A {@code Registry} instance.
	 */
	public static ETLRegistry getInstance()
	{
		if (instance == null) {
			instance = new ETLRegistry();
		}
		return instance;
	}

	private ETLRegistry()
	{
		setupLogging();
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
	 * Get an index manager for the NBA index.
	 * 
	 * @return
	 */
	public IndexManagerNative getIndexManager(DocumentType documentType)
	{
		Client client = ESClientManager.getInstance().getClient();
		String index = documentType.getIndexInfo().getName();
		IndexManagerNative idxMgr = new IndexManagerNative(client, index);
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(documentType.getESType());
		idxMgr.setObjectMapper(om);
		return new IndexManagerNative(client, index);
	}

	private void setupLogging()
	{
		System.setProperty(SYSPROP_ETL_LOGFILE, getLogFileName());
		if (System.getProperty("log4j.configurationFile") == null) {
			File f = DAORegistry.getInstance().getFile("log4j2.xml");
			if (f.exists()) {
				System.setProperty("log4j.configurationFile", f.getAbsolutePath());
			}
			else {
				String fmt = "Log4j config file not in default location "
						+ "(%s) and no system property \"log4j.configurationFile\"";
				String msg = String.format(fmt, f.getAbsolutePath());
				throw new InitializationException(msg);
			}
		}
		logger = LogManager.getLogger(getClass());
	}

	private static String getLogFileName()
	{
		File confDir = DAORegistry.getInstance().getConfigurationDirectory();
		File logDir = FileUtil.newFile(confDir.getParentFile(), "log");
		String now = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		String command = System.getProperty("sun.java.command");
		String[] chunks = command.split("\\.");
		String mainClass = chunks[chunks.length - 1].split(" ")[0];
		String logFileName = now + "." + mainClass + ".log";
		File logFile = FileUtil.newFile(logDir, logFileName);
		System.out.println("Log file: " + logFile.getAbsolutePath());
		return logFile.getAbsolutePath();
	}

}
