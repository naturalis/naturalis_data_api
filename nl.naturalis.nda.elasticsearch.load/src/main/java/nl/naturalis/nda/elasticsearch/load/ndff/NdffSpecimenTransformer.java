package nl.naturalis.nda.elasticsearch.load.ndff;

import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.*;

import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;

public class NdffSpecimenTransformer extends AbstractCSVTransformer<NdffCsvField, ESSpecimen> {

	public NdffSpecimenTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	protected String getObjectID()
	{
		return input.get(ndff_identity);
	}

	@Override
	protected List<ESSpecimen> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			ESSpecimen specimen = new ESSpecimen();
			specimen.setSourceSystem(SourceSystem.NDFF);
			specimen.setSourceSystemId(objectID);
			specimen.setNumberOfSpecimen(getNumberOfSpecimen());
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			error(t.getMessage());
			error(input.getLine());
			return null;
		}

		return null;
	}
	
	private int getNumberOfSpecimen() {
		String s = input.get(count_unit);
		if(s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			warn("Invalid number for \"%s\": %s", count_unit, s);
			return 0;
		}
	}

}
