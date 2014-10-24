package nl.naturalis.nda.service.rest.util;

import java.io.File;

import org.domainobject.util.FileUtil;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class LogUtil {
	
	public static void configureLogging()
	{
		String confDir = System.getProperty(NDA.SYSPROP_CONFIG_DIR);
		if (confDir == null) {
			FileUtil.log("Failed to set up NBA logging. Missing system property \"%s\"", NDA.SYSPROP_CONFIG_DIR);
			return;
		}
		File file = new File(confDir);
		if (!file.isDirectory()) {
			FileUtil.log("Failed to set up NBA logging. No such directory \"%s\"", confDir);
			return;
		}
		String logbackXmlPath = confDir + "/logback.xml";
		file = new File(logbackXmlPath);
		if (!file.isFile()) {
			FileUtil.log("Failed to set up NBA logging. Missing logback configuration file (logback.xml)");
			return;
		}
		if (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext)) {
			FileUtil.log("Failed to set up NBA logging. Non-logback implementation used for slf4j: " + LoggerFactory.getILoggerFactory().getClass().getName());
			return;
		}
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			configurator.doConfigure(logbackXmlPath);
			FileUtil.log("NBA logging successfully configured");
		}
		catch (JoranException e) {
			FileUtil.log("Failed to set up NBA logging. %s", e.getMessage());
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
}
