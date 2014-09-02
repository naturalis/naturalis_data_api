package nl.naturalis.nda.elasticsearch.load.crs;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;

import org.domainobject.util.DOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class CrsTransfer {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CrsTransfer.class);
	private static final String ABCD_NAMESPACE_URI = "http://rs.tdwg.org/abcd/2.06/b/";


	public static Specimen transfer(Element recordElement)
	{
		Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setSourceSystemId(val(recordElement, "UnitGUID"));
		specimen.setAccessionSpecimenNumbers(val(recordElement, "AccessionSpecimenNumbers"));
		specimen.setAltitude(ival(recordElement, "Altitude"));
		specimen.setAltitudeUnit(val(recordElement, "AltitudeUnit"));
		specimen.setCollectionType(val(recordElement, "CollectionType"));
		specimen.setKindOfUnit(val(recordElement, "KindOfUnit"));
		specimen.setUnitGUID(val(recordElement, "UnitGUID"));
		return specimen;
	}


	private static String val(Element e, String tag)
	{
		return DOMUtil.getDescendantValue(e, tag);
	}


	private static int ival(Element e, String tag)
	{
		return DOMUtil.getDescendantIntValue(e, tag);
	}

}
