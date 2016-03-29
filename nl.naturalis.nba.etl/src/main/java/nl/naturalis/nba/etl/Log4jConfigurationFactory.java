package nl.naturalis.nba.etl;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Programmatic configuration of log4j. Not currently used.
 * @author Ayco Holleman
 *
 */
@Deprecated
public class Log4jConfigurationFactory extends ConfigurationFactory {


	public Log4jConfigurationFactory()
	{
	}

	@Override
	protected String[] getSupportedTypes()
	{
		return new String[] { "*" };
	}

	@Override
	public Configuration getConfiguration(ConfigurationSource source)
	{
		return getConfiguration(source.toString(), null);
	}

	@Override
	public Configuration getConfiguration(final String name, final URI configLocation)
	{
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
		return createConfiguration(builder);
	}

	private static Configuration createConfiguration(ConfigurationBuilder<BuiltConfiguration> config)
	{

		config.setConfigurationName("The One");
		config.setStatusLevel(Level.ERROR);

		AppenderComponentBuilder appender = config.newAppender("Console", "Console");
		config.add(appender);
		appender.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		LayoutComponentBuilder layout = config.newLayout("PatternLayout");
		appender.add(layout);
		layout.addAttribute("pattern", "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n");

		appender = config.newAppender("File", "File");
		appender.addAttribute("fileName", "test.log");
		config.add(appender);
		layout = config.newLayout("PatternLayout");
		appender.add(layout);
		layout.addAttribute("pattern", "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n");

		LoggerComponentBuilder logger = config.newLogger("nl.naturalis.nba", Level.INFO);
		config.add(logger);
		AppenderRefComponentBuilder appenderRef = config.newAppenderRef("File");
		logger.add(appenderRef);
		appenderRef.addAttribute("additivity", false);

		RootLoggerComponentBuilder rootLogger = config.newRootLogger(Level.ERROR);
		config.add(rootLogger);
		appenderRef = config.newAppenderRef("Console");
		rootLogger.add(appenderRef);
		appenderRef.addAttribute("additivity", false);

		return config.build();

	}
}
