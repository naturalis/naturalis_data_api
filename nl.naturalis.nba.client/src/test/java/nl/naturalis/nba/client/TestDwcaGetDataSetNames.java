package nl.naturalis.nba.client;

public class TestDwcaGetDataSetNames {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		String[] names = client.dwcaGetDataSetNames();
		ClientUtil.printTerse(names);
	}

}
