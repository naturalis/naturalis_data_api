package nl.naturalis.nba.client;

import static java.lang.System.out;
import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS_IC;
import static org.domainobject.util.StringUtil.pad;
import static org.domainobject.util.StringUtil.rpad;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();
		String[] collections = client.getNamedCollections();
		for (String collection : collections) {
			System.out.println(pad(' ' + collection + ' ', 80, '-'));
			String[] ids = client.getIdsInCollection(collection);
			Specimen[] specimens = client.find(ids);
			for(Specimen specimen: specimens) {
				out.print(rpad(specimen.getUnitID(),15, " | "));
				out.print(rpad(specimen.getSourceSystem().getCode(),15, " | "));
				out.print(rpad(specimen.getSex(),10, " | "));
				out.println(specimen.getIdentifications().get(0).getScientificName().getFullScientificName());
			}
			System.out.println();
		}
		Condition condition1 = new Condition("gatheringEvent.gatheringPersons.fullName", EQUALS_IC, "burg");
		condition1.and("unitID","=", "ZMA.MAM.100").and("sex","=","male");
		Condition condition2 =new Condition("phaseOrStage","=","EGG");
		condition2.or(condition1);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition1);
		QueryResult<Specimen> result=null;
		try {
			result = client.query(query);
		}
		catch (InvalidQueryException e) {
			System.err.println(e.getMessage());
		}
		ClientUtil.printTerse(result);
		System.out.println("Number of specimens found: " + result.size());		
	}

}
