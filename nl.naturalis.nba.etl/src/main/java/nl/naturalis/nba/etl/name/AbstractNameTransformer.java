package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_IMPORT_NAME_PARTS;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.etl.AbstractDocumentTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.ConfigObject;

abstract class AbstractNameTransformer<INPUT extends IDocumentObject>
		extends AbstractDocumentTransformer<INPUT, Name> {

	private static final Logger logger = getLogger(AbstractNameTransformer.class);

	protected final boolean importNameParts;

	AbstractNameTransformer(ETLStatistics stats)
	{
		super(stats);
		importNameParts = ConfigObject.isEnabled(SYSPROP_IMPORT_NAME_PARTS);
		logger.info("Import name parts of scientific name: {}", importNameParts);
	}

	abstract void initializeOutputObjects(List<INPUT> inputObjects);

}
