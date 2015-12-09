package nl.naturalis.nda.elasticsearch.load.ndff;

import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.abundance_min;
import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.ndff_identity;
import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.period_start;
import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.period_stop;
import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.species_sci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;

public class NdffSpecimenTransformer extends AbstractCSVTransformer<NdffCsvField, ESSpecimen> {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");

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
			specimen.setUnitID(objectID);
			specimen.setRecordBasis("humanObservation");
			specimen.setNumberOfSpecimen(getNumberOfSpecimen());
			specimen.setSourceInstitutionID("Dutch Butterfly Conservation");
			specimen.setSourceID("NDFF");
			specimen.setOwner("Dutch Butterfly Conservation");
			specimen.setLicenceType("Copyright");
			specimen.setLicence("CC-BY-NC");
			specimen.setCollectionType("Dutch Butterfly Conservation");

			SpecimenIdentification si = new SpecimenIdentification();
			specimen.addIndentification(si);
			ScientificName sn = new ScientificName();
			si.setScientificName(sn);
			sn.setFullScientificName(input.get(species_sci));

			ESGatheringEvent ge = new ESGatheringEvent();
			specimen.setGatheringEvent(ge);
			String s = null;
			try {
				s = input.get(period_start);
				if (s != null)
					ge.setDateTimeBegin(sdf.parse(s));
			}
			catch (ParseException e) {
				warn("Invalid %s date: \"%s\"", period_start, s);
			}			
			try {
				s = input.get(period_stop);
				if (s != null)
					ge.setDateTimeBegin(sdf.parse(s));
			}
			catch (ParseException e) {
				warn("Invalid %s date: \"%s\"", period_stop, s);
			}

		}
		catch (Throwable t) {
			stats.objectsRejected++;
			error(t.getMessage());
			error(input.getLine());
			return null;
		}

		return null;
	}

	private int getNumberOfSpecimen()
	{
		String s = input.get(abundance_min);
		if (s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			warn("Invalid number for \"%s\": %s", abundance_min, s);
			return 0;
		}
	}

}
