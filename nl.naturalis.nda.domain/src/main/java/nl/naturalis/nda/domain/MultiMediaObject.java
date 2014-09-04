package nl.naturalis.nda.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dwc:scientificName
 */
public class MultiMediaObject extends NdaTraceableObject {

	/**
	 * Enumeration of the possible types of a {@code Media} object.
	 * 
	 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dc:type
	 */
	public static enum Type
	{
		COLLECTION, STILL_IMAGE, SOUND, MOVING_IMAGE, INTERACTIVE_RESOURCE, TEXT, OTHER
	}

	private String title;
	private String caption;
	private String description;
	private Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints;

	private Type type;
	private int taxonCount;
	private List<String> subjectParts;
	private List<String> subjectOrientations;
	private List<String> phasesOrStages;
	private List<String> sexes;

	private List<Iptc4xmpExt> iptcInfo;
	private List<ScientificName> scientificNames;
	private List<DefaultClassification> defaultClassifications;
	private List<List<Monomial>> systemClassifications;


	public void addServiceAccessPoint(String uri, String format, ServiceAccessPoint.Variant variant)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new HashMap<ServiceAccessPoint.Variant, ServiceAccessPoint>();
		}
		serviceAccessPoints.put(variant, new ServiceAccessPoint(uri, format, variant));
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getCaption()
	{
		return caption;
	}


	public void setCaption(String caption)
	{
		this.caption = caption;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public Map<ServiceAccessPoint.Variant, ServiceAccessPoint> getServiceAccessPoints()
	{
		return serviceAccessPoints;
	}


	public void setServiceAccessPoints(Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints)
	{
		this.serviceAccessPoints = serviceAccessPoints;
	}


	public Type getType()
	{
		return type;
	}


	public void setType(Type type)
	{
		this.type = type;
	}


	public int getTaxonCount()
	{
		return taxonCount;
	}


	public void setTaxonCount(int taxonCount)
	{
		this.taxonCount = taxonCount;
	}


	public List<String> getSubjectParts()
	{
		return subjectParts;
	}


	public void setSubjectParts(List<String> subjectParts)
	{
		this.subjectParts = subjectParts;
	}


	public List<String> getSubjectOrientations()
	{
		return subjectOrientations;
	}


	public void setSubjectOrientations(List<String> subjectOrientations)
	{
		this.subjectOrientations = subjectOrientations;
	}


	public List<String> getPhasesOrStages()
	{
		return phasesOrStages;
	}


	public void setPhasesOrStages(List<String> phasesOrStages)
	{
		this.phasesOrStages = phasesOrStages;
	}


	public List<String> getSexes()
	{
		return sexes;
	}


	public void setSexes(List<String> sexes)
	{
		this.sexes = sexes;
	}


	public List<Iptc4xmpExt> getIptcInfo()
	{
		return iptcInfo;
	}


	public void setIptcInfo(List<Iptc4xmpExt> iptcInfo)
	{
		this.iptcInfo = iptcInfo;
	}


	public List<ScientificName> getScientificNames()
	{
		return scientificNames;
	}


	public void setScientificNames(List<ScientificName> scientificNames)
	{
		this.scientificNames = scientificNames;
	}


	public List<DefaultClassification> getDefaultClassifications()
	{
		return defaultClassifications;
	}


	public void setDefaultClassifications(List<DefaultClassification> defaultClassifications)
	{
		this.defaultClassifications = defaultClassifications;
	}


	public List<List<Monomial>> getSystemClassifications()
	{
		return systemClassifications;
	}


	public void setSystemClassifications(List<List<Monomial>> systemClassifications)
	{
		this.systemClassifications = systemClassifications;
	}

}
