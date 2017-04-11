package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.model.metadata.NbaSetting.INDEX_MAX_RESULT_WINDOW;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.IScientificNameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.common.PathValueComparator;
import nl.naturalis.nba.common.PathValueComparator.Comparee;

public class ScientificNameGroupDao extends NbaDao<ScientificNameGroup>
		implements IScientificNameGroupAccess {

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
	public QueryResult<ScientificNameGroup> query(ScientificNameGroupQuerySpec querySpec)
			throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup> result = super.query(querySpec);
		processSpecimens(result, querySpec);
		return result;
	}

	@Override
	public QueryResult<ScientificNameGroup> getSpeciesWithSpecimens(
			ScientificNameGroupQuerySpec querySpec) throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup> result = super.query(querySpec);
		QuerySpec specimenQuerySpec = createQuerySpecForSpecimens(querySpec);
		if (specimenQuerySpec.getConditions() != null) {
			purgeSpecimens(result, specimenQuerySpec);
		}
		processSpecimens(result, querySpec);
		return result;
	}

	private static QuerySpec createQuerySpecForSpecimens(QuerySpec querySpec)
	{
		QuerySpec qs = new QuerySpec();
		qs.setConstantScore(true);
		qs.setFields(Collections.emptyList());
		ScientificNameGroupMetaDataDao metadataDao = new ScientificNameGroupMetaDataDao();
		Object val = metadataDao.getSetting(INDEX_MAX_RESULT_WINDOW);
		int size = Integer.parseInt(val.toString());
		qs.setSize(size);
		if (querySpec.getConditions() != null) {
			qs.setLogicalOperator(querySpec.getLogicalOperator());
			qs.setConditions(processConditions(querySpec.getConditions()));
		}
		return qs;
	}

	private static ArrayList<QueryCondition> processConditions(List<QueryCondition> conditions)
	{
		if (conditions == null) {
			return null;
		}
		ArrayList<QueryCondition> copy = new ArrayList<>(conditions.size());
		for (QueryCondition condition : conditions) {
			if (condition.getField().getElement(0).equals("specimens")) {
				QueryCondition c = new QueryCondition(condition);
				processCondition(c);
				copy.add(c);
			}
			else {
				String fmt = "Query condition on field {} with {} AND sibling(s) "
						+ "and {} OR sibling(s) ignored for specimen purge. Make "
						+ "sure all conditions on specimen fields are main "
						+ "(non-sibling) conditions within the QuerySpec object, "
						+ "or siblings of main conditions on specimen fields";
				int x = condition.getAnd() == null ? 0 : condition.getAnd().size();
				int y = condition.getOr() == null ? 0 : condition.getOr().size();
				logger.warn(fmt, x, y);
			}
		}
		return copy.isEmpty() ? null : copy;
	}

	private static void processCondition(QueryCondition condition)
	{
		if (condition.getField().getElement(1).equals("matchingIdentifications")) {
			Path mapped = condition.getField().replace(1, "identifications");
			condition.setField(mapped);
		}
		condition.setAnd(processConditions(condition.getAnd()));
		condition.setOr(processConditions(condition.getOr()));
	}

	private static void purgeSpecimens(QueryResult<ScientificNameGroup> result,
			QuerySpec specimenQuerySpec) throws InvalidQueryException
	{
		List<QueryCondition> origConditions = specimenQuerySpec.getConditions();
		String field = "identifications.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		List<QueryCondition> newConditions = new ArrayList<>(origConditions.size() + 1);
		newConditions.add(extraCondition);
		newConditions.addAll(origConditions);
		SpecimenDao specimenDao = new SpecimenDao();
		for (QueryResultItem<ScientificNameGroup> item : result) {
			ScientificNameGroup nameGroup = item.getItem();
			extraCondition.setValue(nameGroup.getName());
			QueryResult<Specimen> specimens = specimenDao.query(specimenQuerySpec);
			List<String> ids = new ArrayList<>(specimens.size());
			for (int i = 0; i < specimens.size(); i++) {
				ids.add(specimens.get(i).getItem().getId());
			}
			TreeSet<String> idSet = new TreeSet<>(ids);
			List<SummarySpecimen> purged = new ArrayList<>(ids.size());
			for (SummarySpecimen ss : nameGroup.getSpecimens()) {
				if (idSet.contains(ss.getId())) {
					purged.add(ss);
				}
			}
			if (logger.isDebugEnabled()) {

			}
			nameGroup.setSpecimens(purged);
			nameGroup.setSpecimenCount(purged.size());
		}
	}

	private static void processSpecimens(QueryResult<ScientificNameGroup> result,
			ScientificNameGroupQuerySpec qs)
	{
		Integer f = qs.getSpecimensFrom();
		Integer s = qs.getSpecimensSize();
		int offset = f == null ? 0 : Math.max(f.intValue(), 0);
		int maxSpecimens = s == null ? 10 : Math.max(s.intValue(), -1);
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
			if (sng.getSpecimenCount() == 0) {
				continue;
			}
			if (maxSpecimens == 0 || sng.getSpecimenCount() < offset) {
				sng.setSpecimens(null);
			}
			else {
				if (qs.getSpecimensSortFields() != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Sorting specimens in name group {}", sng.getName());
					}
					Collections.sort(sng.getSpecimens(), comparator);
				}
				if (offset != 0 || maxSpecimens != -1) {
					int to;
					if (maxSpecimens == -1) {
						to = sng.getSpecimenCount();
					}
					else {
						to = Math.min(sng.getSpecimenCount(), offset + maxSpecimens);
					}
					if (logger.isDebugEnabled()) {
						String fmt = "Extracting specimens {} to {} in name group {}";
						logger.debug(fmt, offset, to, sng.getName());
					}
					sng.setSpecimens(sng.getSpecimens().subList(offset, to));
				}
			}
		}
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
