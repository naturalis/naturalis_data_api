package nl.naturalis.nda.export.dwca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.es.types.ESGatheringSiteCoordinates;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

/**
 * <h1>FieldMapping</h1> Description: FieldMapping class for BRAHMS, GEOLOGY and
 * ZOOLOGY class
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 01-07-2015
 * 
 */
public class Fieldmapping {

	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	private static final String EMPTY_STRING = "";

	static SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");

	private static String specificEpithet = "";
	private static String fullScientificName = "";
	private static String infraSpecificEpithet = "";
	private static String taxonRank = "";
	private static String family = "";
	private static String genusName = "";
	private static String order = "";
	private static String dateiden = "";
	private static String authorVerbatim = "";
	private static String subGenusName = "";

	private static int identification = 0;
	private static List<String> allFalseList = new ArrayList<>();

	public static String toString(boolean value)
	{
		return value ? "true" : "false";
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Get ID value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setDummyValue(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * 00_Dummy0 is ID
		 */
		if (specimen.getSourceSystemId() != null) {
			dataRow.add(specimen.getSourceSystemId());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get RecordBasis value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setBasisOfRecord(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getRecordBasis() != null) {
			dataRow.add(specimen.getRecordBasis());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get BasisOf record value For BRAHMS
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setBasisOfRecord_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getRecordBasis() != null
				&& specimen.getRecordBasis().contains("photo(copy) of herbarium sheet")
				&& specimen.getRecordBasis().contains("Illustration")
				&& specimen.getRecordBasis().contains("Photographs, negatives")
				&& specimen.getRecordBasis().contains("DNA sample from sheet")
				&& specimen.getRecordBasis().contains("Slides")
				&& specimen.getRecordBasis().contains("Observation")) {
			System.out.println("Not included in Brahms CSV: " + specimen.getRecordBasis());
			logger.info("Not included in Brahms CSV: " + specimen.getRecordBasis());
			return;
		}
		else if (specimen.getRecordBasis() != null) {
			dataRow.add("PreservedSpecimen");
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get catalogNumber value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setCatalogNumber(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getSourceSystemId() != null) {
			dataRow.add(specimen.getSourceSystemId());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get ClassName value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setClassName(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		/*
		 * if (getIdentificationsPreferred(specimen)) { if(className != null) {
		 * dataRow.add(className); } else { dataRow.add(EMPTY_STRING); } }
		 */

		int size = specimen.getIdentifications().size();
		String classNameValue = "";
		for (int cnt = 0; cnt < size; cnt++) {
			if (specimen.getIdentifications().get(cnt).isPreferred()) {
				if (specimen.getIdentifications().get(cnt).getDefaultClassification() != null) {
					classNameValue = specimen.getIdentifications().get(cnt)
							.getDefaultClassification().getClassName();
				}
			}
			else {
				if (specimen.getIdentifications().get(cnt).getDefaultClassification() != null) {
					classNameValue = specimen.getIdentifications().get(cnt)
							.getDefaultClassification().getClassName();
				}
			}
		}

		if (classNameValue != null) {
			dataRow.add(classNameValue);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Get Classname for BRAHMS
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setClassName_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * 03_DummyDefault class
		 */
		dataRow.add(EMPTY_STRING);
	}

	/**
	 * Get CollectionType value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setCollectionType(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getCollectionType() != null) {
			dataRow.add(specimen.getCollectionType());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Get CollectionCode value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setCollectionCode_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		dataRow.add("Botany");
	}

	/**
	 * Get Continent value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setContinent(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getWorldRegion() != null) {
			dataRow.add(specimen.getGatheringEvent().getWorldRegion());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Country value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setCountry(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getCountry() != null) {
			dataRow.add(specimen.getGatheringEvent().getCountry());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get County value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setCounty(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getGatheringEvent().getCity() != null) {
			dataRow.add(specimen.getGatheringEvent().getCity());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get DateIndentified value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	@SuppressWarnings("null")
	public static void setDateIndentified(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();
		Date datumidentified = new Date();
		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				/*
				 * dateiden = dateidentified.format(specimen
				 * .getIdentifications().get(i).getDateIdentified());
				 */
				datumidentified = specimen.getIdentifications().get(i).getDateIdentified();
				if (datumidentified == null && list.contains("true")) {
					datumidentified = null;
				}
				else if (datumidentified != null && list.contains("true")) {
					datumidentified = specimen.getIdentifications().get(i).getDateIdentified();
				}
			}
			else {
				if (datumidentified != null && list.isEmpty()) {
					datumidentified = specimen.getIdentifications().get(i).getDateIdentified();
				}
				if (datumidentified == null && list.isEmpty()) {
					datumidentified = null;
				}
			}
		}

		if (datumidentified != null) {
			dataRow.add(dateidentified.format(datumidentified));
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if
		 * (specimen.getIdentifications().iterator().next().getDateIdentified()
		 * != null && specimen.getIdentifications().iterator().next()
		 * .isPreferred() == true) { SimpleDateFormat dateidentified = new
		 * SimpleDateFormat("yyyy-MM-dd"); String dateiden = dateidentified
		 * .format(specimen.getIdentifications().iterator().next()
		 * .getDateIdentified()); dataRow.add(dateiden); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */

	}

	/**
	 * Get DateIndentified value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setDateIndentified_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDateIdentified() != null) {
			SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
			String dateiden = dateidentified
					.format(specimen.getIdentifications().iterator().next().getDateIdentified());
			dataRow.add(dateiden);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get LatitudeDecimal value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setLatitudeDecimal(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
					.getLatitudeDecimal() != null) {
				dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates()
						.iterator().next().getLatitudeDecimal()));
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get LongituDecimal value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setLongitudeDecimal(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().iterator().next()
					.getLongitudeDecimal() != null) {
				dataRow.add(String.format("%s", specimen.getGatheringEvent().getSiteCoordinates()
						.iterator().next().getLongitudeDecimal()));
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Eventdate value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setEvendate(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/*
		 * if BeginDate is equal to EndDate then only the value of BeginDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		}
		else if (specimen.getGatheringEvent().getDateTimeBegin()
				.equals(specimen.getGatheringEvent().getDateTimeEnd())
				&& specimen.getGatheringEvent().getDateTimeBegin() != null) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if only begindate has a value then get the value of begindate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			String dateEnd = EMPTY_STRING;
			dataRow.add(dateEnd);
		}
	}

	/**
	 * Get Eventdate value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setEvendate_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			/*
			 * logger.debug("Begindate and EndDate has value and not equal ");
			 * logger.debug("Begindate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 * logger.debug("Enddate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 */
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}

		/*
		 * if BeginDate is equal to EndDate then only the value of BeginDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		}
		else if (specimen.getGatheringEvent().getDateTimeBegin()
				.equals(specimen.getGatheringEvent().getDateTimeEnd())
				&& specimen.getGatheringEvent().getDateTimeBegin() != null) {
			/*
			 * logger.debug(
			 * "BeginDate is equal to EndDate then only the value of BeginDate "
			 * ); logger.debug("Begindate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 * logger.debug("Enddate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 */
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}

		/*
		 * if only begindate has a value then get the value of begindate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			/*
			 * logger.debug(
			 * "only begindate has a value then get the value of begindate");
			 * logger.debug("Begindate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 * logger.debug("Enddate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 */
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			/*
			 * logger.debug(
			 * "EndDate has a value and Begindate has no value set the value of null for Enddate"
			 * ); logger.debug("Begindate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 * logger.debug("Enddate: " +
			 * specimen.getGatheringEvent().getDateTimeBegin());
			 */
			String dateEnd = EMPTY_STRING;
			dataRow.add(dateEnd);
		}
	}

	/**
	 * Get Family value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setFamily(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				family = specimen.getIdentifications().get(i).getDefaultClassification()
						.getFamily();
				if (family != null && list.contains("true")) {
					family = specimen.getIdentifications().get(i).getDefaultClassification()
							.getFamily();
				}
				else if (family == null && list.contains("true")) {
					family = specimen.getIdentifications().get(i).getDefaultClassification()
							.getFamily();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getDefaultClassification()
						.getFamily() != null && list.isEmpty()) {
					family = specimen.getIdentifications().get(i).getDefaultClassification()
							.getFamily();
				}
			}
		}

		if (family != null) {
			dataRow.add(family);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (specimen.getIdentifications().iterator().next()
		 * .getDefaultClassification().getFamily() != null &&
		 * specimen.getIdentifications().iterator().next() .isPreferred() ==
		 * true) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getDefaultClassification().getFamily()); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get Family value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setFamily_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDefaultClassification()
				.getFamily() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification()
					.getFamily());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Genus value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setGenus(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				genusName = specimen.getIdentifications().get(i).getScientificName()
						.getGenusOrMonomial();
				if (genusName != null && list.contains("true")) {
					genusName = specimen.getIdentifications().get(i).getScientificName()
							.getGenusOrMonomial();
				}
				else if (genusName == null && list.contains("true")) {
					genusName = specimen.getIdentifications().get(i).getScientificName()
							.getGenusOrMonomial();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName()
						.getGenusOrMonomial() != null && list.isEmpty()) {
					genusName = specimen.getIdentifications().get(i).getScientificName()
							.getGenusOrMonomial();
				}
			}
		}

		if (genusName != null) {
			dataRow.add(genusName);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) {
		 * 
		 * if (genusName != null) { dataRow.add(genusName); } else {
		 * dataRow.add(EMPTY_STRING); } }
		 */

		/*
		 * int cnt = specimen.getIdentifications().size();
		 * 
		 * if (cnt > 1) { for (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred(); if (preferred) {
		 * String genusName = specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial(); if (preferred && genusName
		 * != null) { dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial()); break; } else if
		 * (preferred == false && genusName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial()); break; } else if
		 * (preferred && genusName == null) { dataRow.add(EMPTY_STRING); break;
		 * } } } } else if (cnt == 1) { for (int i = 0; i < cnt; i++) { boolean
		 * preferred = specimen.getIdentifications().get(i) .isPreferred(); if
		 * (preferred || preferred == false) { String genusName =
		 * specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial(); if (preferred == false &&
		 * genusName != null) { dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial()); break; } else if
		 * (genusName == null && preferred) { dataRow.add(EMPTY_STRING); break;
		 * 
		 * } else if (preferred && genusName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getGenusOrMonomial()); break; } else { if
		 * (genusName == null && preferred == false) {
		 * dataRow.add(EMPTY_STRING); break; } } } } }
		 */
	}

