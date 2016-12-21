package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_LOADER_QUEUE_SIZE;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

import nl.naturalis.nba.utils.ConfigObject;

abstract class CoLImporter {

	final int loaderQueueSize;
	final boolean suppressErrors;

	CoLImporter()
	{
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String val = System.getProperty(SYSPROP_LOADER_QUEUE_SIZE, "1000");
		loaderQueueSize = Integer.parseInt(val);
	}

}
