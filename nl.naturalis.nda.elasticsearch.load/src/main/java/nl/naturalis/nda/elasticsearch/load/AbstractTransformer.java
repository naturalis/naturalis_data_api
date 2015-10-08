package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.rpad;

import java.util.List;

import org.slf4j.Logger;

/**
 * Abstract base class for all transformers. Provides functionality for uniform
 * error reporting.
 * 
 * @author Ayco Holleman
 *
 * @param <INPUT>
 *            The type of object serving as input to the transformer
 * @param <OUTPUT>
 *            The type of object that is output from the transformer
 */
public abstract class AbstractTransformer<INPUT, OUTPUT> implements Transformer<INPUT, OUTPUT> {

	protected final ETLStatistics stats;
	protected final Logger logger;

	protected boolean suppressErrors;

	protected INPUT input;
	protected String objectID;

	/**
	 * Creates a transformer that will update the counters of the specified
	 * statistics object.
	 * 
	 * @param stats
	 */
	public AbstractTransformer(ETLStatistics stats)
	{
		this.stats = stats;
		this.logger = Registry.getInstance().getLogger(getClass());
	}

	public final List<OUTPUT> transform(INPUT input)
	{
		this.input = input;
		stats.recordsProcessed++;
		if (skipRecord()) {
			stats.recordsSkipped++;
			return null;
		}
		objectID = getObjectID();
		if (objectID == null) {
			stats.recordsRejected++;
			if (!suppressErrors) {
				error("Missing object ID");
			}
			return null;
		}
		return doTransform();
	}

	protected boolean skipRecord()
	{
		return false;
	}

	protected abstract String getObjectID();

	protected abstract List<OUTPUT> doTransform();

	/**
	 * Get the statistics object updated by this transformer.
	 * 
	 * @return The statistics object updated by this transformer
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
	 * are let through while most ERROR and all WARN messages are suppressed.
	 * This may make the log file more readable if you expect huge amounts of
	 * well-known, anticipated errors. There is some intelligence in
	 * <i>which</i> error messages are suppressed. If the import program
	 * generates the error (meaning it sort of already anticipated the error
	 * condition), the error message is suppressed. However if the error is
	 * caught in a catch-all block, the error is always logged, even with error
	 * suppression enabled.
	 * 
	 * @param suppressErrors
	 * 
	 * @see #handleError(Throwable)
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
	 * 
	 * @see #setSuppressErrors(boolean)
	 * 
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
	 * log methods in this class. The idea is that any message is by default
	 * prefixed with the ID of the object for which the message was generated.
	 * By default the message prefix is a right-padded column containing the
	 * object ID of the validated object. Therefore, one of the first things
	 * subclasses must do in their implementation of {@link #transform(Object)
	 * transform()} is set the {@link #objectID} field. Subclasses may override
	 * {@code messagePrefix()} if appropriate.
	 * 
	 * @return
	 * 
	 * @see #error(String, Object...)
	 * @see #warn(String, Object...)
	 * @see #info(String, Object...)
	 * @see #debug(String, Object...)
	 * 
	 */
	protected String messagePrefix()
	{
		return rpad(objectID, 16, " | ");
	}

}