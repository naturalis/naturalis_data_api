package nl.naturalis.nda.client;

import org.domainobject.util.debug.BeanPrinter;

import nl.naturalis.nda.domain.MultiMediaObject;

public class DeleteMe {

	public static void main(String[] args) throws NBAResourceException
	{
		ClientConfig cfg = new ClientConfig("http://localhost:8080/v0");
		MultiMediaClient client = ClientFactory.getInstance(cfg).createMultiMediaClient();
		MultiMediaObject[] multimedia = client.getMultiMediaForSpecimen("ZMA.INS.787862");
		BeanPrinter.out(multimedia);
	}

}
