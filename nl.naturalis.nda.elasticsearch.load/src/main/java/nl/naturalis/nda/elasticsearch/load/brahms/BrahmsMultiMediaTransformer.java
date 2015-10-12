package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.domain.SourceSystem.BRAHMS;
import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.val;
import static nl.naturalis.nda.elasticsearch.load.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsCsvField.*;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDate;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getGatheringEvent;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getSystemClassification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.AbstractCSVTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.ConfigObject;

/**
 * The transformer component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaTransformer extends AbstractCSVTransformer<ESMultiMediaObject> {

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
		return val(input.getRecord(), BARCODE);
	}

	@Override
	protected List<ESMultiMediaObject> doTransform()
	{
		// No record-level validations for Brahms multimedia, so:
		stats.recordsAccepted++;
		ArrayList<ESMultiMediaObject> result = new ArrayList<>(3);
		String images = val(input.getRecord(), IMAGELIST);
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
			CSVRecord rec = input.getRecord();
			ESMultiMediaObject mmo = newMultiMediaObject();
			String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
			mmo.setUnitID(objectID + '_' + uriHash);
			mmo.setSourceSystemId(mmo.getUnitID());
			mmo.setAssociatedSpecimenReference(objectID);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, BRAHMS);
			mmo.setTheme(themes);
			mmo.setDescription(val(rec, PLANTDESC));
			mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(rec)));
			mmo.setIdentifications(Arrays.asList(getIdentification(rec)));
			mmo.setSpecimenTypeStatus(typeStatusNormalizer.normalize(val(rec, TYPE)));
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

	private static MultiMediaContentIdentification getIdentification(CSVRecord record)
	{
		MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		String s = val(record, VERNACULAR);
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = val(record, YEARIDENT);
		String m = val(record, MONTHIDENT);
		String d = val(record, DAYIDENT);
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(record);
		DefaultClassification dc = getDefaultClassification(record, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	private static ServiceAccessPoint newServiceAccessPoint(URI uri)
	{
		return new ServiceAccessPoint(uri, "image/jpeg", Variant.MEDIUM_QUALITY);
	}

}
