package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.ENCODING;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.FIELDS_ENCLOSED_BY;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.FIELDS_TERMINATED_BY;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.IGNORE_HEADER_LINES;
import static nl.naturalis.nba.dao.es.format.dwca.DwcaConstants.LINES_TERMINATED_BY;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;core&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "core")
class Core {

	@XmlAttribute(name = "encoding")
	private final String encoding = ENCODING;
	@XmlAttribute(name = "fieldsEnclosedBy")
	private final String fieldsEnclosedBy = FIELDS_ENCLOSED_BY;
	@XmlAttribute(name = "fieldsTerminatedBy")
	private final String fieldsTerminatedBy = FIELDS_TERMINATED_BY;
	@XmlAttribute(name = "linesTerminatedBy")
	private final String linesTerminatedBy = LINES_TERMINATED_BY;
	@XmlAttribute(name = "ignoreHeaderLines")
	private final String ignoreHeaderLines = IGNORE_HEADER_LINES;
	@XmlElement(name = "coreid")
	private final Id id = new Id();

	@XmlAttribute(name = "rowType")
	private String rowType;
	@XmlElement(name = "files")
	private Files files;
	@XmlElement(name = "field")
	private List<Field> fields;

	Core()
	{
	}

	void setRowType(String rowType)
	{
		this.rowType = rowType;
	}

	void setFiles(Files files)
	{
		this.files = files;
	}

	void setFields(List<Field> fields)
	{
		this.fields = fields;
	}

}
