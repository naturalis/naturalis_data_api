package nl.naturalis.nba.client;

import java.util.Arrays;

import nl.naturalis.nba.api.INameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NameGroupQuerySpec;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.debug.DebugUtil;

public class ScientificNameGroupDemo {

	public static void main(String[] args) throws InvalidQueryException
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		INameGroupAccess client = session.getNameGroupClient();
		NameGroupQuerySpec qs = new NameGroupQuerySpec();
		//SortField sf = new SortField("matchingIdentifications.scientificName.fullScientificName");
		//SortField sf = new SortField("gatheringEvent.localityText");
		SortField sf = new SortField("gatheringEvent.gatheringPersons.fullName",SortOrder.ASC);
		qs.setSpecimensSortFields(Arrays.asList(sf));
		qs.setSize(1);
		qs.addCondition(new QueryCondition("specimenCount", "=", 20));
		QueryResult<ScientificNameGroup> result = client.query(qs);
		//System.out.println(JsonUtil.toPrettyJson(result));
		DebugUtil.log("/tmp/ayco.txt", JsonUtil.toPrettyJson(result));
	}

}
