package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCSVTransformer<T> implements CSVTransformer<T> {

	protected final ETLStatistics stats;

	protected boolean suppressErrors;
	protected CSVRecordInfo recInf;
	protected String objectID;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractCSVTransformer(ETLStatistics stats)
	{
		this.stats = stats;
	}

	/**
	 * To be implemented by subclasses, but really the first thing they should
	 * do here is assign the argument to the {@link #recInf} field and set the
	 * {@link #objectID} field somehow.
	 */
	public abstract List<T> transform(CSVRecordInfo record);

	public ETLStatistics getStatistics()
	{
		return stats;
	}

	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
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
		return "Line " + lpad(recInf.getLineNumber(), 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}