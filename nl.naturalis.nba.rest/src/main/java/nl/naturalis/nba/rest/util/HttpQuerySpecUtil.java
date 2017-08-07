package nl.naturalis.nba.rest.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;

public class HttpQuerySpecUtil {

	/**
	 * Compares two Query Specs
	 * 
	 * @author Tom Gilissen
	 * 
	 * @param qs1  QuerySpec one
	 * @param qs2  QuerySpec two
	 * @return  true if the Query Specs are equal, false otherwise
	 */
	static Boolean compareQuerySpecs(QuerySpec qs1, QuerySpec qs2)
	{

		// Compare the Logical Operator
		if (qs1.getLogicalOperator() != qs2.getLogicalOperator())
			return false;

		// Compare From and Size
		if (qs1.getSize() != qs2.getSize() || qs1.getFrom() != qs2.getFrom())
			return false;

		// Compare the Fields
		if (qs1.getFields().size() != qs2.getFields().size())
			return false;
		for (Path field : qs1.getFields()) {
			if (!qs2.getFields().contains(field)) {
				return false;
			}
		}

		// Compare the Sort Fields
		if (qs1.getSortFields().size() != qs2.getSortFields().size()) {
			return false;
		}

		Map<Path, SortOrder> sortMap1 = new HashMap<>();
		for (SortField sortField : qs1.getSortFields()) {
			sortMap1.put(sortField.getPath(), sortField.getSortOrder());
		}
		Map<Path, SortOrder> sortMap2 = new HashMap<>();
		for (SortField sortField : qs2.getSortFields()) {
			sortMap2.put(sortField.getPath(), sortField.getSortOrder());
		}
		if (!sortMap1.equals(sortMap2))
			return false;

		// Compare the Query Conditions
		Map<Path, ArrayList<String>> map1 = new HashMap<>();
		for (QueryCondition cond : qs1.getConditions()) {
			map1.put(cond.getField(), new ArrayList<>(
					Arrays.asList(cond.getOperator().toString(), cond.getValue().toString())));
		}

		Map<Path, ArrayList<String>> map2 = new HashMap<>();
		for (QueryCondition cond : qs2.getConditions()) {
			map2.put(cond.getField(), new ArrayList<>(
					Arrays.asList(cond.getOperator().toString(), cond.getValue().toString())));
		}

		if (!map1.equals(map2))
			return false;
		
		if (qs1 instanceof GroupByScientificNameQuerySpec)
		{
			// Compare the groupSort
			Map<Path, SortOrder> groupSortMap1 = new HashMap<>();
			for (SortField field : qs1.getSortFields())
				groupSortMap1.put(field.getPath(), field.getSortOrder());
			
			Map<Path, SortOrder> groupSortMap2 = new HashMap<>();
			for (SortField field : qs1.getSortFields())
				groupSortMap2.put(field.getPath(), field.getSortOrder());

			if (!groupSortMap1.equals(groupSortMap2))
				return false;
			
			// Compare the groupFilters acceptRegexp and rejectRegexp
			if (
					((GroupByScientificNameQuerySpec) qs1).getGroupFilter().getAcceptRegexp() != ((GroupByScientificNameQuerySpec) qs2).getGroupFilter().getAcceptRegexp() || 
					((GroupByScientificNameQuerySpec) qs1).getGroupFilter().getRejectRegexp() != ((GroupByScientificNameQuerySpec) qs2).getGroupFilter().getRejectRegexp()
				) {
				return false;
			}
			// ... and groupFilters acceptValues and rejectValues
			String[] group1 = ((GroupByScientificNameQuerySpec) qs1).getGroupFilter().getAcceptValues();
			String[] group2 = ((GroupByScientificNameQuerySpec) qs2).getGroupFilter().getAcceptValues();
			
			if (!Arrays.deepEquals(group1, group2)) {
				return false;
			}
			
			// Compare noTaxa
			if (((GroupByScientificNameQuerySpec) qs1).isNoTaxa() != ((GroupByScientificNameQuerySpec) qs2).isNoTaxa())
				return false;
				
		}
			
		// Query Specs are equal
		return true;
	}

}
