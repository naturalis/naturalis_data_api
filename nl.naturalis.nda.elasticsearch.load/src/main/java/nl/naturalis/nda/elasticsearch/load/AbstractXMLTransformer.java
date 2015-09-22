package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.rpad;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for transformers that take XML elements as their input. Used for
 * CRS and NSR.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of output produced by this transformer (e.g.
 *            {@link ESSpecimen}, {@link ESTaxon}, {@link ESMultiMediaObject})
 */
public abstract class AbstractXMLTransformer<T> implements XMLTransformer<T> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final ETLStatistics stats;

	protected boolean suppressErrors;
	protected XMLRecordInfo recInf;
	protected String objectID;


	/**
	 * Create a new XML transformer that will update the specified
	 * {@link ETLStatistics statistics} object while/after transforming its
	 * input.
	 * 
	 * @param stats
	 */
	public AbstractXMLTransformer(ETLStatistics stats)
	{
		this.stats = stats;
	}

	/**
	 * Get the statistics object used by this transformer.
	 * 
	 * @return The statistics object used by this transformer
	 */
	public ETLStatistics getStatistics()
	{
		return stats;
	}

	/**
	 * Whether or not error suppression is on.
	 * 
	 * @return
	 */
	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	/**
	 * Whether or not to suppress errors. With error suppression, INFO messages
	 * are let through while ERROR and WARN messages are suppressed. This may
	 * make the log file more readable if you expect huge amounts of well-known,
	 * anticipated errors.
	 * 
	 * @param suppressErrors
	 */
	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	/**
	 * Handles a validation error. Increases the
	 * {@link ETLStatistics#objectsRejected objectsRejected} counter by one and
	 * logs a standardized error message. The error message is logged
	 * <i>even</i> if error suppression is enabled, because the assumption is
	 * that this method is called only for unexpected errors, mostly likely
	 * caught in a catch-all block, that you do not want to miss. If DEBUG is
	 * enabled the specified throwable's stack trace is logged as well.
	 * 
	 * @param t
	 *            An (unanticipated) exception thrown while transformer the XML
	 *            input
	 */
	protected void handleError(Throwable t)
	{
		stats.objectsRejected++;
		error(t.toString());
		if (logger.isDebugEnabled())
			logger.debug(t.toString(), t);
	}

	/**
	 * Logs a error message, prefixing a standardized {@link #messagePrefix()
	 * message prefix} to the custom error message.
	 * 
	 * @param pattern
	 *            The message pattern
	 * @param args
	 *            The message arguments
	 */
	protected void error(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.error(msg);
	}

	/**
	 * Logs a error message, prefixing a standardized {@link #messagePrefix()
	 * message prefix} to the custom error message.
	 * 
	 * @param pattern
	 *            The message pattern
	 * @param args
	 *            The message arguments
	 */
	protected void warn(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.warn(msg);
	}

	/**
	 * Logs a error message, prefixing a standardized {@link #messagePrefix()
	 * message prefix} to the custom error message.
	 * 
	 * @param pattern
	 *            The message pattern
	 * @param args
	 *            The message arguments
	 */
	protected void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.info(msg);
	}

	/**
	 * Logs a error message, prefixing a standardized {@link #messagePrefix()
	 * message prefix} to the custom error message.
	 * 
	 * @param pattern
	 *            The message pattern
	 * @param args
	 *            The message arguments
	 */
	protected void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	/**
	 * A standard prefix for all messages logged using one of the specialized
	 * log methods in this class. By default the message prefix is a
	 * right-padded column containing the object ID of the validated object.
	 * Subclasses may to override this if appropriate.
	 * 
	 * @return
	 */
	protected String messagePrefix()
	{
		return rpad(objectID, 16, " | ");
	}
}
