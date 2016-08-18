package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.METADATA;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.XMLNS;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.XMLNS_XSI;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSet;

/**
 * JAXB class modeling the &lt;archive&gt; (root) element within the
 * meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
abstract class Archive {

	@XmlAttribute(name = "metadata")
	private final String metadata = METADATA;
	@XmlAttribute(name = "xmlns")
	private final String xmlns = XMLNS;
	@XmlAttribute(name = "xmlns:xsi")
	private final String xmlnsxsi = XMLNS_XSI;

	@XmlElement(name = "core")
	protected Core core;
	@XmlElement(name = "extension")
	protected List<Extension> extensions;

	Archive()
	{
		DwcaDataSetBuilder builder;
		builder = new DwcaDataSetBuilder(SPECIMEN, "test-data-set-01");
		DataSet ds = builder.build();
		this.core = new SpecimenCore().forDataSet(ds);
	}

}
