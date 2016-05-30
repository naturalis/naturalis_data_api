package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

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

	private static final String SYSPROP_CONFIG_DIR = "ndaConfDir";
	private static final String CONFIG_FILE_NAME = "nda-import.properties";

	private static Registry instance;

	private File confDir;
	private ConfigObject config;
	private Client esClient;

	private LoggerContext ctx;

	/*
	 * This is the registry's own logger. No need (and confusing) to make it
	 * static because logging only become available once the registry is
	 * instantiated.
	 */
	private Logger logger;

	/**
	 * Instantiates and initializes a {@code Registry} instance. This method
	 * must be called before handling any PURL request. If anything goes wrong
	 * while initializing the {@code Registry}, an
	 * {@link InitializationException} is thrown, causing the PURL server to die
	 * during startup. An explanation of what went wrong is written to the
	 * Wildfly log (standalone/log/server.log).
	 */
	public static void initialize()
	{
		if (instance == null) {
			instance = new Registry();
		}
	}

	/**
	 * Return a {@code Registry} instance. Will call {@link #initialize()}
	 * first.
	 * 
	 * @return A {@code Registry} instance.
	 */
	public static Registry getInstance()
	{
		initialize();
		return instance;
	}

	private Registry()
	{
		setConfDir();
		loadConfig();
		configureLogging();
	}

	/**
	 * Get a {@code ConfigObject} for the main configuration file
	 * (purl.properties).
	 * 
	 * @return
	 */
	public ConfigObject getConfig()
	{
		return config;
	}

	/**
	 * Get the directory designated to contain the application's configuration
	 * files. This directory will contain at least purl.properties, but may
	 * contain additional files that the application expects to be there.
	 * 
	 * @return
	 */
	public File getConfDir()
	{
		return confDir;
	}

	/**
	 * Returns a file within the application's configuration directory or one of
	 * its subdirectories.
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
		ch.qos.logback.classic.Logger logger = ctx.getLogger(cls);
		// TODO: in the future we may allow nda-import.properties or some other
		// config file to include separate log levels for different classes
		return logger;
	}

	/**
	 * Get a native Java ElasticSearch {@code Client}.
	 * 
	 * @return
	 */
	public Client getESClient()
	{
		if (esClient == null) {
			logger.info("Initializing ElasticSearch session");
			String cluster = config.required("elasticsearch.cluster.name");
			String[] hosts = config.required("elasticsearch.transportaddress.host").trim()
					.split(",");
			String[] ports = getPorts(hosts.length);
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster)
					.build();
			esClient = new TransportClient(settings);
			for (int i = 0; i < hosts.length; ++i) {
				String host = hosts[i].trim();
				int port = Integer.parseInt(ports[i].trim());
				logger.info(String.format("Adding transport address \"%s:%s\"", host, port));
				InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host,
						port);
				((TransportClient) esClient).addTransportAddress(transportAddress);
			}
			if (logger.isDebugEnabled()) {
				ClusterStatsRequest request = new ClusterStatsRequest();
				ClusterStatsResponse response = esClient.admin().cluster().clusterStats(request)
						.actionGet();
				logger.debug("Cluster stats: " + response.toString());
			}
		}
		return esClient;
	}

	/**
	 * Releases the connection to ElasticSearch. Must always be called once
	 * you're done interacting with ElasticSearch. If the connection was not
	 * open, this method does nothing.
	 */
	public void closeESClient()
	{
		if (esClient != null)
			esClient.close();
	}

	/**
	 * Get an index manager for the NBA index.
	 * 
	 * @return
	 */
	public IndexManagerNative getNbaIndexManager()
	{
		return new IndexManagerNative(getESClient(), getConfig().required(
				"elasticsearch.index.name"));
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
			System.out.println("Configuration directory: " + dir.getAbsolutePath());
		}
		catch (IOException e) {
			throw new InitializationException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void configureLogging()
	{
		ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
		ctx.reset();
		ch.qos.logback.classic.Logger root = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
		Level logLevel = Level.toLevel(config.required("logger.level"));
		root.setLevel(logLevel);
		if (config.isTrue("logger.to_file")) {
			FileAppender fileAppender = new FileAppender();
			fileAppender.setName("FILE");
			fileAppender.setContext(ctx);
			fileAppender.setFile(getLogFile());
			fileAppender.setEncoder(createPatternLayoutEncoder(ctx));
			fileAppender.start();
			root.addAppender(fileAppender);
		}
		if (config.isTrue("logger.to_console")) {
			ConsoleAppender consoleAppender = new ConsoleAppender();
			consoleAppender.setName("CONSOLE");
			consoleAppender.setContext(ctx);
			consoleAppender.setEncoder(createPatternLayoutEncoder(ctx));
			consoleAppender.start();
			root.addAppender(consoleAppender);
		}
		logger = getLogger(getClass());
	}

	private String[] getPorts(int numHosts)
	{
		String port = config.get("elasticsearch.transportaddress.port", true);
		String[] ports = port == null ? new String[] { "9300" } : port.trim().split(",");
		if (ports.length > 1 && ports.length != numHosts) {
			throw new RuntimeException(
					"Error creating ES client: number of ports does not match number of hosts");
		}
		else if (ports.length == 1 && numHosts > 1) {
			port = ports[0];
			ports = new String[numHosts];
			for (int i = 0; i < ports.length; ++i) {
				ports[i] = port;
			}
		}
		return ports;
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

	private String getLogFile()
	{
		File logDir = FileUtil.newFile(confDir.getParentFile(), "log");
		String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String command = System.getProperty("sun.java.command");
		String[] chunks = command.split("\\.");
		String mainClass = chunks[chunks.length - 1].split(" ")[0];
		String logFileName = mainClass + "-" + now + ".log";
		File logFile = FileUtil.newFile(logDir, logFileName);
		System.out.println("Created log file: " + logFile.getAbsolutePath());
		return logFile.getAbsolutePath();
	}

	private static PatternLayoutEncoder createPatternLayoutEncoder(LoggerContext ctx)
	{
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(ctx);
		encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} %5p | %-50logger{50} | %m %n");
		encoder.start();
		return encoder;
	}

}