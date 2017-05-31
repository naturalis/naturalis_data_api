package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import nl.naturalis.nba.common.PathValueComparator.PathValueComparee;

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
	public QueryResult<ScientificNameGroup> query(QuerySpec querySpec) throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup> result = super.query(querySpec);
		if (querySpec instanceof ScientificNameGroupQuerySpec) {
			processExtraQueryOptions(result, (ScientificNameGroupQuerySpec) querySpec);
		}
		return result;
	}

	@Override
	public QueryResult<ScientificNameGroup> querySpecial(QuerySpec querySpec)
			throws InvalidQueryException
	{
		QueryResult<ScientificNameGroup> result = super.query(querySpec);
		QuerySpec specimenQuerySpec = createQuerySpecForSpecimens(querySpec);
		if (specimenQuerySpec.getConditions() != null) {
			if (sortByScore(querySpec)) {
				purgeAndSortByScore(result, specimenQuerySpec);
			}
			else {
				purge(result, specimenQuerySpec);
			}
		}
		if (querySpec instanceof ScientificNameGroupQuerySpec) {
			processExtraQueryOptions(result, (ScientificNameGroupQuerySpec) querySpec);
		}
		return result;
	}

	/*
	 * Processed the QuerySpec for the ScientificNameGroup index and churns out
	 * a QuerySpec that is suitable to query the Specimen index with.
	 */
	private static QuerySpec createQuerySpecForSpecimens(QuerySpec querySpec)
			throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		if (querySpec.getConditions() != null) {
			qs.setLogicalOperator(querySpec.getLogicalOperator());
			qs.setConditions(processConditions(querySpec.getConditions()));
		}
		qs.setConstantScore(querySpec.isConstantScore());
		return qs;
	}

	private static ArrayList<QueryCondition> processConditions(List<QueryCondition> conditions)
			throws InvalidQueryException
	{
		if (conditions == null) {
			return null;
		}
		ArrayList<QueryCondition> copies = new ArrayList<>(conditions.size());
		for (QueryCondition condition : conditions) {
			if (isSpecimenCondition(condition)) {
				QueryCondition copy = new QueryCondition(condition);
				processCondition(copy);
				copies.add(copy);
			}
			else {
				/*
				 * Ignore this condition, but first make sure it doesn't have
				 * any siblings that ARE specimen-specific query conditions.
				 */
				QueryCondition descendant = findDescendantWithSpecimenField(condition);
				if (descendant != null) {
					String fmt = "Query condition on field %s must not be nested within "
							+ "condition on field %s. Specimen-related conditions must "
							+ "be top-level conditions or nested within another "
							+ "specimen-related, top-level condition";
					String msg = String.format(fmt, descendant.getField(), condition.getField());
					throw new InvalidQueryException(msg);
				}
			}
		}
		return copies.isEmpty() ? null : copies;
	}

	/*
	 * Maps a query condition for the ScientificNameGroup index to a query
	 * condition for the Specimen index.
	 */
	private static void processCondition(QueryCondition condition) throws InvalidQueryException
	{
		// Chop off the "specimens" path element
		condition.setField(condition.getField().shift());
		// Map the matchingIdentifications field to the identifications field
		if (condition.getField().getElement(0).equals("matchingIdentifications")) {
			Path mapped = condition.getField().replace(0, "identifications");
			condition.setField(mapped);
		}
		// Get rid of all sibling conditions that are not specimen-specific
		condition.setAnd(processConditions(condition.getAnd()));
		condition.setOr(processConditions(condition.getOr()));
	}

	/*
	 * Check if the specified QueryCondition has a descendant that is a
	 * condition on a specimen-related field.
	 */
	private static QueryCondition findDescendantWithSpecimenField(QueryCondition condition)
	{
		if (condition.getAnd() != null) {
			for (QueryCondition c : condition.getAnd()) {
				if (isSpecimenCondition(c)) {
					return c;
				}
				return findDescendantWithSpecimenField(c);
			}
		}
		if (condition.getOr() != null) {
			for (QueryCondition c : condition.getOr()) {
				if (isSpecimenCondition(c)) {
					return c;
				}
				return findDescendantWithSpecimenField(c);
			}
		}
		return null;
	}

	private static boolean isSpecimenCondition(QueryCondition condition)
	{
		return condition.getField().getElement(0).equals("specimens");
	}

	/*
	 * Purges specimens from a ScientificNameGroup document that do no satisfy
	 * the specimen-specific query conditions in the QuerySpec. It does this by
	 * copying all specimen-specific query conditions to a new QuerySpec and use
	 * that one to query the Specimen index. The Specimen objects coming back
	 * from that query are then used to purge the specimens in the
	 * ScientificNameGroup document.
	 */
	private static void purge(QueryResult<ScientificNameGroup> result, QuerySpec specimenQuerySpec)
			throws InvalidQueryException
	{
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		specimenQuerySpec.addCondition(extraCondition);
		SpecimenDao specimenDao = new SpecimenDao();
		for (QueryResultItem<ScientificNameGroup> item : result) {
			ScientificNameGroup nameGroup = item.getItem();
			specimenQuerySpec.setSize(nameGroup.getSpecimenCount());
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
				String fmt = "Number of specimens purged from name group {}: {}";
				int count = nameGroup.getSpecimens().size() - purged.size();
				logger.debug(fmt, nameGroup.getName(), count);
			}
			nameGroup.setSpecimens(purged);
			nameGroup.setSpecimenCount(purged.size());
		}
	}

	/*
	 * Purges specimens from a ScientificNameGroup document that do no satisfy
	 * the specimen-specific query conditions in the QuerySpec. This method will
	 * also sort the specimens within each ScientificNameGroup document
	 * according the score assigned to them by Elasticsearch if you would use
	 * those query conditions to query the Specimen index.
	 */
	private static void purgeAndSortByScore(QueryResult<ScientificNameGroup> result,
			QuerySpec specimenQuerySpec) throws InvalidQueryException
	{
		/*
		 * For each ScientificNameGroup document in the result set we are going
		 * to execute an extra query against the Specimen index using all
		 * conditions in the specified QuerySpec object PLUS an extra condition
		 * limiting the specimens to those who have a scientific name
		 * corresponding to the ScientificNameGroup document we are currently
		 * processing.
		 */
		String field = "identifications.scientificName.scientificNameGroup";
		QueryCondition extraCondition = new QueryCondition(field, "=", null);
		specimenQuerySpec.addCondition(extraCondition);
		SpecimenDao specimenDao = new SpecimenDao();
		for (QueryResultItem<ScientificNameGroup> item : result) {
			ScientificNameGroup nameGroup = item.getItem();
			/*
			 * First convert the name group's specimens to a lookup table
			 * allowing for quick lookup-by-id
			 */
			HashMap<String, SummarySpecimen> lookupTable = new HashMap<>(
					nameGroup.getSpecimenCount() + 8, 1F);
			for (SummarySpecimen specimen : nameGroup.getSpecimens()) {
				lookupTable.put(specimen.getId(), specimen);
			}
			/*
			 * Now execute the query against the Specimen index
			 */
			specimenQuerySpec.setSize(nameGroup.getSpecimenCount());
			extraCondition.setValue(nameGroup.getName());
			QueryResult<Specimen> realSpecimens = specimenDao.query(specimenQuerySpec);
			/*
			 * Now filter and sort the specimens within the ScientificNameGroup
			 * document just like the "real" specimens coming back from the
			 * query
			 */
			ArrayList<SummarySpecimen> purgedAndSorted = new ArrayList<>(realSpecimens.size());
			for (int i = 0; i < realSpecimens.size(); i++) {
				String specimenId = realSpecimens.get(i).getItem().getId();
				SummarySpecimen summarySpecimen = lookupTable.get(specimenId);
				if (summarySpecimen != null) {
					purgedAndSorted.add(summarySpecimen);
				}
			}
			if (logger.isDebugEnabled()) {
				String fmt = "Number of specimens purged from name group {}: {}";
				int count = nameGroup.getSpecimens().size() - purgedAndSorted.size();
				logger.debug(fmt, nameGroup.getName(), count);
			}
			nameGroup.setSpecimens(purgedAndSorted);
			nameGroup.setSpecimenCount(purgedAndSorted.size());
		}
	}

	/*
	 * Applies/processes the extra fields in ScientificNameGroupQuerySpec
	 * (specimensFrom, specimensSize, etc.)
	 */
	private static void processExtraQueryOptions(QueryResult<ScientificNameGroup> result,
			ScientificNameGroupQuerySpec qs) throws InvalidQueryException
	{
		Integer f = qs.getSpecimensFrom();
		Integer s = qs.getSpecimensSize();
		int offset = f == null ? 0 : Math.max(f.intValue(), 0);
		int maxSpecimens = s == null ? 10 : Math.max(s.intValue(), -1);
		boolean mustSort = qs.getSpecimensSortFields() != null && !sortByScore(qs);
		PathValueComparator<SummarySpecimen> comparator = null;
		if (mustSort) {
			PathValueComparee[] comparees = sortFieldsToComparees(qs.getSpecimensSortFields());
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
				if (mustSort) {
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
					sng.setSpecimens(sng.getSpecimens().subList(offset, to));
				}
			}
		}
	}

	private static PathValueComparee[] sortFieldsToComparees(List<SortField> sortFields)
			throws InvalidQueryException
	{
		PathValueComparee[] comparees = new PathValueComparee[sortFields.size()];
		for (int i = 0; i < sortFields.size(); i++) {
			SortField sf = sortFields.get(i);
			if (sf.getPath().getElement(0).equals("specimens")) {
				String fmt = "Invalid sort field: %s\nSort fields for specimens "
						+ "within a ScientificNameGroup document must be specified "
						+ "relative to the \"specimens\" field.\nThey must NOT "
						+ "include the \"specimens\" path element";
				String msg = String.format(fmt, sf.getPath());
				throw new InvalidQueryException(msg);

			}
			comparees[i] = new PathValueComparee(sf.getPath(), !sf.isAscending());
		}
		return comparees;
	}

	private static boolean sortByScore(QuerySpec qs)
	{
		if (qs instanceof ScientificNameGroupQuerySpec) {
			ScientificNameGroupQuerySpec sngQs = (ScientificNameGroupQuerySpec) qs;
			if (sngQs.getSpecimensSortFields() == null) {
				return true;
			}
			Iterator<SortField> iterator = sngQs.getSpecimensSortFields().iterator();
			SortField sortField = iterator.next();
			return sortField.getPath().equals(SortField.SORT_FIELD_SCORE);
		}
		return false;
	}

}
