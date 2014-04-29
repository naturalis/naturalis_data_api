package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.naturalis.bioportal.oaipmh.CRSBioportalInterface;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.ResumptionTokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CRSHarvester {

	private static final Logger logger = LoggerFactory.getLogger(CRSHarvester.class);

	private static final String CONFIG_FILE = "/config/import-crs.properties";
	private static final String RES_TOKEN_FILE = "/.resumption-token";
	private static final String RES_TOKEN_DELIM = ",";
	private static final String JAXB_PACKAGES = "org.openarchives.oai._2:org.openarchives.oai._2_0.oai_dc:org.purl.dc.elements._1:org.tdwg.schemas.abcd._2";

	//@formatter:off
	private static final String[] ALL_FIELDS_ARRAY = new String[] {
		"DML",
		"identifier",
		"UnitID",
		"RecordBasis",
		"KindOfUnit",
		"SourceInstitutionID",
		"UnitGUID",
		"Sex",
		"PhaseOrStage",
		"AccessionSpecimenNumbers",
		"Altitude",
		"Depth",
		"PreferredFlag",
		"ScientificName",
		"GenusOrMonomial",
		"Subgenus",
		"SpeciesEpithet",
		"InfrasubspecificRank",
		"subspeciesepithet",
		"InfrasubspecificName",
		"AuthorTeamOriginalAndYear",
		"TypeStatus",
		"NameAddendum",
		"IdentificationQualifier1",
		"IdentificationQualifier2",
		"IdentificationQualifier3",
		"GatheringAgent",
		"WorldRegion",
		"Country",
		"ProvinceState",
		"Locality",
		"ObjectPublic",
		"AltitudeUnit",
		"DepthUnit",
		"CollectingStartDate",
		"CollectingEndDate",
		"Title",
		"taxonCoverage",
		"MultiMediaPublic",
		"LatitudeDecimal",
		"LongitudeDecimal",
		"geodeticDatum"	
	};
	
	private static final String[] DERMINATION_FIELDS_ARRAY = new String[] {
		"PreferredFlag",
		"ScientificName",
		"GenusOrMonomial",
		"SpeciesEpithet",
		"InfrasubspecificRank",
		"subspeciesepithet",
		"InfrasubspecificName",
		"AuthorTeamOriginalAndYear",
		"TypeStatus",
		"NameAddendum",
		"IdentificationQualifier1",
		"IdentificationQualifier2",
		"IdentificationQualifier3"		
	};
	//@formatter:on

	public static void main(String[] args) throws JAXBException
	{
		CRSHarvester harvester = new CRSHarvester();
		harvester.harvest();
	}

	private final ConfigObject config;
	private final Unmarshaller unmarshaller;

	private int batch;


	public CRSHarvester() throws JAXBException
	{
		URL url = CRSBioportalInterface.class.getResource(CONFIG_FILE);
		config = new ConfigObject(url);
		JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_PACKAGES);
		unmarshaller = jaxbContext.createUnmarshaller();
	}


	public void harvest()
	{

		try {

			String resToken;

			URL url = getClass().getResource(RES_TOKEN_FILE);
			if (url == null) {
				String dir = getClass().getResource("/").toString();
				logger.info(String.format("Did not find resumption token file (.resumption-token) in %s.will start from scratch", dir));
				batch = 0;
				resToken = null;
			}
			else {
				String[] elements = FileUtil.getContents(url).split(RES_TOKEN_DELIM);
				batch = Integer.parseInt(elements[0]);
				resToken = elements[1];
				logger.info(String.format("Found resumption token file (%s). Will start with resumption token %s.", url.toString(), resToken));
			}

			do {
				logger.info("Processing batch " + batch);
				resToken = processBatch(resToken);
				++batch;
				break;
			} while (resToken != null);

			logger.info(getClass().getSimpleName() + " finished successfully");

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}

	}


	private String processBatch(String resToken) throws JAXBException
	{
		logger.info("Calling CRS OAI service");
		URL url = getServiceUrl(resToken);
		JAXBElement<OAIPMHtype> e = (JAXBElement<OAIPMHtype>) unmarshaller.unmarshal(url);
		OAIPMHtype root = e.getValue();

		List<RecordType> records = root.getListRecords().getRecord();

		for (RecordType record : records) {
			//MetadataType metadata = record.getMetadata();
			//JAXBElement<OaiDcType> x = (JAXBElement<OaiDcType>) metadata.getAny();
		}

		return processResumptionToken(root);
	}


	private String processResumptionToken(OAIPMHtype root)
	{
		ResumptionTokenType rtt = root.getListRecords().getResumptionToken();
		String resToken = null;
		if (rtt != null) {
			resToken = rtt.getValue();
			logger.info("Next batches available using resumption token " + resToken);
			URI uri = URI.create(getClass().getResource("/").toString() + RES_TOKEN_FILE);
			File file = new File(uri);
			try {
				logger.info("Writing resumption token to file " + file.getCanonicalPath());
			}
			catch (IOException e) {
				throw ExceptionUtil.smash(e);
			}
			FileUtil.putContents(file, resToken + RES_TOKEN_DELIM + batch);
		}
		return resToken;
	}


	private URL getServiceUrl(String resToken)
	{
		if (config.getBoolean("isTest")) {
			String key = resToken == null ? "service.url.initial.test" : "service.url.resume.test";
			String val = config.getString(key);
			return getClass().getResource(val);
		}
		String key = resToken == null ? "service.url.initial" : "service.url.resume";
		String val = config.getString(key);
		try {
			return new URL(val);
		}
		catch (MalformedURLException e) {
			throw ExceptionUtil.smash(e);
		}
	}
}
