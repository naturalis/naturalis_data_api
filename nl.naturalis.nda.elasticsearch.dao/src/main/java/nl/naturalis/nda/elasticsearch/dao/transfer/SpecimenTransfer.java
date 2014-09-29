package nl.naturalis.nda.elasticsearch.dao.transfer;

import java.util.List;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class SpecimenTransfer {

	private SpecimenTransfer()
	{
		// Only static method in transfer objects
	}


	public static Specimen transfer(ESSpecimen esSpecimen)
	{
		Specimen specimen = new Specimen();
		return specimen;
	}

}
