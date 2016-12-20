package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.IDocumentObject;

public abstract class AbstractDocumentTransformer<INPUT extends IDocumentObject, OUTPUT extends IDocumentObject>
		extends AbstractTransformer<INPUT, OUTPUT> {

	public AbstractDocumentTransformer(ETLStatistics stats)
	{
		super(stats);
	}

}
