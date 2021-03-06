package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.CSVImportUtil.getDefaultMessagePrefix;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Base class for Transformers that take a CSV record as input.
 * 
 * @author Ayco Holleman
 *
 * @param <OUTPUT>
 *            The type of object that is output from the transformer
 */
public abstract class AbstractCSVTransformer<T extends Enum<T>, OUTPUT extends IDocumentObject>
		extends AbstractTransformer<CSVRecordInfo<T>, OUTPUT> {

	public AbstractCSVTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	/**
	 * Overrides {@link AbstractTransformer#messagePrefix()} by also reporting the line
	 * number of the CSV record being processed.
	 */
	@Override
	protected String messagePrefix()
	{
		return getDefaultMessagePrefix(input.getLineNumber(), objectID);
	}

}