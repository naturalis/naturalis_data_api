package nl.naturalis.nda.elasticsearch.load;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.getDefaultMessagePrefix;

/**
 * Base class for Transformers that take a CSV record as input.
 * 
 * @author Ayco Holleman
 *
 * @param <OUTPUT>
 *            The type of object that is output from the transformer
 */
public abstract class AbstractCSVTransformer<OUTPUT> extends AbstractTransformer<CSVRecordInfo, OUTPUT> {

	public AbstractCSVTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	/**
	 * Overrides {@link AbstractTransformer#messagePrefix()} by also reporting
	 * the line number of the CSV record being processed.
	 */
	@Override
	protected String messagePrefix()
	{
		return getDefaultMessagePrefix(input.getLineNumber(), objectID);
	}

}