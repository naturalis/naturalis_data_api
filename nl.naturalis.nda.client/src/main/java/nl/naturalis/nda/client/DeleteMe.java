package nl.naturalis.nda.client;

import org.domainobject.util.debug.BeanPrinter;

import nl.naturalis.nda.domain.MultiMediaObject;

public class DeleteMe {

	public static void main(String[] args) throws NBAResourceException
	{
		ClientConfig cfg = new ClientConfig("http://localhost:8080/v0");
		SpecimenClient client = ClientFactory.getInstance(cfg).createSpecimenClient();
		MultiMediaObject[] multimedia = client.getMultiMedia("ZMA.INS.750532");
		//MultiMediaObject[] multimedia = client.getMultiMedia("U.1040749");
		BeanPrinter.out(multimedia);		
	}

}
