package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.ServiceAccessPoint.Variant.MEDIUM_QUALITY;
import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.TransformUtil.equalizeNameComponents;
import static nl.naturalis.nba.etl.TransformUtil.guessMimeType;
import static nl.naturalis.nba.etl.nsr.NsrImportUtil.val;
import static nl.naturalis.nba.utils.xml.DOMUtil.getDescendants;
import static nl.naturalis.nba.utils.xml.DOMUtil.getValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.es.ESDateInput;
import nl.naturalis.nba.etl.AbstractXMLTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.NameMismatchException;

/**
 * Transforms and validates NSR source data.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
class NsrMultiMediaTransformer extends AbstractXMLTransformer<MultiMediaObject> {

	private Taxon taxon;
	private String[] testGenera;

	public NsrMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		/*
		 * We only need this because we don't want to swamp the log file with
		 * useless log messages in case we are creating a test set.
		 */
		testGenera = getTestGenera();
	}

	/**
	 * Set the taxon object associated with this multimedia object. The taxon
	 * object is extracted from the same XML record by the
	 * {@link NsrTaxonTransformer}.
	 * 
	 * @param taxon
	 */
	public void setTaxon(Taxon taxon)
	{
		this.taxon = taxon;
	}

	@Override
	protected String getObjectID()
	{
		return val(input.getRecord(), "nsr_id");
	}

	/**
	 * Transforms an XML record into one ore more {@code MultiMediaObject}s. The
	 * multimedia transformer does not keep track of record-level statistics.
	 * The assumption is that if the taxon transformer was able to extract a
	 * taxon from the XML record, then the record was OK at the record level.
	 */
	@Override
	protected List<MultiMediaObject> doTransform()
	{
		if (taxon == null) {
			stats.recordsSkipped++;
			if (logger.isDebugEnabled() && testGenera == null) {
				debug("Ignoring images for skipped or invalid taxon");
			}
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
		List<MultiMediaObject> mmos = new ArrayList<>(imageElems.size());
		for (Element imageElement : imageElems) {
			MultiMediaObject mmo = transformOne(imageElement);
			if (mmo != null)
				mmos.add(mmo);
		}
		return mmos.size() == 0 ? null : mmos;
	}

	private MultiMediaObject transformOne(Element e)
	{
		stats.objectsProcessed++;
		try {
			URI uri = getUri(e);
			if (uri == null)
				return null;
			MultiMediaObject mmo = newMediaObject();
			String uriHash = String.valueOf(uri.hashCode()).replace('-', '0');
			mmo.setSourceSystemId(objectID + '_' + uriHash);
			mmo.setUnitID(mmo.getSourceSystemId());
			mmo.setId(getElasticsearchId(NSR, mmo.getUnitID())); 
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
			mmo.setLicenseType(LicenseType.parse( val(e, "licence_type") ));
			mmo.setLicense(License.parse( val(e, "licence") ));
			mmo.setDescription(val(e, "short_description"));
			mmo.setCaption(mmo.getDescription());
			String date = val(e, "date_taken");
			if (date != null && date.equalsIgnoreCase("in prep")) {
				date = null;
				if (logger.isDebugEnabled()) {
					logger.debug("Invalid date: \"{}\"", date);
				}
			}
			String locality = val(e, "geography");
			if (locality != null || date != null) {
				MultiMediaGatheringEvent ge = new MultiMediaGatheringEvent();
				mmo.setGatheringEvents(Arrays.asList(ge));
				if (locality != null) {
					ge.setLocalityText(locality);
				}
				if (date != null) {
					ge.setDateTimeBegin(parseDateTaken(date));
					ge.setDateTimeEnd(ge.getDateTimeBegin());
				}
			}
			stats.objectsAccepted++;
			return mmo;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private static final DateTimeFormatter formatter0 = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	private static final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d MMMM yyyy");

	private OffsetDateTime parseDateTaken(String date)
	{
		ESDateInput input = new ESDateInput(date);
		OffsetDateTime odt = input.parseAsLocalDate(formatter0);
		if (odt == null) {
			odt = input.parseAsLocalDate(formatter1);
		}
		if (odt == null) {
			if (!suppressErrors) {
				warn("Invalid input for <date_taken>: " + date);
			}
		}
		return odt;
	}

	private MultiMediaObject newMediaObject() throws NameMismatchException
	{
		MultiMediaObject mmo = new MultiMediaObject();
		mmo.setSourceSystem(NSR);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("LNG NSR");
		String taxonId = getElasticsearchId(NSR, taxon.getSourceSystemId());
		mmo.setAssociatedTaxonReference(taxonId);
		mmo.setIdentifications(Arrays.asList(getIdentification()));
		equalizeNameComponents(mmo);
		return mmo;
	}

	private MultiMediaContentIdentification getIdentification()
	{
		Taxon t = taxon;
		MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
		mmci.setTaxonRank(t.getTaxonRank());
		mmci.setScientificName(t.getAcceptedName());
		mmci.setDefaultClassification(t.getDefaultClassification());
		// System classification disabled for specimens and multimedia
		// mmci.setSystemClassification(t.getSystemClassification());
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
