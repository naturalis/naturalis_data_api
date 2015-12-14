package nl.naturalis.nda.elasticsearch.load.ndff;

import static nl.naturalis.nda.elasticsearch.load.ndff.NdffCsvField.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
/**
 * The transformer component for the NDFF specimen import.
 * 
 * @author Ayco Holleman
 *
 */

public class NdffSpecimenTransformer extends AbstractCSVTransformer<NdffCsvField, ESSpecimen> {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
			ScientificName sn = new ScientificName();
			si.setScientificName(sn);
			sn.setFullScientificName(input.get(species_sci));
			// MUST add identification affter setting scientific
			// name, otherwise NullPointerException.
			// TODO refactor in API/domain package
			specimen.addIndentification(si);

			ESGatheringEvent ge = new ESGatheringEvent();
			specimen.setGatheringEvent(ge);
			ge.setDateTimeBegin(getDate(period_start));
			ge.setDateTimeEnd(getDate(period_stop));
			ESGatheringSiteCoordinates coords = new ESGatheringSiteCoordinates();
			ge.setSiteCoordinates(Arrays.asList(coords));
			coords.setGridLatitudeDecimal(getCoordinate(rd_y_5km));
			coords.setGridLongitudeDecimal(getCoordinate(rd_x_5km));
			coords.setGridCellSystem("Amersfoort");
			stats.objectsAccepted++;
			return Arrays.asList(specimen);
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			error(t.toString());
			t.printStackTrace();
			error(input.getLine());
			return null;
		}
	}

	private Date getDate(NdffCsvField field)
	{
		String s = input.get(field);
		if (s != null) {
			try {
				return sdf.parse(s);
			}
			catch (ParseException e) {
				warn("Invalid value for field %s: \"%s\"", field, s);
			}
		}
		return null;
	}

	private Double getCoordinate(NdffCsvField field)
	{
		String s = input.get(field);
		if (s != null) {
			try {
				return Double.valueOf(s);
			}
			catch (NumberFormatException e) {
				warn("Invalid value for field %s: \"%s\"", field, s);
			}
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
