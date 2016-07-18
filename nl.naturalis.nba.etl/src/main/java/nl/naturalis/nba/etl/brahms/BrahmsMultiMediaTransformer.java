package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.es.DocumentType.*;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE;
import static nl.naturalis.nba.etl.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.*;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDate;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getScientificName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.domainobject.util.ConfigObject;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.ServiceAccessPoint.Variant;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.es.types.ESMultiMediaObject;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;

/**
 * The transformer component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaTransformer
		extends AbstractCSVTransformer<BrahmsCsvField, ESMultiMediaObject> {

	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private static final ThemeCache themeCache;

	static {
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
	}

	public BrahmsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	@Override
	protected String getObjectID()
	{
		return input.get(BARCODE);
	}

	@Override
	protected List<ESMultiMediaObject> doTransform()
	{
		// No record-level validations for Brahms multimedia, so:
		stats.recordsAccepted++;
		ArrayList<ESMultiMediaObject> result = new ArrayList<>(3);
		String images = input.get(IMAGELIST);
		if (images != null) {
			String[] urls = images.split(",");
			for (int i = 0; i < urls.length; ++i) {
				ESMultiMediaObject mmo = transformOne(urls[i]);
				if (mmo != null) {
					result.add(mmo);
				}
			}
		}
		return result;
	}

	private ESMultiMediaObject transformOne(String url)
	{
		stats.objectsProcessed++;
		try {
			URI uri = getUri(url);
			ESMultiMediaObject mmo = newMultiMediaObject();
			String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
			mmo.setUnitID(objectID + '_' + uriHash);
			mmo.setSourceSystemId(mmo.getUnitID());
			mmo.setAssociatedSpecimenReference(objectID);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, BRAHMS);
			mmo.setTheme(themes);
			mmo.setDescription(input.get(PLANTDESC));
			mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(input)));
			mmo.setIdentifications(Arrays.asList(getIdentification()));
			mmo.setSpecimenTypeStatus(typeStatusNormalizer.normalize(input.get(TYPE)));
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

	private static ESMultiMediaObject newMultiMediaObject()
	{
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setSourceSystem(BRAHMS);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("Brahms");
		mmo.setLicenceType(LICENCE_TYPE);
		mmo.setLicence(LICENCE);
		mmo.setCollectionType("Botany");
		return mmo;
	}

	private static URI getUri(String url) throws URISyntaxException
	{
		url = url.trim().replaceAll(" ", "%20");
		return new URI(url);
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
		// identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	private static ServiceAccessPoint newServiceAccessPoint(URI uri)
	{
		return new ServiceAccessPoint(uri, "image/jpeg", Variant.MEDIUM_QUALITY);
	}

}
