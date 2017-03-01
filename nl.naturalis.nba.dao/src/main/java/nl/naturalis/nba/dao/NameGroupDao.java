package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.INameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NameGroupQuerySpec;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.SummarySpecimen;

import static java.lang.Boolean.*;

public class NameGroupDao extends NbaDao<NameGroup> implements INameGroupAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(NameGroupDao.class);

	public NameGroupDao()
	{
		super(NAME_GROUP);
	}

	@Override
	NameGroup[] createDocumentObjectArray(int length)
	{
		return new NameGroup[length];
	}

	@Override
	public QueryResult<NameGroup> query(QuerySpec querySpec) throws InvalidQueryException
	{
		QueryResult<NameGroup> result = super.query(querySpec);
		if (querySpec instanceof NameGroupQuerySpec) {
			NameGroupQuerySpec qs = (NameGroupQuerySpec) querySpec;
			boolean showTaxa = qs.isNoTaxa() == null || qs.isNoTaxa().equals(TRUE);
			if (qs.getSpecimensFrom() == null && qs.getSpecimensSize() == null && showTaxa) {
				return result;
			}
			for (QueryResultItem<NameGroup> item : result) {
				NameGroup nameGroup = item.getItem();
				if (!showTaxa) {
					nameGroup.setTaxa(null);
				}
				if (qs.getSpecimensSize() == 0) {
					nameGroup.setSpecimens(null);
				}
				List<SummarySpecimen> specimens = new ArrayList<>(nameGroup.getSpecimens());
				int from = qs.getSpecimensFrom() == null ? 0 : qs.getSpecimensFrom();
				int to = qs.getSpecimensSize() == null ? specimens.size() : qs.getSpecimensSize();
				to = Math.min(to, specimens.size());
				Set<SummarySpecimen> chunk = new LinkedHashSet<>(specimens.subList(from, to));
				nameGroup.setSpecimens(chunk);
			}
		}
		return result;
	}

}
