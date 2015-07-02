package nl.naturalis.nda.client;

public class DeleteMe {

	public static void main(String[] args) throws NBAResourceException
	{
		SpecimenClient client = new SpecimenClient(new ClientConfig("http://localhost:8080/v0"));
		client.exists("12345");
	}

}
