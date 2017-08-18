package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.etl.ndff.NdffCsvField.abundance_min;
import static nl.naturalis.nba.etl.ndff.NdffCsvField.ndff_identity;
import static nl.naturalis.nba.etl.ndff.NdffCsvField.rd_x_5km;
import static nl.naturalis.nba.etl.ndff.NdffCsvField.rd_y_5km;
import static nl.naturalis.nba.etl.ndff.NdffCsvField.species_sci;

import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
/**
 * The transformer component for the NDFF specimen import.
 * 
 * @author Ayco Holleman
 *
 */

public class NdffSpecimenTransformer extends AbstractCSVTransformer<NdffCsvField, Specimen> {

	//private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	protected List<Specimen> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			Specimen specimen = new Specimen();
			specimen.setSourceSystem(SourceSystem.NDFF);
			specimen.setSourceSystemId(objectID);
			specimen.setUnitID(objectID);
			specimen.setRecordBasis("humanObservation");
			specimen.setNumberOfSpecimen(getNumberOfSpecimen());
			specimen.setSourceInstitutionID("Dutch Butterfly Conservation");
			specimen.setSourceID("NDFF");
			specimen.setOwner("Dutch Butterfly Conservation");
			specimen.setLicenseType("Copyright");
			specimen.setLicense("CC-BY-NC");
			specimen.setCollectionType("Dutch Butterfly Conservation");

			SpecimenIdentification si = new SpecimenIdentification();
			ScientificName sn = new ScientificName();
			si.setScientificName(sn);
			sn.setFullScientificName(input.get(species_sci));
			// MUST add identification affter setting scientific
			// name, otherwise NullPointerException.
			// TODO refactor in API/domain package
			specimen.addIndentification(si);

			GatheringEvent ge = new GatheringEvent();
			specimen.setGatheringEvent(ge);
			//ge.setDateTimeBegin(getDate(period_start));
			//ge.setDateTimeEnd(getDate(period_stop));
			GatheringSiteCoordinates coords = new GatheringSiteCoordinates();
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

//	private Date getDate(NdffCsvField field)
//	{
//		String s = input.get(field);
//		if (s != null) {
//			try {
//				return sdf.parse(s);
//			}
//			catch (ParseException e) {
//				warn("Invalid value for field %s: \"%s\"", field, s);
//			}
//		}
//		return null;
//	}

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
