package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import java.util.ArrayList;
import java.util.Collections;
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
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.common.PathValueComparator;
import nl.naturalis.nba.common.PathValueComparator.Comparee;

public class ScientificNameGroupDao extends NbaDao<ScientificNameGroup>
		implements INameGroupAccess {

	private static final Logger logger = getLogger(ScientificNameGroupDao.class);

	public ScientificNameGroupDao()
	{
		super(SCIENTIFIC_NAME_GROUP);
	}

	@Override
	ScientificNameGroup[] createDocumentObjectArray(int length)
	{
		return new ScientificNameGroup[length];
	}

	@Override
	public QueryResult<ScientificNameGroup> query(QuerySpec querySpec) throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup> result = super.query(querySpec);
		if (querySpec instanceof NameGroupQuerySpec) {
			NameGroupQuerySpec qs = (NameGroupQuerySpec) querySpec;
			Integer f = qs.getSpecimensFrom();
			Integer s = qs.getSpecimensSize();
			int from = f == null ? 0 : Math.max(f.intValue(), 0);
			int size = s == null ? -1 : Math.max(s.intValue(), -1);
			PathValueComparator<SummarySpecimen> comparator = null;
			if (qs.getSpecimensSortFields() != null) {
				Comparee[] comparees = sortFieldsToComparees(qs.getSpecimensSortFields());
				comparator = new PathValueComparator<>(comparees);
			}
			for (QueryResultItem<ScientificNameGroup> item : result) {
				ScientificNameGroup sng = item.getItem();
				if (qs.isNoTaxa()) {
					sng.setTaxa(null);
				}
				if (size == 0) {
					sng.setSpecimens(null);
				}
				else if (sng.getSpecimenCount() != 0) {
					List<SummarySpecimen> specimens = new ArrayList<>(sng.getSpecimens());
					if (qs.getSpecimensSortFields() != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Sorting specimens in name group {}", sng.getName());
						}
						Collections.sort(specimens, comparator);
					}
					// Make sure from is never beyond the current item's number of specimens
					int myFrom = Math.min(from, specimens.size() - 1);
					int to = size == -1 ? specimens.size() : Math.min(specimens.size(), size);
					Set<SummarySpecimen> chunk = new LinkedHashSet<>(specimens.subList(myFrom, to));
					sng.setSpecimens(chunk);
				}
			}
		}
		return result;
	}

	private static Comparee[] sortFieldsToComparees(List<SortField> sortFields)
	{
		Comparee[] comparees = new Comparee[sortFields.size()];
		for (int i = 0; i < sortFields.size(); i++) {
			SortField sf = sortFields.get(i);
			comparees[i] = new Comparee(sf.getPath(), !sf.isAscending());
		}
		return comparees;
	}

}
