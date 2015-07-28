package nl.naturalis.nda.export.dwca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;



public class Fieldmapping {

	static final Logger logger = LoggerFactory.getLogger(DwCAExporter.class);
	private static final String EMPTY_STRING = "";

	static SimpleDateFormat datetimebegin = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat datetimenend = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/* Get ID value */
	public static void setDummyValue(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/* 00_Dummy0 is ID */
		if (specimen.getSourceSystemId() != null) {
			dataRow.add(specimen.getSourceSystemId());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get RecordBasis value for Zoology and Geology */
	public static void setBasisOfRecord(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getRecordBasis() != null) {
			dataRow.add(specimen.getRecordBasis());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get BasisOf record value For BRAHMS */
	public static void setBasisOfRecord_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getRecordBasis() != null
				&& specimen.getRecordBasis().contains(
						"photo(copy) of herbarium sheet")
				&& specimen.getRecordBasis().contains("Illustration")
				&& specimen.getRecordBasis().contains("Photographs, negatives")
				&& specimen.getRecordBasis().contains("DNA sample from sheet")
				&& specimen.getRecordBasis().contains("Slides")
				&& specimen.getRecordBasis().contains("Observation")) {
			System.out.println("Not included in Brahms CSV: "
					+ specimen.getRecordBasis());
			logger.info("Not included in Brahms CSV: "
					+ specimen.getRecordBasis());
			return;
		} else if (specimen.getRecordBasis() != null) {
			dataRow.add("PreservedSpecimen");
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get catalogNumber value */
	public static void setCatalogNumber(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getSourceSystemId() != null) {
			dataRow.add(specimen.getSourceSystemId());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get ClassName value for Zoology and Geology */
	public static void setClassName(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getClassName());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/* Get Classname for BRAHMS */
	public static void setClassName_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/* 03_DummyDefault class */
		dataRow.add(EMPTY_STRING);
	}

	/* Get CollectionType value for Zoology and Geology */
	public static void setCollectionType(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getCollectionType() != null) {
			dataRow.add(specimen.getCollectionType());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/* Get CollectionCode value for Brahms */
	public static void setCollectionCode_Brahms(
			 ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add("Botany");
	}

	/* Get Continent value */
	public static void setContinent(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getWorldRegion() != null) {
			dataRow.add(specimen.getGatheringEvent().getWorldRegion());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Country value */
	public static void setCountry(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getCountry() != null) {
			dataRow.add(specimen.getGatheringEvent().getCountry());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get County value */
	public static void setCounty( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getCity() != null) {
			dataRow.add(specimen.getGatheringEvent().getCity());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get DateIndentified value for Zoology and Geology */
	public static void setDateIndentified(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getDateIdentified() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
			String dateiden = dateidentified
					.format(specimen.getIdentifications().iterator().next()
							.getDateIdentified());
			dataRow.add(dateiden);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get DateIndentified value for Brahms */
	public static void setDateIndentified_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getDateIdentified() != null) {
			SimpleDateFormat dateidentified = new SimpleDateFormat("yyyy-MM-dd");
			String dateiden = dateidentified
					.format(specimen.getIdentifications().iterator().next()
							.getDateIdentified());
			dataRow.add(dateiden);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get LatitudeDecimal value */
	public static void setLatitudeDecimal(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().iterator()
					.next().getLatitudeDecimal() != null) {
				dataRow.add(String.format("%s", specimen.getGatheringEvent()
						.getSiteCoordinates().iterator().next()
						.getLatitudeDecimal()));
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get LongituDecimal value */
	public static void setLongitudeDecimal(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().iterator()
					.next().getLongitudeDecimal() != null) {
				dataRow.add(String.format("%s", specimen.getGatheringEvent()
						.getSiteCoordinates().iterator().next()
						.getLongitudeDecimal()));
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Eventdate value for Zoology and Geology */
	public static void setEvendate(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent()
					.getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/* if BeginDate is equal to EndDate then only the value of BeginDate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == specimen
						.getGatheringEvent().getDateTimeEnd()) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/* if only begindate has a value then get the value of begindate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Eventdate value for Brahms */
	public static void setEvendate_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent()
					.getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/* if BeginDate is equal to EndDate then only the value of BeginDate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == specimen
						.getGatheringEvent().getDateTimeEnd()) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/* if only begindate has a value then get the value of begindate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Family value for Zoology and Geology */
	public static void setFamily( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification().getFamily() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Family value for Brahms */
	public static void setFamily_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification().getFamily() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Genus value for Zoology and Geology */
	public static void setGenus( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getGenusOrMonomial() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getGenusOrMonomial());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Genus value for Brahms */
	public static void setGenus_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*
		 * if (ExportDwCAUtilities.isEnabled(MAPPING_FILE_NAME,
		 * "13_GenusOrMonomial")) {
		 */
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getGenusOrMonomial() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getGenusOrMonomial());
		} else {
			dataRow.add(EMPTY_STRING);
		}
		// }
	}

	@SuppressWarnings("unused")
	/* Get Geodeticdatum value */
	public static void setGeodeticDatum(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/* Get Habitat value */
	public static void setHabitat(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/* Get HigherClassification value for Zoology and Geology */
	public static void setHigherClassification(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	/* Get HigherClassification value for Brahms */
	public static void setHigherClassification_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification() != null) {
			String kingdom = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getKingdom();
			String classname = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getClassName();
			String order = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getOrder();
			String family = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily();
			if (kingdom != null && classname != null && order != null
					&& family != null) {
				dataRow.add(kingdom + "|" + classname + "|" + order + "|"
						+ family);
			} else if (kingdom == null) {
				dataRow.add(classname + "|" + order + "|" + family);
			} else if (classname == null) {
				dataRow.add(kingdom + "|" + order + "|" + family);
			} else if (order == null) {
				dataRow.add(kingdom + "|" + classname + "|" + family);
			} else if (family == null) {
				dataRow.add(kingdom + "|" + classname + "|" + order);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get IndentifiersFullname value for Zoology and Geology */
	public static void setIdentifiersFullName(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		List<String> listAgentFullname = new ArrayList<String>();
		if (specimen.getIdentifications().iterator().next().getIdentifiers() != null) {
			Iterator<Agent> identifiedByIterator = specimen
					.getIdentifications().iterator().next().getIdentifiers()
					.iterator();
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
				String resultAgentFullName = listAgentFullname.toString()
						.replace(",", " ").replace("[", " ").replace("]", " ")
						.trim();
				dataRow.add(resultAgentFullName);
				/* NDA-303/372 */
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get IndentifiersFullname value for Brahms */
	public static void setIdentifiersFullName_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getIdentifiers() != null) {
			List<String> listAgentFullname = new ArrayList<String>();
			Iterator<Agent> identifiedByIterator = specimen
					.getIdentifications().iterator().next().getIdentifiers()
					.iterator();
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
				String resultAgentFullName = listAgentFullname.toString()
						.replace(",", " ").replace("[", " ").replace("]", " ")
						.trim();
				dataRow.add(resultAgentFullName);
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get NumberOfSpecimen value for Zoology and Geology */
	public static void setNumberOfSpecimen(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (Integer.toString(specimen.getNumberOfSpecimen()) != null
				&& specimen.getNumberOfSpecimen() > 0) {
			dataRow.add(Integer.toString(specimen.getNumberOfSpecimen()));
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/* Get InformationWithHeld value for Zoology and Geology */
	public static void setInformationWithHeld(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	/* Get InfraspecificEpithet value for Zoology and Geology */
	public static void setInfraspecificEpithet(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getInfraspecificEpithet() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getInfraspecificEpithet());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get InfraspecificEpithet value for Brahms */
	public static void setInfraspecificEpithet_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getInfraspecificEpithet() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getInfraspecificEpithet());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Island value for Zoology and Geology */
	public static void setIsland( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getIsland() != null) {
			dataRow.add(specimen.getGatheringEvent().getIsland());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get InstitudeCode value for Zoology and Geology */
	public static void setInstitudeCode(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getSourceInstitutionID().contains("Naturalis")) {
			dataRow.add(specimen.getSourceInstitutionID().substring(0, 9));
		} else if (specimen.getSourceInstitutionID() != null) {
			dataRow.add(specimen.getSourceInstitutionID());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Kingdom value for Zoology and Geology */
	public static void setKingdom(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getKingdom());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Kingdom value for Brahms */
	public static void setKingdom_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification() != null) {
			String family = specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getFamily();
			if (family != null) {
				if (family.contains("Fungi")) {
					dataRow.add("fungi");
				} else {
					dataRow.add("Plantae");
					// dataRow.add(specimen.getIdentifications().iterator().next().getDefaultClassification().getKingdom());
				}
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get PhaseOrStage value for Zoology and Geology */
	public static void setPhaseOrStage(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getPhaseOrStage() != null) {
			dataRow.add(specimen.getPhaseOrStage());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Locality value for Zoology and Geology */
	public static void setLocality(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getLocality() != null) {
			String localityResult = specimen.getGatheringEvent().getLocality()
					.replace('"', ' ').replace(' ', ' ').replace('\t', ' ')
					.replace("\r", "").replace("\n", "").trim();
			dataRow.add(localityResult);
			/* NDA-303/372 */
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	@SuppressWarnings("unused")
	/* Get MaximumElevationInMeters value */
	public static void setMaximumElevationInMeters(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}
	
	@SuppressWarnings("unused")
	/* Get MinimumElevationInMeters value */
	public static void setMinimumElevationInMeters(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/* Get NomenclaturalCode value for Zoology */
	public static void setNomenclaturalCode_Zoology(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add("ICZN");
	}

	@SuppressWarnings("unused")
	/* Get NomenclaturalCode value Geology */
	public static void setNomenclaturalCode_Geology(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add(EMPTY_STRING);
	}

	@SuppressWarnings("unused")
	/* Get NomenclaturalCode value for Brahms */
	public static void setNomenclaturalCode_Brahms(ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		dataRow.add("ICN");
	}

	/* Get OccurrenceID value for Brahms, Zoology and Geology */
	public static void setOccurrenceID(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*String institutionCode = null;
		String objectType = "specimen";
		if (specimen.getSourceInstitutionID().contains("Naturalis")) {
			institutionCode = specimen.getSourceInstitutionID().substring(0, 9);
		} else if (!specimen.getSourceInstitutionID().contains("Naturalis")) {
			institutionCode = specimen.getSourceInstitutionID();
		}
		
		 * PersistentID is: Example: occurrence id =
		 * http://data.biodiversitydata.nl/naturalis/specimen/RMNH.MAM.40012
		 
		dataRow.add(CsvFileWriter.httpUrl + institutionCode + "/" + objectType
				+ "/" + specimen.getSourceSystemId());
		*/
		
		/* NDA 407 only unitGuid*/
		if (specimen.getUnitGUID() != null)
		{
			dataRow.add(specimen.getUnitGUID());
		}
		else
		{
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Order value for Zoology and Geology */
	public static void setOrder( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification().getOrder() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getOrder());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Order value for Brahms */
	public static void setOrder_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification().getOrder() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getOrder());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Phylum value for Zoology and Geology */
	public static void setPhylum( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next()
				.getDefaultClassification().getPhylum() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getDefaultClassification().getPhylum());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get PreparationType value for Zoology and Geology */
	public static void setPreparationType(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getPreparationType() != null) {
			dataRow.add(specimen.getPreparationType());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get GatheringAgents_FullName value */
	public static void setGatheringAgents_FullName(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		List<String> listFullname = new ArrayList<String>();
		if (specimen.getGatheringEvent().getGatheringPersons() != null) {
			Iterator<Person> fullnameIterator = specimen.getGatheringEvent()
					.getGatheringPersons().iterator();
			while (fullnameIterator.hasNext()) {
				listFullname.add(fullnameIterator.next().getFullName());

				if (specimen.getGatheringEvent().getGatheringPersons().size() > 1) {
					listFullname.add(" | ");
				}
			}

			if (listFullname.size() > 0) {
				String resultFullName = listFullname.toString()
						.replace(",", " ").replace("[", " ").replace("]", " ")
						.trim();
				dataRow.add(resultFullName);
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get FullScientificName value for Zoology and Geology */
	public static void setFullScientificName(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getFullScientificName() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getFullScientificName());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get FullScientificName value for Brahms */
	public static void setFullScientificName_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getFullScientificName() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getFullScientificName());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get AuthorshipVerbatim value for Zoology and Geology */
	public static void setAuthorshipVerbatim(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getAuthorshipVerbatim() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getAuthorshipVerbatim());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get AuthorshipVerbatim value for Brahms */
	public static void setAuthorshipVerbatim_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getAuthorshipVerbatim() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getAuthorshipVerbatim());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Sex value for Zoology and Geology */
	public static void setSex( ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getSex() != null) {
			dataRow.add(specimen.getSex());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get SpecificEpithet value for Zoology and Geology */
	public static void setSpecificEpithet(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSpecificEpithet() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getSpecificEpithet());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get SpecificEpithet value for Brahms */
	public static void setSpecificEpithet_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSpecificEpithet() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getSpecificEpithet());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get ProvinceState value for Zoology and Geology */
	public static void setProvinceState(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getProvinceState() != null) {
			/* NDA-303/372 */
			dataRow.add(specimen.getGatheringEvent().getProvinceState());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get SubGenus value for Zoology and Geology */
	public static void setSubGenus(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSubgenus() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getSubgenus());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get SubGenus value for Brahms */
	public static void setSubGenus_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getScientificName()
				.getSubgenus() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getScientificName().getSubgenus());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Taxonrank value for Zoology and Geology */
	public static void setTaxonrank(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getTaxonRank() != null
				&& specimen.getIdentifications().iterator().next()
						.isPreferred() == true) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getTaxonRank());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get Taxonrank value for Brahms */
	public static void setTaxonrank_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getTaxonRank() != null) {
			if (specimen.getIdentifications().iterator().next().getTaxonRank()
					.contains("subsp.")) {
				dataRow.add("subspecies");
			} else if (specimen.getIdentifications().iterator().next()
					.getTaxonRank().contains("var.")) {
				dataRow.add("variety");
			} else if (specimen.getIdentifications().iterator().next()
					.getTaxonRank().contains("f.")) {
				dataRow.add("form");
			} else if (specimen.getIdentifications().iterator().next()
					.getTaxonRank() != null
					&& !specimen.getIdentifications().iterator().next()
							.getTaxonRank().contains("f.")
					&& !specimen.getIdentifications().iterator().next()
							.getTaxonRank().contains("var.")
					&& !specimen.getIdentifications().iterator().next()
							.getTaxonRank().contains("subsp.")) {
				dataRow.add(specimen.getIdentifications().iterator().next()
						.getTaxonRank());
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get TaxonRemarks value for Zoology and Geology */
	public static void setTaxonRemarks(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications() != null) {
			List<String> listFullname = new ArrayList<String>();
			Iterator<SpecimenIdentification> identIterator = specimen
					.getIdentifications().iterator();
			while (identIterator.hasNext()) {
				listFullname.add(identIterator.next().getScientificName()
						.getFullScientificName());
				if (specimen.getIdentifications().size() > 1) {
					listFullname.add(" | ");
				}
			}

			if (listFullname.size() > 1) {
				String resultFullName = listFullname.toString()
						.replace(",", "").replace("[", "").replace("]", "")
						.trim();
				dataRow.add(resultFullName);
				listFullname.clear();
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get TypeStatus value */
	public static void setTypeStatus(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getTypeStatus() != null) {
			dataRow.add(specimen.getTypeStatus());
		} else {
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

	/* Get VerbatimCoordinates value for Zoology and Geology */
	public static void setVerbatimCoordinates(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1) {
				Iterator<ESGatheringSiteCoordinates> iterator = specimen
						.getGatheringEvent().getSiteCoordinates().iterator();

				while (iterator.hasNext()) {
					count++;
					if (count == record1) {
						latitudeDecimal1 = Double.toString(iterator.next()
								.getLatitudeDecimal());
						longitudeDecimal1 = Double.toString(iterator.next()
								.getLongitudeDecimal());
					}

					if (count == record2) {
						latitudeDecimal2 = Double.toString(iterator.next()
								.getLatitudeDecimal());
						longitudeDecimal2 = Double.toString(iterator.next()
								.getLongitudeDecimal());
					}
				}
				if (latitudeDecimal1 != null && longitudeDecimal1 != null
						&& latitudeDecimal2 != null
						&& longitudeDecimal2 != null) {
					dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2
							+ " | " + longitudeDecimal1 + ", "
							+ longitudeDecimal2);
				}
			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get VerbatimCoordinates value for Brahms */
	public static void setVerbatimCoordinates_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getSiteCoordinates() != null) {
			if (specimen.getGatheringEvent().getSiteCoordinates().size() > 1) {
				Iterator<ESGatheringSiteCoordinates> iterator = specimen
						.getGatheringEvent().getSiteCoordinates().iterator();

				while (iterator.hasNext()) {
					count++;
					if (count == record1) {
						latitudeDecimal1 = Double.toString(iterator.next()
								.getLatitudeDecimal());
						longitudeDecimal1 = Double.toString(iterator.next()
								.getLongitudeDecimal());
					}

					if (count == record2) {
						latitudeDecimal2 = Double.toString(iterator.next()
								.getLatitudeDecimal());
						longitudeDecimal2 = Double.toString(iterator.next()
								.getLongitudeDecimal());
					}
					if (latitudeDecimal1 != null && longitudeDecimal1 != null
							&& latitudeDecimal2 != null
							&& longitudeDecimal2 != null) {
						dataRow.add(latitudeDecimal1 + ", " + latitudeDecimal2
								+ " | " + longitudeDecimal1 + ", "
								+ longitudeDecimal2);
					}
				}

			} else {
				dataRow.add(EMPTY_STRING);
			}
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get VerbatimDepth value */
	public static void setVerbatimDepth(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getDepth() != null) {
			dataRow.add(specimen.getGatheringEvent().getDepth());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get AltitudeUnifOfMeasurement value */
	public static void setAltitudeUnifOfMeasurement(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getGatheringEvent().getAltitudeUnifOfMeasurement() != null) {
			dataRow.add(specimen.getGatheringEvent()
					.getAltitudeUnifOfMeasurement());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get VerbatimEventDate value for Zoology and Geology */
	public static void setVerbatimEventDate(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent()
					.getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/* if BeginDate is equal to EndDate then only the value of BeginDate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == specimen
						.getGatheringEvent().getDateTimeEnd()) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/* if only begindate has a value then get the value of begindate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get VerbatimEventDate value for Brahms */
	public static void setVerbatimEventDate_Brahms(
			ESSpecimen specimen, CsvFileWriter.CsvRow dataRow) throws Exception {
		/*
		 * if BeginDate and EndDate both has values and not equal then get the
		 * value of the BeginDate and EndDate
		 */
		if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() != null
				&& !specimen.getGatheringEvent().getDateTimeBegin()
						.equals(specimen.getGatheringEvent().getDateTimeEnd())) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			String dateEnd = datetimenend.format(specimen.getGatheringEvent()
					.getDateTimeEnd());
			dataRow.add(dateBegin + " | " + dateEnd);
		}
		/* if BeginDate is equal to EndDate then only the value of BeginDate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == specimen
						.getGatheringEvent().getDateTimeEnd()) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/* if only begindate has a value then get the value of begindate */
		else if (specimen.getGatheringEvent().getDateTimeBegin() != null
				&& specimen.getGatheringEvent().getDateTimeEnd() == null) {
			String dateBegin = datetimebegin.format(specimen
					.getGatheringEvent().getDateTimeBegin());
			dataRow.add(dateBegin);
		}
		/*
		 * if EndDate has a value and Begindate has no value set the value of
		 * null for Enddate
		 */
		else if (specimen.getGatheringEvent().getDateTimeEnd() != null
				&& specimen.getGatheringEvent().getDateTimeBegin() == null) {
			dataRow.add(EMPTY_STRING);
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

	/* Get TaxonRank Is VerbatimTaxonRank value */
	public static void setTaxonRank_Is_VerbatimTaxonRank(
			 ESSpecimen specimen,
			CsvFileWriter.CsvRow dataRow) throws Exception {
		if (specimen.getIdentifications().iterator().next().getTaxonRank() != null) {
			dataRow.add(specimen.getIdentifications().iterator().next()
					.getTaxonRank());
		} else {
			dataRow.add(EMPTY_STRING);
		}
	}

}
