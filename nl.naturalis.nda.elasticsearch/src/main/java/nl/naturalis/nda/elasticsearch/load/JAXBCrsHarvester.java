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
import org.domainobject.util.debug.BeanPrinter;
import org.domainobject.util.http.SimpleHttpGet;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.ResumptionTokenType;
import org.openarchives.oai._2_0.oai_dc.OaiDcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class harvesting CRS using its OAIPMH interface. The OAIPMH parsing is left
 * to JAXB. Development is halted due to strange behaviour of JAXB with respect
 * to the &lt;metadata&gt; element in the XML.
 * 
 * @author ayco
 * 
 */
public class JAXBCrsHarvester {

	private static final Logger logger = LoggerFactory.getLogger(JAXBCrsHarvester.class);

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
		JAXBCrsHarvester harvester = new JAXBCrsHarvester();
		harvester.harvest();
	}

	private final ConfigObject config;
	private final Unmarshaller unmarshaller;
	private final BeanPrinter beanPrinter;

	private int batch;


	public JAXBCrsHarvester() throws JAXBException
	{
		URL url = CRSBioportalInterface.class.getResource(CONFIG_FILE);
		config = new ConfigObject(url);
		JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_PACKAGES);
		unmarshaller = jaxbContext.createUnmarshaller();
		beanPrinter = new BeanPrinter("C:/tmp/BeanPrinter.txt");
	}


	public void harvest()
	{

		try {

			String resToken;

			File resTokenFile = getResumptionTokenFile();
			if (!resTokenFile.exists()) {
				logger.info(String.format("Did not find resumption token file: %s", resTokenFile.getCanonicalPath()));
				logger.info("Will start from scratch (batch 0)");
				batch = 0;
				resToken = null;
			}
			else {
				String[] elements = FileUtil.getContents(resTokenFile).split(RES_TOKEN_DELIM);
				batch = Integer.parseInt(elements[1]);
				resToken = elements[0];
				logger.info(String.format("Found resumption token file: %s", resTokenFile.getCanonicalPath()));
				logger.info(String.format("Will resume with resumption token %s (batch %s)", resToken, batch));
			}

			do {
				logger.info("Processing batch " + batch);
				resToken = processBatch(resToken);
				++batch;
				break;
			} while (resToken != null);

			logger.info("Deleting resumption token file");
			if (resTokenFile.exists()) {
				resTokenFile.delete();
			}

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
		beanPrinter.dump(records);

		for (RecordType record : records) {
			MetadataType metadata = record.getMetadata();
			if (metadata == null) {
				// This is a record that was deleted in CRS
			}
			else {
				//OaiDcType oaiDc = metadata.getOaiDc();
			}
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
			File file = getResumptionTokenFile();
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


	private static File getResumptionTokenFile()
	{
		String path = JAXBCrsHarvester.class.getResource("/").toString() + RES_TOKEN_FILE;
		URI uri = URI.create(path);
		return new File(uri);
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
