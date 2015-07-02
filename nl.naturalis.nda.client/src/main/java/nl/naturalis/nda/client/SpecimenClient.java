package nl.naturalis.nda.client;

import org.apache.http.Header;
import org.domainobject.util.debug.BeanPrinter;

public class SpecimenClient extends AbstractClient {

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}


	public boolean exists(String unitID)
	{
		request.setPath("specimen/exists/" + unitID);
		if (!request.execute().isOK()) {
			System.out.println(new String(request.getResponseBody()));
			return false;
		}
		else {
			return false;
		}
	}

}
