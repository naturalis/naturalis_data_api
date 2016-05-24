package nl.naturalis.nba.client;

import static java.lang.System.out;
import static org.domainobject.util.StringUtil.pad;
import static org.domainobject.util.StringUtil.rpad;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import static nl.naturalis.nba.api.query.Operator.*;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
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
		Condition condition1 = new Condition("gatheringEvent.gatheringPersons.fullName", LIKE, "burg");
		QuerySpec query = new QuerySpec();
		query.setCondition(condition1);
		Specimen[] result=null;
		try {
			result = client.query(query);
		}
		catch (InvalidQueryException e) {
			System.err.println(e.getMessage());
		}
		ClientUtil.printTerse(result);
		System.out.println("Number of specimens found: " + result.length);		
	}

}
