package nl.naturalis.nda.elasticsearch.load.crs;

import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;

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
		final Specimen specimen = new Specimen();
		specimen.setSourceSystem(SourceSystem.CRS);
		specimen.setSourceSystemId(val(recordElement, "UnitGUID"));
		specimen.setUnitID(val(recordElement, "UnitID"));
		specimen.setUnitGUID(val(recordElement, "UnitGUID"));
		specimen.setSourceInstitutionID(val(recordElement, "SourceInstitutionID"));
		specimen.setRecordBasis(val(recordElement, "RecordBasis"));
		specimen.setKindOfUnit(val(recordElement, "KindOfUnit"));
		specimen.setCollectionType(val(recordElement, "CollectionType"));
		specimen.setSex(val(recordElement, "Sex"));
		specimen.setPhaseOrStage(val(recordElement, "PhaseOrStage"));
		specimen.setAccessionSpecimenNumbers(val(recordElement, "AccessionSpecimenNumbers"));
		specimen.setTitle(val(recordElement, "Title"));
		String s = val(recordElement, "ObjectPublic");
		specimen.setObjectPublic(s != null && s.trim().equals("1"));
		s = val(recordElement, "MultiMediaPublic");
		specimen.setMultiMediaPublic(s != null && s.trim().equals("1"));
		List<Element> determinationElements = DOMUtil.getChildren(recordElement, "ncrsDetermination");
		for (Element e : determinationElements) {
			specimen.addIndentification(transferIdentification(e));
		}
		return specimen;
	}


	public static SpecimenIdentification transferIdentification(Element determinationElement)
	{
		SpecimenIdentification si = new SpecimenIdentification();
		return si;
	}


	private static String val(Element e, String tag)
	{
		return DOMUtil.getDescendantValue(e, tag, ABCD_NAMESPACE_URI);
	}


	private static int ival(Element e, String tag)
	{
		return DOMUtil.getDescendantIntValue(e, tag, ABCD_NAMESPACE_URI);
	}

}
