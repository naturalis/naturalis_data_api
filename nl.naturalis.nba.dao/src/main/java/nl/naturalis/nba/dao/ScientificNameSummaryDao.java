package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.IScientificNameSummaryAccess;
import nl.naturalis.nba.api.model.ScientificNameSummary;

public class ScientificNameSummaryDao extends NbaDao<ScientificNameSummary>
		implements IScientificNameSummaryAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(ScientificNameSummaryDao.class);

	public ScientificNameSummaryDao()
	{
		super(SCIENTIFIC_NAME_SUMMARY);
	}

	@Override
	ScientificNameSummary[] createDocumentObjectArray(int length)
	{
		return new ScientificNameSummary[length];
	}

}
