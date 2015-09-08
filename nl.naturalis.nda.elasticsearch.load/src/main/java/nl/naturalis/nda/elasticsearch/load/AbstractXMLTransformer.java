package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.rpad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractXMLTransformer<T> implements XMLTransformer<T> {

	protected final ETLStatistics stats;

	protected boolean suppressErrors;
	protected XMLRecordInfo recInf;
	protected String objectID;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractXMLTransformer(ETLStatistics stats)
	{
		this.stats = stats;
	}

	@Override
	public abstract List<T> transform(XMLRecordInfo recInf);

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

	protected void handleError(Throwable t)
	{
		stats.objectsRejected++;
		if (!suppressErrors)
			error(t.toString());
		if (logger.isDebugEnabled())
			logger.debug(t.toString(), t);
	}

	protected void error(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.error(msg);
	}

	protected void warn(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.warn(msg);
	}

	protected void info(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.info(msg);
	}

	protected void debug(String pattern, Object... args)
	{
		String msg = messagePrefix() + String.format(pattern, args);
		logger.debug(msg);
	}

	private String messagePrefix()
	{
		return rpad(objectID, 16, " | ");
	}
}
