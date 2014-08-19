package nl.naturalis.nda.domain;

import java.util.List;
import java.util.Map;

/**
 * 
 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dwc:scientificName
 */
public class Media {

	/**
	 * Enumeration of the possible types of a {@code Media} object.
	 * 
	 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dc:type
	 */
	public static enum Type
	{
		COLLECTION, STILL_IMAGE, SOUND, MOVING_IMAGE, INTERACTIVE_RESOURCE, TEXT
	}

	private String title;
	private Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints;
	
	private Type type;
	private int taxonCount;
	private List<String> subjectParts;

	private List<ScientificName> scientificNames;
	private List<DefaultClassification> defaultClassifications;

}