	/**
	 * Get Genus value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setGenus_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME,
		 * "13_GenusOrMonomial")) {
		 */
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getGenusOrMonomial() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getGenusOrMonomial());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Get Geodeticdatum value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setGeodeticDatum(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/**
	 * Get Habitat value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setHabitat(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/**
	 * Get HigherClassification value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setHigherClassification(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	/**
	 * Get HigherClassification value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setHigherClassification_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null) {
			String kingdom = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getKingdom();
			String classname = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getClassName();
			String order = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getOrder();
			String family = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily();
			if (kingdom != null && classname != null && order != null && family != null) {
				dataRow.add(kingdom + "|" + classname + "|" + order + "|" + family);
			}
			else if (kingdom == null) {
				dataRow.add(classname + "|" + order + "|" + family);
			}
			else if (classname == null) {
				dataRow.add(kingdom + "|" + order + "|" + family);
			}
			else if (order == null) {
				dataRow.add(kingdom + "|" + classname + "|" + family);
			}
			else if (family == null) {
				dataRow.add(kingdom + "|" + classname + "|" + order);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get IndentifiersFullname value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setIdentifiersFullName(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		List<String> listAgentFullname = new ArrayList<>();
		if (specimen.getIdentifications().iterator().next().getIdentifiers() != null) {
			Iterator<Agent> identifiedByIterator = specimen.getIdentifications().iterator().next()
					.getIdentifiers().iterator();
			while (identifiedByIterator.hasNext()) {
				Agent ag = identifiedByIterator.next();
				if (ag instanceof Person) {
					Person per = (Person) ag;
					/* NDA-303/372 */
					listAgentFullname.add(per.getFullName());
				}

				if (listAgentFullname.size() > 1) {
					listAgentFullname.add(" | ");
				}
			}

			if (listAgentFullname.size() > 0) {
				String resultAgentFullName = listAgentFullname.toString().replace(",", " ")
						.replace("[", " ").replace("]", " ").trim();
				dataRow.add(resultAgentFullName);
				/* NDA-303/372 */
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get IndentifiersFullname value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setIdentifiersFullName_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getIdentifiers() != null) {
			List<String> listAgentFullname = new ArrayList<>();
			Iterator<Agent> identifiedByIterator = specimen.getIdentifications().iterator().next()
					.getIdentifiers().iterator();
			while (identifiedByIterator.hasNext()) {
				Agent ag = identifiedByIterator.next();
				if (ag instanceof Person) {
					Person per = (Person) ag;
					listAgentFullname.add(per.getFullName());
				}

				if (listAgentFullname.size() > 1) {
					listAgentFullname.add(" | ");
				}
			}

			if (listAgentFullname.size() > 0) {
				String resultAgentFullName = listAgentFullname.toString().replace(",", " ")
						.replace("[", " ").replace("]", " ").trim();
				dataRow.add(resultAgentFullName);
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get NumberOfSpecimen value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setNumberOfSpecimen(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (Integer.toString(specimen.getNumberOfSpecimen()) != null
				&& specimen.getNumberOfSpecimen() > 0) {
			dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Get InformationWithHeld value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setInformationWithHeld(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	/**
	 * Get InfraspecificEpithet value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setInfraspecificEpithet(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				infraSpecificEpithet = specimen.getIdentifications().get(i).getScientificName()
						.getInfraspecificEpithet();
				if ((infraSpecificEpithet != null && list.contains("true"))
						|| (infraSpecificEpithet == null && list.contains("true"))) {
					infraSpecificEpithet = specimen.getIdentifications().get(i).getScientificName()
							.getInfraspecificEpithet();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName()
						.getInfraspecificEpithet() != null && list.isEmpty()) {
					infraSpecificEpithet = specimen.getIdentifications().get(i).getScientificName()
							.getInfraspecificEpithet();
				}
			}
		}

		if (infraSpecificEpithet != null) {
			dataRow.add(infraSpecificEpithet);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) { if (infraSpecificEpithet
		 * != null) { dataRow.add(infraSpecificEpithet); } else {
		 * dataRow.add(EMPTY_STRING); } }
		 */

		/*
		 * int cnt = specimen.getIdentifications().size(); if (cnt > 1) { for
		 * (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred(); if (preferred) {
		 * String infraSpecificeEpithet = specimen
		 * .getIdentifications().get(i).getScientificName()
		 * .getInfraspecificEpithet();
		 * 
		 * if (preferred && infraSpecificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getInfraspecificEpithet()); break; } else if
		 * (preferred == false && infraSpecificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getInfraspecificEpithet()); break; } else if
		 * (preferred == false && infraSpecificeEpithet == null) {
		 * dataRow.add(EMPTY_STRING); break; } else if (preferred &&
		 * infraSpecificeEpithet == null) { dataRow.add(EMPTY_STRING); break; }
		 * } } } else if (cnt == 1) { for (int i = 0; i < cnt; i++) { boolean
		 * preferred = specimen.getIdentifications().get(i) .isPreferred(); if
		 * (preferred || !preferred) { String infraSpecificeEpithet = specimen
		 * .getIdentifications().get(i).getScientificName()
		 * .getInfraspecificEpithet();
		 * 
		 * if (preferred == false && infraSpecificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getInfraspecificEpithet()); break; } else if
		 * (preferred && infraSpecificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getInfraspecificEpithet()); break; } else if
		 * (preferred && infraSpecificeEpithet == null) {
		 * dataRow.add(EMPTY_STRING); break; } else if (preferred == false &&
		 * infraSpecificeEpithet == null) { dataRow.add(EMPTY_STRING); break; }
		 * } } }
		 */

		/*
		 * if
		 * (specimen.getIdentifications().iterator().next().getScientificName()
		 * .getInfraspecificEpithet() != null &&
		 * specimen.getIdentifications().iterator().next() .isPreferred() ==
		 * true) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getScientificName().getInfraspecificEpithet()); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get InfraspecificEpithet value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setInfraspecificEpithet_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getInfraspecificEpithet() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getInfraspecificEpithet());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Island value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setIsland(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getGatheringEvent().getIsland() != null) {
			dataRow.add(specimen.getGatheringEvent().getIsland());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get InstitudeCode value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setInstitudeCode(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getSourceInstitutionID().contains("Naturalis")) {
			dataRow.add(specimen.getSourceInstitutionID().substring(0, 9));
		}
		else if (specimen.getSourceInstitutionID() != null) {
			dataRow.add(specimen.getSourceInstitutionID());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Kingdom value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setKingdom(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		/*
		 * List<String> list = new ArrayList<>(); int size =
		 * specimen.getIdentifications().size();
		 * 
		 * for (int i = 0; i < size; i++) { if
		 * (specimen.getIdentifications().get(i).isPreferred()) {
		 * list.add(toString(specimen.getIdentifications().get(i)
		 * .isPreferred())); if (specimen.getIdentifications().get(i)
		 * .getDefaultClassification() != null) { kingDom =
		 * specimen.getIdentifications
		 * ().get(i).getDefaultClassification().getKingdom(); }
		 * 
		 * if (kingDom != null && list.contains("true")) { kingDom =
		 * specimen.getIdentifications().get(i)
		 * .getDefaultClassification().getKingdom(); } else if(kingDom == null
		 * && list.contains("true")) { kingDom = EMPTY_STRING; }
		 * 
		 * 
		 * } else { if (specimen.getIdentifications().get(i)
		 * .getDefaultClassification() != null && list.isEmpty()) { kingDom =
		 * specimen.getIdentifications().get(i)
		 * .getDefaultClassification().getKingdom(); } } }
		 * 
		 * if (kingDom != null) { dataRow.add(kingDom); } else {
		 * dataRow.add(EMPTY_STRING); }
		 * 
		 * list.clear();
		 */
		if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null
				&& specimen.getIdentifications().iterator().next().isPreferred()) {
			dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification()
					.getKingdom());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

	}

	/**
	 * Get Kingdom value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setKingdom_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDefaultClassification() != null) {
			String family = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily();
			if (family != null) {
				if (family.contains("Fungi")) {
					dataRow.add("fungi");
				}
				else {
					dataRow.add("Plantae");
				}
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get PhaseOrStage value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setPhaseOrStage(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getPhaseOrStage() != null) {
			dataRow.add(specimen.getPhaseOrStage().toString());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Locality value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setLocality(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getLocality() != null) {
			String localityResult = specimen.getGatheringEvent().getLocality().replace('"', ' ')
					.replace(' ', ' ').replace('\t', ' ').replace("\r", "").replace("\n", "")
					.trim();
			dataRow.add(localityResult);
			/* NDA-303/372 */
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Get MaximumElevationInMeters value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setMaximumElevationInMeters(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/**
	 * Get MinimumElevationInMeters value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setMinimumElevationInMeters(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/**
	 * Get NomenclaturalCode value for Zoology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setNomenclaturalCode_Zoology(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		dataRow.add("ICZN");
	}

	@SuppressWarnings("unused")
	/**
	 * Get NomenclaturalCode value Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setNomenclaturalCode_Geology(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/**
	 * Get NomenclaturalCode value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen classn
	 */
	public static void setNomenclaturalCode_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		dataRow.add("ICN");
	}

	/**
	 * Get OccurrenceID value for Brahms, Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setOccurrenceID(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * NDA 407 only unitGuid
		 */
		if (specimen.getUnitGUID() != null) {
			dataRow.add(specimen.getUnitGUID());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Order value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setOrder(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				order = specimen.getIdentifications().get(i).getDefaultClassification().getOrder();

				if ((order != null && list.contains("true"))
						|| (order == null && list.contains("true"))) {
					order = specimen.getIdentifications().get(i).getDefaultClassification()
							.getOrder();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getDefaultClassification()
						.getOrder() != null && list.isEmpty()) {
					order = specimen.getIdentifications().get(i).getDefaultClassification()
							.getOrder();
				}
			}
		}

		if (order != null) {
			dataRow.add(order);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (specimen.getIdentifications().iterator().next()
		 * .getDefaultClassification().getOrder() != null &&
		 * specimen.getIdentifications().iterator().next() .isPreferred() ==
		 * true) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getDefaultClassification().getOrder()); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get Order value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setOrder_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDefaultClassification()
				.getOrder() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification()
					.getOrder());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Phylum value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setPhylum(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getDefaultClassification()
				.getPhylum() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification()
					.getPhylum());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get PreparationType value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setPreparationType(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getPreparationType() != null) {
			dataRow.add(specimen.getPreparationType());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get GatheringAgents_FullName value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setGatheringAgents_FullName(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		List<String> listFullname = new ArrayList<>();
		if (specimen.getGatheringEvent().getGatheringPersons() != null) {
			Iterator<Person> fullnameIterator = specimen.getGatheringEvent().getGatheringPersons()
					.iterator();
			while (fullnameIterator.hasNext()) {
				listFullname.add(fullnameIterator.next().getFullName());

				if (specimen.getGatheringEvent().getGatheringPersons().size() > 1) {
					listFullname.add(" | ");
				}
			}

			if (listFullname.size() > 0) {
				String resultFullName = listFullname.toString().replace(",", " ").replace("[", " ")
						.replace("]", " ").trim();
				dataRow.add(resultFullName);
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get FullScientificName value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setFullScientificName(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				fullScientificName = specimen.getIdentifications().get(i).getScientificName()
						.getFullScientificName();
				if ((fullScientificName != null && list.contains("true"))
						|| (fullScientificName == null && list.contains("true"))) {
					fullScientificName = specimen.getIdentifications().get(i).getScientificName()
							.getFullScientificName();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName()
						.getFullScientificName() != null && list.isEmpty()) {
					fullScientificName = specimen.getIdentifications().get(i).getScientificName()
							.getFullScientificName();
				}
			}
		}

		if (fullScientificName != null) {
			dataRow.add(fullScientificName);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) { if (fullScientificName
		 * != null) { dataRow.add(fullScientificName); } else {
		 * dataRow.add(EMPTY_STRING); } }
		 */

		/*
		 * int cnt = specimen.getIdentifications().size(); if (cnt > 1) { for
		 * (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred(); if (preferred) {
		 * String scientificName = specimen.getIdentifications()
		 * .get(i).getScientificName().getFullScientificName(); if (preferred &&
		 * scientificName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getFullScientificName()); break; } else if
		 * (preferred == false && scientificName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getFullScientificName()); break; } else if
		 * (preferred && scientificName == null) { dataRow.add(EMPTY_STRING);
		 * break; } else if (preferred == false && scientificName == null) {
		 * dataRow.add(EMPTY_STRING); break; } } } } else if (cnt == 1) { for
		 * (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred();
		 * 
		 * if (preferred || !preferred) { String scientificName =
		 * specimen.getIdentifications()
		 * .get(i).getScientificName().getFullScientificName();
		 * 
		 * if (preferred == false && scientificName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getFullScientificName()); break; } else if
		 * (preferred && scientificName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getFullScientificName()); break; } else if
		 * (preferred && scientificName == null) { dataRow.add(EMPTY_STRING);
		 * break; } else if (preferred == false && scientificName == null) {
		 * dataRow.add(EMPTY_STRING); break; } } } }
		 */

		/*
		 * if
		 * (specimen.getIdentifications().iterator().next().getScientificName()
		 * .getFullScientificName() != null &&
		 * specimen.getIdentifications().iterator().next() .isPreferred() ==
		 * true) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getScientificName().getFullScientificName()); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get FullScientificName value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setFullScientificName_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getFullScientificName() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getFullScientificName());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get AuthorshipVerbatim value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setAuthorshipVerbatim(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				authorVerbatim = specimen.getIdentifications().get(i).getScientificName()
						.getAuthorshipVerbatim();
				if (authorVerbatim != null && list.contains("true")) {
					authorVerbatim = specimen.getIdentifications().get(i).getScientificName()
							.getAuthorshipVerbatim();
				}
				else if (authorVerbatim == null && list.contains("true")) {
					authorVerbatim = specimen.getIdentifications().get(i).getScientificName()
							.getAuthorshipVerbatim();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName()
						.getAuthorshipVerbatim() != null && list.isEmpty()) {
					authorVerbatim = specimen.getIdentifications().get(i).getScientificName()
							.getAuthorshipVerbatim();
				}
			}
		}

		if (authorVerbatim != null) {
			dataRow.add(authorVerbatim);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) { if (authorVerbatim !=
		 * null) { dataRow.add(authorVerbatim); } else {
		 * dataRow.add(EMPTY_STRING); } }
		 */

		/*
		 * int cnt = specimen.getIdentifications().size(); if (cnt > 1) { for
		 * (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred(); if (preferred) {
		 * String authorName = specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim(); if (preferred &&
		 * authorName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim()); break; } else if
		 * (preferred == false && authorName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim()); break; } else if
		 * (preferred && authorName == null) { dataRow.add(EMPTY_STRING); break;
		 * } else if (preferred == false && authorName == null) {
		 * dataRow.add(EMPTY_STRING); break; } }
		 * 
		 * } } else if (cnt == 1) { for (int i = 0; i < cnt; i++) { boolean
		 * preferred = specimen.getIdentifications().get(i) .isPreferred();
		 * 
		 * if (preferred || !preferred) { String authorName =
		 * specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim(); if (preferred &&
		 * authorName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim()); break; } else if
		 * (preferred == false && authorName != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getAuthorshipVerbatim()); break; } else if
		 * (preferred && authorName == null) { dataRow.add(EMPTY_STRING); break;
		 * } else if (preferred == false && authorName == null) {
		 * dataRow.add(EMPTY_STRING); break; } } } }
		 */
		/*
		 * if
		 * (specimen.getIdentifications().iterator().next().getScientificName()
		 * .getAuthorshipVerbatim() != null &&
		 * specimen.getIdentifications().iterator().next() .isPreferred() ==
		 * true) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getScientificName().getAuthorshipVerbatim()); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get AuthorshipVerbatim value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setAuthorshipVerbatim_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getAuthorshipVerbatim() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getAuthorshipVerbatim());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Sex value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setSex(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getSex() != null) {
			dataRow.add(specimen.getSex().toString());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/*
	 * public static boolean getPreferred(ESSpecimen specimen) { int cnt =
	 * specimen.getIdentifications().size(); for (int i = 0; i < cnt; i++) {
	 * allValueList.add(toString(specimen.getIdentifications().get(i)
	 * .isPreferred())); if (allValueList.contains("true")) { return true; } }
	 * return false; }
	 */

	public static boolean getPreferredFalse(ESSpecimen specimen)
	{
		int cnt = specimen.getIdentifications().size();
		for (int i = 0; i < cnt; i++) {
			if (!specimen.getIdentifications().get(i).isPreferred()) {
				allFalseList.add(toString(specimen.getIdentifications().get(i).isPreferred()));
			}
			if (allFalseList.contains("false")) {
				return true;
			}
		}
		return false;
	}

	/*
	 * public static boolean getPreferredtrue(ESSpecimen esSpecimen) { int cnt =
	 * esSpecimen.getIdentifications().size(); for (int i = 0; i < cnt; i++) {
	 * if (esSpecimen.getIdentifications().get(i).isPreferred()) { return true;
	 * } } return false; }
	 */

	/*
	 * public static boolean getIdentificationsPreferred(ESSpecimen specimen) {
	 * List<String> list = new ArrayList<>(); int cnt =
	 * specimen.getIdentifications().size(); allFalseList.clear();
	 * 
	 * for (int i = 0; i < cnt; i++) { trueList.clear(); specificEpithet = "";
	 * fullScientificName = ""; genusName = ""; subGenusName = "";
	 * authorVerbatim = ""; infraSpecificEpithet = ""; className = "";
	 * 
	 * preferred = specimen.getIdentifications().get(i).isPreferred();
	 * 
	 * if (preferred) { trueList.add(toString(preferred)); }
	 * 
	 * if (trueList.contains("true")) {
	 * 
	 * System.out.println(specimen.getUnitID() + " Test true: " + 1); if
	 * (specimen.getIdentifications().get(i).getScientificName()
	 * .getSpecificEpithet() != null) { specificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getSpecificEpithet(); } else { specificEpithet =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getFullScientificName() != null) { fullScientificName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getFullScientificName(); } else { fullScientificName
	 * = EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getGenusOrMonomial() != null) { genusName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getGenusOrMonomial(); } else { genusName =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getSubgenus() != null) { subGenusName =
	 * specimen.getIdentifications().get(i) .getScientificName().getSubgenus();
	 * } else subGenusName = EMPTY_STRING;
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getAuthorshipVerbatim() != null) { authorVerbatim =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getAuthorshipVerbatim(); } else { authorVerbatim =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getInfraspecificEpithet() != null) { infraSpecificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getInfraspecificEpithet(); } else {
	 * infraSpecificEpithet = EMPTY_STRING; } return true; } else if
	 * (getPreferredFalse(specimen) && trueList.isEmpty()) {
	 * System.out.println(specimen.getUnitID() + " Test false: " + 2);
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getSpecificEpithet() != null) { specificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getSpecificEpithet(); } else { specificEpithet =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getFullScientificName() != null) { fullScientificName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getFullScientificName(); } else { fullScientificName
	 * = EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getGenusOrMonomial() != null) { genusName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getGenusOrMonomial(); } else { genusName =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getSubgenus() != null) { subGenusName =
	 * specimen.getIdentifications().get(i) .getScientificName().getSubgenus();
	 * } else subGenusName = EMPTY_STRING;
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getAuthorshipVerbatim() != null) { authorVerbatim =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getAuthorshipVerbatim(); } else { authorVerbatim =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getInfraspecificEpithet() != null) { infraSpecificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getInfraspecificEpithet(); } else {
	 * infraSpecificEpithet = EMPTY_STRING; } return true; } else if (cnt == i +
	 * 1 && getPreferredFalse(specimen)) {
	 * 
	 * System.out.println(specimen.getUnitID() + " Test false: " + 3);
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getSpecificEpithet() != null) { specificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getSpecificEpithet(); } else { specificEpithet =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getFullScientificName() != null) { fullScientificName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getFullScientificName(); } else { fullScientificName
	 * = EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getGenusOrMonomial() != null) { genusName =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getGenusOrMonomial(); } else { genusName =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getSubgenus() != null) { subGenusName =
	 * specimen.getIdentifications().get(i) .getScientificName().getSubgenus();
	 * } else subGenusName = EMPTY_STRING;
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getAuthorshipVerbatim() != null) { authorVerbatim =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getAuthorshipVerbatim(); } else { authorVerbatim =
	 * EMPTY_STRING; }
	 * 
	 * if (specimen.getIdentifications().get(i).getScientificName()
	 * .getInfraspecificEpithet() != null) { infraSpecificEpithet =
	 * specimen.getIdentifications().get(i)
	 * .getScientificName().getInfraspecificEpithet(); } else {
	 * infraSpecificEpithet = EMPTY_STRING; } return true; } } return false;
	 * 
	 * }
	 */

	/**
	 * Get SpecificEpithet value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setSpecificEpithet(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				specificEpithet = specimen.getIdentifications().get(i).getScientificName()
						.getSpecificEpithet();
				if (specificEpithet != null && list.contains("true")) {
					specificEpithet = specimen.getIdentifications().get(i).getScientificName()
							.getSpecificEpithet();
				}
				else if (specificEpithet == null && list.contains("true")) {
					specificEpithet = specimen.getIdentifications().get(i).getScientificName()
							.getSpecificEpithet();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName()
						.getSpecificEpithet() != null && list.isEmpty()) {
					specificEpithet = specimen.getIdentifications().get(i).getScientificName()
							.getSpecificEpithet();
				}
			}
		}

		if (specificEpithet != null) {
			dataRow.add(specificEpithet);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) { if (specificEpithet !=
		 * null) { dataRow.add(specificEpithet); } else {
		 * dataRow.add(EMPTY_STRING); }
		 */

		/*
		 * if (cnt > 1) { for (int i = 0; i < cnt; i++) { preferred =
		 * specimen.getIdentifications().get(i) .isPreferred();
		 * 
		 * if (preferred) { specificeEpithet =
		 * specimen.getIdentifications().get(i)
		 * .getScientificName().getSpecificEpithet();
		 * 
		 * if (specificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSpecificEpithet()); break; } else if
		 * (specificeEpithet == null) { dataRow.add(EMPTY_STRING); break; } } }
		 * 
		 * } else if (cnt == 1) { for (int i = 0; i < cnt; i++) { boolean
		 * preferred = specimen.getIdentifications().get(i) .isPreferred();
		 * 
		 * if (preferred || !preferred) { preferredTrue = preferred;
		 * specificeEpithet = specimen.getIdentifications().get(i)
		 * .getScientificName().getSpecificEpithet();
		 * 
		 * if (preferred && specificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSpecificEpithet()); break; } else if
		 * (preferred == false && specificeEpithet != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSpecificEpithet()); break; } else if
		 * (preferred && specificeEpithet == null) { dataRow.add(EMPTY_STRING);
		 * break; } else if (preferred == false && specificeEpithet == null) {
		 * dataRow.add(EMPTY_STRING); break; } } } }
		 */
		// }
	}

	/**
	 * Get SpecificEpithet value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setSpecificEpithet_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSpecificEpithet() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getSpecificEpithet());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get ProvinceState value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setProvinceState(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getProvinceState() != null
				&& !specimen.getGatheringEvent().getProvinceState().contains("0")) {
			/* NDA-303/372 */
			dataRow.add(specimen.getGatheringEvent().getProvinceState());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get SubGenus value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setSubGenus(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				subGenusName = specimen.getIdentifications().get(i).getScientificName()
						.getSubgenus();
				if (subGenusName != null && list.contains("true")) {
					subGenusName = specimen.getIdentifications().get(i).getScientificName()
							.getSubgenus();
				}
				else if (subGenusName == null && list.contains("true")) {
					subGenusName = specimen.getIdentifications().get(i).getScientificName()
							.getSubgenus();
				}
			}
			else {
				if (specimen.getIdentifications().get(i).getScientificName().getSubgenus() != null
						&& list.isEmpty()) {
					subGenusName = specimen.getIdentifications().get(i).getScientificName()
							.getSubgenus();
				}
			}
		}

		if (subGenusName != null) {
			dataRow.add(subGenusName);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (getIdentificationsPreferred(specimen)) { if (subGenusName !=
		 * null) { dataRow.add(subGenusName); } else {
		 * dataRow.add(EMPTY_STRING); } }
		 */
		/*
		 * String subGenus = "";
		 * 
		 * int cnt = specimen.getIdentifications().size(); if (cnt > 1) {
		 * trueList.clear(); for (int i = 0; i < cnt; i++) { boolean preferred =
		 * specimen.getIdentifications().get(i) .isPreferred();
		 * 
		 * if (preferred) { trueList.add(toString(preferred)); }
		 * 
		 * if (preferred) { subGenus = specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus();
		 * 
		 * if (subGenus != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus()); break; }
		 * 
		 * if (subGenus == null) { dataRow.add(EMPTY_STRING); break; } }
		 * 
		 * 
		 * else if (!preferredFalse) { subGenus =
		 * specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus(); if (subGenus != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus()); break; }
		 * 
		 * if (subGenus == null) { dataRow.add(EMPTY_STRING); break; } }
		 * 
		 * } } else if (cnt == 1) { trueList.clear(); for (int i = 0; i < cnt;
		 * i++) { boolean preferred = specimen.getIdentifications().get(i)
		 * .isPreferred();
		 * 
		 * if (preferred) { trueList.add(toString(preferred)); }
		 * 
		 * if (preferred || !preferred) { subGenus =
		 * specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus();
		 * 
		 * if (preferred && subGenus != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus()); break; } else if (!preferred &&
		 * subGenus != null) { dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus()); break; } else if (preferred &&
		 * subGenus == null) { dataRow.add(EMPTY_STRING); break; } else if
		 * (!preferred && subGenus == null) { dataRow.add(EMPTY_STRING); break;
		 * } } else if (!preferredFalse) { if (subGenus != null) {
		 * dataRow.add(specimen.getIdentifications().get(i)
		 * .getScientificName().getSubgenus()); break; } else if (subGenus ==
		 * null) { dataRow.add(EMPTY_STRING); break; } }
		 * 
		 * 
		 * } }
		 *//*
			 * if (specimen.getIdentifications().iterator().next().
			 * getScientificName () .getSubgenus() != null &&
			 * specimen.getIdentifications().iterator().next() .isPreferred() ==
			 * true) {
			 * dataRow.add(specimen.getIdentifications().iterator().next()
			 * .getScientificName().getSubgenus()); } else {
			 * dataRow.add(EMPTY_STRING); }
			 */
	}

	/**
	 * Get SubGenus value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setSubGenus_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSubgenus() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next().getScientificName()
					.getSubgenus());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get Taxonrank value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setTaxonrank(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				if (taxonRank != null && list.contains("true")) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
				else if (taxonRank == null && list.contains("true")) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
			}
			else {
				if (specimen.getIdentifications().iterator().next().getTaxonRank() != null
						&& list.isEmpty()) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
			}
		}

		if (taxonRank != null) {
			dataRow.add(taxonRank);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (specimen.getIdentifications().iterator().next().getTaxonRank() !=
		 * null && specimen.getIdentifications().iterator().next()
		 * .isPreferred() == true) {
		 * dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getTaxonRank()); } else { dataRow.add(EMPTY_STRING); }
		 */
	}

	/**
	 * Get Taxonrank value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setTaxonrank_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications().iterator().next().getTaxonRank() != null) {
			if (specimen.getIdentifications().iterator().next().getTaxonRank().contains("subsp.")) {
				dataRow.add("subspecies");
			}
			else if (specimen.getIdentifications().iterator().next().getTaxonRank()
					.contains("var.")) {
				dataRow.add("variety");
			}
			else if (specimen.getIdentifications().iterator().next().getTaxonRank()
					.contains("f.")) {
				dataRow.add("form");
			}
			else if (specimen.getIdentifications().iterator().next().getTaxonRank() != null
					&& !specimen.getIdentifications().iterator().next().getTaxonRank()
							.contains("f.")
					&& !specimen.getIdentifications().iterator().next().getTaxonRank()
							.contains("var.")
					&& !specimen.getIdentifications().iterator().next().getTaxonRank()
							.contains("subsp.")) {
				dataRow.add(specimen.getIdentifications().iterator().next().getTaxonRank());
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get TaxonRemarks value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setTaxonRemarks(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getIdentifications() != null) {
			List<String> listFullname = new ArrayList<>();
			Iterator<SpecimenIdentification> identIterator = specimen.getIdentifications()
					.iterator();
			while (identIterator.hasNext()) {
				listFullname.add(identIterator.next().getScientificName().getFullScientificName());
				if (specimen.getIdentifications().size() > 1) {
					listFullname.add(" | ");
				}
			}

			if (listFullname.size() > 1) {
				String resultFullName = listFullname.toString().replace(",", "").replace("[", "")
						.replace("]", "").trim();
				dataRow.add(resultFullName);
				listFullname.clear();
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get TypeStatus value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setTypeStatus(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{

		identification = specimen.getIdentifications().size();

		if (specimen.getTypeStatus() != null && identification > 0) {
			dataRow.add(specimen.getTypeStatus().toString());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	static String latitudeDecimal1 = null;
	static String longitudeDecimal1 = null;
	static String latitudeDecimal2 = null;
	static String longitudeDecimal2 = null;
	static int record1 = 1;
	static int record2 = 2;
	static int count = 0;

	/**
	 * Get VerbatimCoordinates value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setVerbatimCoordinates(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1) {
				Iterator<ESGatheringSiteCoordinates> iterator = specimen.getGatheringEvent()
						.getSiteCoordinates().iterator();

				while (iterator.hasNext()) {
					count++;
					if (count == record1) {
						latitudeDecimal1 = Double.toString(iterator.next().getLatitudeDecimal());
						longitudeDecimal1 = Double.toString(iterator.next().getLongitudeDecimal());
					}

					if (count == record2) {
						latitudeDecimal2 = Double.toString(iterator.next().getLatitudeDecimal());
						longitudeDecimal2 = Double.toString(iterator.next().getLongitudeDecimal());
					}
				}
				if (latitudeDecimal1 != null && longitudeDecimal1 != null
						&& latitudeDecimal2 != null && longitudeDecimal2 != null) {
					dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2 + " | "
							+ longitudeDecimal1 + ", " + longitudeDecimal2);
				}
			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get VerbatimCoordinates value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setVerbatimCoordinates_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1) {
				Iterator<ESGatheringSiteCoordinates> iterator = specimen.getGatheringEvent()
						.getSiteCoordinates().iterator();

				while (iterator.hasNext()) {
					count++;
					if (count == record1) {
						latitudeDecimal1 = Double.toString(iterator.next().getLatitudeDecimal());
						longitudeDecimal1 = Double.toString(iterator.next().getLongitudeDecimal());
					}

					if (count == record2) {
						latitudeDecimal2 = Double.toString(iterator.next().getLatitudeDecimal());
						longitudeDecimal2 = Double.toString(iterator.next().getLongitudeDecimal());
					}
					if (latitudeDecimal1 != null && longitudeDecimal1 != null
							&& latitudeDecimal2 != null && longitudeDecimal2 != null) {
						dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2 + " | "
								+ longitudeDecimal1 + ", " + longitudeDecimal2);
					}
				}

			}
			else {
				dataRow.add(EMPTY_STRING);
			}
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get VerbatimDepth value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setVerbatimDepth(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		if (specimen.getGatheringEvent().getDepth() != null) {
			dataRow.add(specimen.getGatheringEvent().getDepth());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get AltitudeUnifOfMeasurement value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setAltitudeUnifOfMeasurement(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		if (specimen.getGatheringEvent().getAltitudeUnifOfMeasurement() != null) {
			dataRow.add(specimen.getGatheringEvent().getAltitudeUnifOfMeasurement());
		}
		else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/**
	 * Get VerbatimEventDate value for Zoology and Geology
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setVerbatimEventDate(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow)
			throws Exception
	{
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}

		else if (specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		}
		/*
		 * if BeginDate is equal to EndDate then only the value of BeginDate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if only begindate has a value then get the value of begindate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			String dateEnd = EMPTY_STRING;
			dataRow.add(dateEnd);
		}
	}

	/**
	 * Get VerbatimEventDate value for Brahms
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setVerbatimEventDate_Brahms(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent().getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/*
		 * if BeginDate is equal to EndDate then only the value of BeginDate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		}

		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if only begindate has a value then get the value of begindate
		 */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin
					.format(specimen.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			String dateEnd = EMPTY_STRING;
			dataRow.add(dateEnd);
		}
	}

	/**
	 * Get TaxonRank Is VerbatimTaxonRank value
	 * 
	 * @param specimen
	 *            ESspecimen class
	 * @param dataRow
	 *            CsvRow dataRow
	 * @throws Exception
	 *             problem occurred in specimen class
	 */
	public static void setTaxonRank_Is_VerbatimTaxonRank(ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception
	{

		List<String> list = new ArrayList<>();
		int size = specimen.getIdentifications().size();

		for (int i = 0; i < size; i++) {
			if (specimen.getIdentifications().get(i).isPreferred()) {
				list.add(toString(specimen.getIdentifications().get(i).isPreferred()));
				taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				if (specimen.getIdentifications().get(i).getTaxonRank() != null
						&& list.contains("true")) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
				else if (specimen.getIdentifications().get(i).getTaxonRank() == null
						&& list.contains("true")) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
			}
			else {
				if (specimen.getIdentifications().iterator().next().getTaxonRank() != null
						&& list.isEmpty()) {
					taxonRank = specimen.getIdentifications().get(i).getTaxonRank();
				}
			}
		}

		if (taxonRank != null) {
			dataRow.add(taxonRank);
		}
		else {
			dataRow.add(EMPTY_STRING);
		}

		list.clear();

		/*
		 * if (specimen.getIdentifications().iterator().next().getTaxonRank() !=
		 * null) { dataRow.add(specimen.getIdentifications().iterator().next()
		 * .getTaxonRank()); } else { dataRow.add(EMPTY_STRING); }
		 */
	}

}
