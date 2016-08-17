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

import nl.naturalis.nba.dao.es.format.DataSetEntity;

/**
 * JAXB class modeling the &lt;extension&gt; element within the meta&#46;xml
 * file.
 * 
 * @author Ayco Holleman
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
public class Extension {

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
	private final CoreId coreId;
	@XmlAttribute(name = "rowType")
	private final String rowType;
	@XmlElement(name = "files")
	private final Files files;
	@XmlElement(name = "field")
	private final List<Field> fields;

	public Extension(DataSetEntity entity, String rowType, String location)
	{
		this.coreId = new CoreId();
		this.rowType = rowType;
		this.files = new Files(location);
		this.fields = DwcaUtil.getMetaXmlFieldElements(entity);
	}
}
