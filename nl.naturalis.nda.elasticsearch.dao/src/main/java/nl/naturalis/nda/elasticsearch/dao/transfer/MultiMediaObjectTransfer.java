package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;

public class MultiMediaObjectTransfer {

	private MultiMediaObjectTransfer()
	{
		// Only static method in transfer objects		
	}


	public static MultiMediaObject transfer(ESMultiMediaObject esMmo)
	{
		MultiMediaObject mmo = new MultiMediaObject();
		return mmo;
	}

}
