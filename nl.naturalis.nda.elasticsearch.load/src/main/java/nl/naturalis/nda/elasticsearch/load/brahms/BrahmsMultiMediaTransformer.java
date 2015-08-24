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
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;
import nl.naturalis.nda.elasticsearch.load.normalize.SpecimenTypeStatusNormalizer;

import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.ConfigObject;
import org.slf4j.Logger;

/**
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaTransformer extends AbstractCSVTransformer<ESMultiMediaObject> {

	@SuppressWarnings("unused")
	private static final Logger logger;
	private static final SpecimenTypeStatusNormalizer typeStatusNormalizer;
	private static final ThemeCache themeCache;

	static {
		logger = Registry.getInstance().getLogger(BrahmsMultiMediaTransformer.class);
		typeStatusNormalizer = SpecimenTypeStatusNormalizer.getInstance();
		themeCache = ThemeCache.getInstance();
	}

	public BrahmsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	@Override
	public List<ESMultiMediaObject> transform(CSVRecordInfo info)
	{
		stats.recordsProcessed++;
		recInf = info;
		objectID = val(info.getRecord(), BARCODE);
		if (objectID == null) {
			stats.recordsRejected++;
			objectID = "?";
			if (!suppressErrors)
				error("Missing barcode");
			return null;
		}

		stats.recordsAccepted++;
		ArrayList<ESMultiMediaObject> result = new ArrayList<>(3);
		String s = val(info.getRecord(), IMAGELIST);
		if (s != null) {
			String[] urls = s.split(",");
			for (int i = 0; i < urls.length; ++i) {
				URI uri = getUri(urls[i]);
				if (uri == null) {
					continue;
				}
				ESMultiMediaObject mmo = transferOne(info, uri);
				if (mmo != null) {
					result.add(mmo);
				}
			}
		}
		return result;
	}

	private URI getUri(String url)
	{
		url = url.trim();
		if (url.charAt(1) == ':') {
			// This is a local file system path like Q:\foo.jpg.
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: " + url);
			return null;
		}
		url = url.replaceAll(" ", "%20");
		try {
			return new URI(url);
		}
		catch (URISyntaxException e) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: " + url);
			return null;
		}
	}

	private ESMultiMediaObject transferOne(CSVRecordInfo info, URI uri)
	{
		stats.objectsProcessed++;
		try {
			CSVRecord record = info.getRecord();
			ESMultiMediaObject mmo = new ESMultiMediaObject();
			String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
			mmo.setUnitID(objectID + '_' + uriHash);
			mmo.setSourceSystemId(mmo.getUnitID());
			mmo.setSourceSystem(BRAHMS);
			mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
			mmo.setOwner(SOURCE_INSTITUTION_ID);
			mmo.setSourceID("Brahms");
			mmo.setLicenceType(LICENCE_TYPE);
			mmo.setLicence(LICENCE);
			mmo.setCollectionType("Botany");
			mmo.setAssociatedSpecimenReference(objectID);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, BRAHMS);
			mmo.setTheme(themes);
			mmo.setDescription(val(record, PLANTDESC));
			mmo.setGatheringEvents(Arrays.asList(getGatheringEvent(record)));
			mmo.setIdentifications(Arrays.asList(getIdentification(record)));
			mmo.setSpecimenTypeStatus(typeStatusNormalizer.getNormalizedValue(val(record, TYPE)));
			mmo.addServiceAccessPoint(newServiceAccessPoint(uri));
			return mmo;
		}
		catch (Throwable t) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				error(t.getMessage());
			}
			return null;
		}
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
