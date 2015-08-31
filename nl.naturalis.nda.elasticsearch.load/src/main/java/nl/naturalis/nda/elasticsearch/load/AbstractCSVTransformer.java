package nl.naturalis.nda.elasticsearch.load;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Transformers that take a commons-csv CSVRecord as input and
 * produce an ElasticSearch object (in practice either ESSpecimen,
 * ESMultiMediaObject or ESTaxon).
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of ES object produced by this transformer
 */
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
		return CSVImportUtil.getDefaultMessagePrefix(recInf.getLineNumber(), objectID);
	}

}