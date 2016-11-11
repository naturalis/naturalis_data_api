package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.query.ComparisonOperator.IN;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.LogicalOperator;
import nl.naturalis.nba.api.query.QuerySpec;

public class Test {

	public static void main(String[] args)
	{
		Condition condition1 = new Condition("gatheringEvent.siteCoordinates.geoShape", IN,
				"Netherlands");
		QuerySpec query = new QuerySpec();
		query.addCondition(condition1);
		query.setLogicalOperator(LogicalOperator.OR);
		ClientUtil.printTerse(query);
	}

}
