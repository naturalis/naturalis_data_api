package nl.naturalis.nba.dao.es.format.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Models the &lt;core&gt; element of a meta XML file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "core")
public class Core {

	@XmlAttribute(name = "encoding")
	private String encoding;
	@XmlAttribute(name = "fieldsEnclosedBy")
	private String fieldsEnclosedBy;
	@XmlAttribute(name = "fieldsTerminatedBy")
	private String fieldsTerminatedBy;
	@XmlAttribute(name = "linesTerminatedBy")
	private String linesTerminatedBy;
	@XmlAttribute(name = "ignoreHeaderLines")
	private String ignoreHeaderLines;
	@XmlAttribute(name = "rowType")
	private String rowType;

	@XmlElement(name = "files")
	Files files;

	@XmlElement(name = "id")
	Id id;

	@XmlAttribute(name = "index")
	private String index;

	@XmlElement(name = "field")
	private List<Field> fields;

	public Core()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return files
	 */
	public Files getFiles()
	{
		return files;
	}

	/**
	 * 
	 * @param files
	 *            set files
	 */

	public void setFiles(Files files)
	{
		this.files = files;
	}

	/**
	 * 
	 * @return encoding
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * 
	 * @param encoding
	 *            set encoding
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * 
	 * @return fieldsEnclosedBy
	 */
	public String getFieldsEnclosedBy()
	{
		return fieldsEnclosedBy;
	}

	/**
	 * 
	 * @param fieldsEnclosedBy
	 *            set fieldsEnclosedBy
	 */
	public void setFieldsEnclosedBy(String fieldsEnclosedBy)
	{
		this.fieldsEnclosedBy = fieldsEnclosedBy;
	}

	/**
	 * 
	 * @return fieldsEnclosedBy
	 */
	public String getFieldsTerminatedBy()
	{
		return fieldsTerminatedBy;
	}

	/**
	 * 
	 * @param fieldsTerminatedBy
	 *            set fieldsEnclosedBy
	 */
	public void setFieldsTerminatedBy(String fieldsTerminatedBy)
	{
		this.fieldsTerminatedBy = fieldsTerminatedBy;
	}

	/**
	 * 
	 * @return linesTerminatedBy
	 */
	public String getLinesTerminatedBy()
	{
		return linesTerminatedBy;
	}

	/**
	 * 
	 * @param linesTerminatedBy
	 *            set linesTerminatedBy
	 */
	public void setLinesTerminatedBy(String linesTerminatedBy)
	{
		this.linesTerminatedBy = linesTerminatedBy;
	}

	/**
	 * 
	 * @return ignoreHeaderLines
	 */
	public String getIgnoreHeaderLines()
	{
		return ignoreHeaderLines;
	}

	/**
	 * 
	 * @param ignoreHeaderLines
	 *            set ignoreHeaderLines
	 */
	public void setIgnoreHeaderLines(String ignoreHeaderLines)
	{
		this.ignoreHeaderLines = ignoreHeaderLines;
	}

	/**
	 * 
	 * @return rowType
	 */
	public String getRowtype()
	{
		return rowType;
	}

	/**
	 * 
	 * @param rowType
	 *            set rowType
	 */
	public void setRowtype(String rowType)
	{
		this.rowType = rowType;
	}

	/**
	 * 
	 * @return id
	 */
	public Id getId()
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 *            set value id
	 */
	public void setId(Id id)
	{
		this.id = id;
	}

	/**
	 * 
	 * @return result index
	 */
	public String getIndex()
	{
		return index;
	}

	/**
	 * 
	 * @param index
	 *            set value index
	 */
	public void setIndex(String index)
	{
		this.index = index;
	}

	/**
	 * 
	 * @return result fields
	 */
	public List<Field> getField()
	{
		return fields;
	}

	/**
	 * 
	 * @param field
	 *            set value fields
	 */
	public void setField(List<Field> field)
	{
		this.fields = field;
	}

	/**
	 * 
	 * @param field
	 *            set value field
	 */
	public void addField(Field field)
	{
		if (fields == null) {
			fields = new ArrayList<>();
		}
		fields.add(field);
	}
}
