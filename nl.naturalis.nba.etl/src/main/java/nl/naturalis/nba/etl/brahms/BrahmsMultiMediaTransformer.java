package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAYIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.IMAGELIST;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTHIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.VERNACULAR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEARIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getSystemClassification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.ServiceAccessPoint.Variant;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;

/**
 * The transformer component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaTransformer extends BrahmsTransformer<MultiMediaObject> {

	public BrahmsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	protected List<MultiMediaObject> doTransform()
	{
		// No record-level validations for Brahms multimedia, so:
		stats.recordsAccepted++;
		ArrayList<MultiMediaObject> result = new ArrayList<>(3);
		String images = input.get(IMAGELIST);
		if (images != null) {
			String[] urls = images.split(",");
			for (int i = 0; i < urls.length; ++i) {
				MultiMediaObject mmo = transformOne(urls[i]);
				if (mmo != null) {
					result.add(mmo);
				}
			}
		}
		return result;
	}

	private MultiMediaObject transformOne(String url)
	{
		stats.objectsProcessed++;
		try {
			URI uri = getUri(url);
			MultiMediaObject mmo = newMultiMediaObject();
			String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
			mmo.setUnitID(objectID + '_' + uriHash);
			mmo.setSourceSystemId(mmo.getUnitID());
			String specimenID = getElasticsearchId(BRAHMS, objectID);
			mmo.setAssociatedSpecimenReference(specimenID);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, BRAHMS);
			mmo.setTheme(themes);
			mmo.setDescription(input.get(PLANTDESC));
			mmo.setGatheringEvents(Arrays.asList(getMultiMediaGatheringEvent(input)));
			mmo.setIdentifications(Arrays.asList(getIdentification()));
			mmo.addServiceAccessPoint(newServiceAccessPoint(uri));
			stats.objectsAccepted++;
			return mmo;
		}
		catch (URISyntaxException e) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: " + url);
			return null;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private static MultiMediaObject newMultiMediaObject()
	{
		MultiMediaObject mmo = new MultiMediaObject();
		mmo.setSourceSystem(BRAHMS);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("Brahms");
		mmo.setLicenseType(LICENCE_TYPE);
		mmo.setLicense(LICENCE);
		mmo.setCollectionType("Botany");
		return mmo;
	}

	private MultiMediaContentIdentification getIdentification()
	{
		MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		String s = input.get(VERNACULAR);
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = input.get(YEARIDENT);
		String m = input.get(MONTHIDENT);
		String d = input.get(DAYIDENT);
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(input);
		DefaultClassification dc = getDefaultClassification(input, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	private MultiMediaGatheringEvent getMultiMediaGatheringEvent(
			CSVRecordInfo<BrahmsCsvField> record)
	{
		MultiMediaGatheringEvent ge = new MultiMediaGatheringEvent();
		populateGatheringEvent(ge, record);
		return ge;
	}

	private static URI getUri(String url) throws URISyntaxException
	{
		url = url.trim().replaceAll(" ", "%20");
		return new URI(url);
	}

	private static ServiceAccessPoint newServiceAccessPoint(URI uri)
	{
		return new ServiceAccessPoint(uri, "image/jpeg", Variant.MEDIUM_QUALITY);
	}

}
