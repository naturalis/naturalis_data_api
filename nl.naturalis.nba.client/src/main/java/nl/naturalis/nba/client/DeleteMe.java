package nl.naturalis.nba.client;

import java.net.URL;
import java.net.URLEncoder;

public class DeleteMe {

	public static void main(String[] args) throws Exception
	{
		//ClientConfig cfg = new ClientConfig("http://localhost:8080/v0");
		//SpecimenClient client = ClientFactory.getInstance(cfg).createSpecimenClient();
		//MultiMediaObject[] multimedia = client.getMultiMedia("ZMA.INS.750532");
		//MultiMediaObject[] multimedia = client.getMultiMedia("U.1040749");
		//BeanPrinter.out(multimedia);
		System.out.println("*" + URLEncoder.encode("L%20AYCO", "UTF-8") + "*");
		
		URL uri = new URL("http://data/biodatversitydata.nl/L    2345");
		System.out.println(uri.toString());
	}
	

}
