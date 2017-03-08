package nl.naturalis.nba.client;

import nl.naturalis.nba.api.INameGroupAccess;

public class NameGroupDemo {


	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		INameGroupAccess client = session.getNameGroupClient();
	}

}
