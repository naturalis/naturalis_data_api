package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.domain.ServiceAccessPoint.Variant.MEDIUM_QUALITY;
import static nl.naturalis.nda.domain.SourceSystem.NSR;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nda.elasticsearch.load.TransformUtil.equalizeNameComponents;
import static nl.naturalis.nda.elasticsearch.load.TransformUtil.guessMimeType;
import static nl.naturalis.nda.elasticsearch.load.TransformUtil.parseDate;
import static nl.naturalis.nda.elasticsearch.load.nsr.NsrImportUtil.val;
import static org.domainobject.util.DOMUtil.getDescendants;
import static org.domainobject.util.DOMUtil.getValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.AbstractXMLTransformer;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.XMLRecordInfo;

import org.w3c.dom.Element;

/**
 * Transforms and validates NSR source data.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrMultiMediaTransformer extends AbstractXMLTransformer<ESMultiMediaObject> {

	private ESTaxon taxon;

	public NsrMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	/**
	 * Set the taxon object associated with this multimedia object. The taxon
	 * object is extracted from the same XML record by the
	 * {@link NsrTaxonTransformer}.
	 * 
	 * @param taxon
	 */
	public void setTaxon(ESTaxon taxon)
	{
		this.taxon = taxon;
	}

	@Override
	protected String getObjectID()
	{
		return val(input.getRecord(), "nsr_id");
	}

	/**
	 * Transforms an XML record into one ore more {@code ESMultiMediaObject}s.
	 * The multimedia transformer does not keep track of record-level
	 * statistics. The assumption is that if the taxon transformer was able to
	 * extract a taxon from the XML record, then the record was OK at the record
	 * level.
	 */
	@Override
	protected List<ESMultiMediaObject> doTransform()
	{
		if (taxon == null) {
			stats.recordsSkipped++;
			if (logger.isDebugEnabled())
				debug("Ignoring images for skipped or invalid taxon");
			return null;
		}
		List<Element> imageElems = getDescendants(input.getRecord(), "image");
		if (imageElems == null) {
			if (logger.isDebugEnabled())
				debug("Skipping taxon without images");
			stats.recordsSkipped++;
			return null;
		}
		stats.recordsAccepted++;
		List<ESMultiMediaObject> mmos = new ArrayList<>(imageElems.size());
		for (Element imageElement : imageElems) {
			ESMultiMediaObject mmo = transformOne(imageElement);
			if (mmo != null)
				mmos.add(mmo);
		}
		return mmos.size() == 0 ? null : mmos;
	}

	private ESMultiMediaObject transformOne(Element e)
	{
		stats.objectsProcessed++;
		try {
			URI uri = getUri(e);
			if (uri == null)
				return null;
			ESMultiMediaObject mmo = newMediaObject();
			String uriHash = String.valueOf(uri.hashCode()).replace('-', '0');
			mmo.setSourceSystemId(objectID + '_' + uriHash);
			mmo.setUnitID(mmo.getSourceSystemId());
			String format = getValue(e, "mime_type");
			if (format == null || format.length() == 0) {
				if (!suppressErrors) {
					String fmt = "Missing mime type for image \"%s\" (taxon \"%s\").";
					warn(fmt, uri, taxon.getAcceptedName().getFullScientificName());
				}
				format = guessMimeType(uri.toString());
			}
			mmo.addServiceAccessPoint(new ServiceAccessPoint(uri, format, MEDIUM_QUALITY));
			mmo.setCreator(val(e, "photographer_name"));
			mmo.setCopyrightText(val(e, "copyright"));
			if (mmo.getCopyrightText() == null) {
				mmo.setLicenceType(LICENCE_TYPE);
				mmo.setLicence(LICENCE);
			}
			mmo.setDescription(val(e, "short_description"));
			mmo.setCaption(mmo.getDescription());
			String locality = val(e, "geography");
			String date = val(e, "date_taken");
			if (locality != null || date != null) {
				ESGatheringEvent ge = new ESGatheringEvent();
				mmo.setGatheringEvents(Arrays.asList(ge));
				ge.setLocalityText(locality);
				ge.setDateTimeBegin(parseDate(date));
				ge.setDateTimeEnd(ge.getDateTimeBegin());
			}
			stats.objectsAccepted++;
			return mmo;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private ESMultiMediaObject newMediaObject()
	{
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setSourceSystem(NSR);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("LNG NSR");
		mmo.setCollectionType("Nederlandse soorten en exoten");
		mmo.setAssociatedTaxonReference(taxon.getSourceSystemId());
		mmo.setIdentifications(Arrays.asList(getIdentification()));
		equalizeNameComponents(mmo);
		return mmo;
	}

	private MultiMediaContentIdentification getIdentification()
	{
		ESTaxon t = taxon;
		MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
		mmci.setTaxonRank(t.getTaxonRank());
		mmci.setScientificName(t.getAcceptedName());
		mmci.setDefaultClassification(t.getDefaultClassification());
		mmci.setSystemClassification(t.getSystemClassification());
		mmci.setVernacularNames(t.getVernacularNames());
		return mmci;
	}

	private URI getUri(Element elem)
	{
		String url = val(elem, "url");
		if (url == null) {
			stats.objectsRejected++;
			if (!suppressErrors) {
				String sn = taxon.getAcceptedName().getFullScientificName();
				error("Empty <url> element for \"%s\"", sn);
			}
			return null;
		}
		try {
			return new URI(url.trim());
		}
		catch (URISyntaxException e) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: \"%s\"", url);
			return null;
		}
	}

}
