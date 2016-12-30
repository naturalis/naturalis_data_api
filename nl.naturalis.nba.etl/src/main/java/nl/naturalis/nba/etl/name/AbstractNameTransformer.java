package nl.naturalis.nba.etl.name;

import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;

abstract class AbstractNameTransformer<INPUT extends IDocumentObject>
		extends AbstractDocumentTransformer<INPUT, Name> {

	AbstractNameTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	abstract void initializeOutputObjects(List<INPUT> inputObjects);

}
