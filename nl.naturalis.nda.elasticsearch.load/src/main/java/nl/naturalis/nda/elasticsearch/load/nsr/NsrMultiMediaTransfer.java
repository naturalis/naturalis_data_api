package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.LICENCE_TYPE;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SOURCE_INSTITUTION_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.ServiceAccessPoint;
import nl.naturalis.nda.domain.ServiceAccessPoint.Variant;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.TransferUtil;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class NsrMultiMediaTransfer {

	private static final Logger logger = LoggerFactory.getLogger(NsrMultiMediaTransfer.class);


	private NsrMultiMediaTransfer()
	{
	}


	static List<ESMultiMediaObject> getImages(Element taxonElement) throws Exception
	{
		List<Element> imageElements = DOMUtil.getDescendants(taxonElement, "image");
		if (imageElements == null) {
			return null;
		}
		ESTaxon taxon = NsrTaxonTransfer.transfer(taxonElement);
		if (taxon == null) {
			return null;
		}
		List<ESMultiMediaObject> mmos = new ArrayList<ESMultiMediaObject>(imageElements.size());
		for (Element imageElement : imageElements) {
			ESMultiMediaObject mmo = transfer(taxon, imageElement);
			if (mmo != null) {
				mmos.add(mmo);
			}
		}
		return mmos.size() == 0 ? null : mmos;
	}


	static ESMultiMediaObject transfer(ESTaxon taxon, Element imageElement)
	{
		String url = nl(DOMUtil.getValue(imageElement, "url"));
		if (url == null) {
			logger.warn(String.format("Missing image url for taxon \"%s\"", taxon.getAcceptedName().getFullScientificName()));
			return null;
		}
		ESMultiMediaObject mmo = new ESMultiMediaObject();
		mmo.setSourceSystem(SourceSystem.NSR);
		mmo.setSourceSystemId(taxon.getSourceSystemId() + ":" + url.hashCode());
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("LNG NSR");
		mmo.setLicenceType(LICENCE_TYPE);
		mmo.setLicence(LICENCE);
		mmo.setUnitID(mmo.getSourceSystemId());
		mmo.setAssociatedTaxonReference(taxon.getSourceSystemId());
		mmo.addServiceAccessPoint(new ServiceAccessPoint(url, null, Variant.MEDIUM_QUALITY));
		mmo.setCreator(nl(DOMUtil.getValue(imageElement, "photographer_name")));
		mmo.setCopyrightText(nl(DOMUtil.getValue(imageElement, "copyright")));
		mmo.setDescription(nl(DOMUtil.getValue(imageElement, "short_description")));
		mmo.setCaption(mmo.getDescription());
		String locality = nl(DOMUtil.getValue(imageElement, "geography"));
		String date = nl(DOMUtil.getValue(imageElement, "date_taken"));
		if (locality != null || date != null) {
			ESGatheringEvent ge = new ESGatheringEvent();
			mmo.setGatheringEvents(Arrays.asList(ge));
			ge.setLocalityText(locality);
			ge.setDateTimeBegin(TransferUtil.parseDate(date));
			ge.setDateTimeEnd(ge.getDateTimeBegin());
		}
		MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		identification.setTaxonRank(taxon.getTaxonRank());
		identification.setScientificName(taxon.getAcceptedName());
		identification.setDefaultClassification(taxon.getDefaultClassification());
		identification.setSystemClassification(taxon.getSystemClassification());
		identification.setVernacularNames(taxon.getVernacularNames());
		mmo.setIdentifications(Arrays.asList(identification));
		TransferUtil.equalizeNameComponents(mmo);
		return mmo;
	}


	private static String nl(String in)
	{
		return NsrTaxonTransfer.nl(in);
	}
}
