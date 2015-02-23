package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
public class Extension
{
	@XmlAttribute(name = "encoding")
	private String encoding;
	@XmlAttribute(name="fieldsEnclosedBy")
	private String fieldsEnclosedBy;
	@XmlAttribute(name="fieldsTerminatedBy")
	private String fieldsTerminatedBy;
	@XmlAttribute(name="linesTerminatedBy")
	private String linesTerminatedBy;
	@XmlAttribute(name="ignoreHeaderLines")
	private String ignoreHeaderLines;
	@XmlAttribute(name="rowtype")
	private String rowtype;
	
	@XmlElement(name = "files")
	Files files;
	
	@XmlElement(name = "coreid")
	Id id;
	
	@XmlElement(name = "field")
	private List<Field> fields;
	
	
	public Extension()
	{
		// TODO Auto-generated constructor stub
	}


	public String getEncoding()
	{
		return encoding;
	}


	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}


	public String getFieldsEnclosedBy()
	{
		return fieldsEnclosedBy;
	}


	public void setFieldsEnclosedBy(String fieldsEnclosedBy)
	{
		this.fieldsEnclosedBy = fieldsEnclosedBy;
	}


	public String getFieldsTerminatedBy()
	{
		return fieldsTerminatedBy;
	}


	public void setFieldsTerminatedBy(String fieldsTerminatedBy)
	{
		this.fieldsTerminatedBy = fieldsTerminatedBy;
	}


	public String getLinesTerminatedBy()
	{
		return linesTerminatedBy;
	}


	public void setLinesTerminatedBy(String linesTerminatedBy)
	{
		this.linesTerminatedBy = linesTerminatedBy;
	}


	public String getIgnoreHeaderLines()
	{
		return ignoreHeaderLines;
	}


	public void setIgnoreHeaderLines(String ignoreHeaderLines)
	{
		this.ignoreHeaderLines = ignoreHeaderLines;
	}


	public String getRowtype()
	{
		return rowtype;
	}


	public void setRowtype(String rowtype)
	{
		this.rowtype = rowtype;
	}


	public Files getFiles()
	{
		return files;
	}


	public void setFiles(Files files)
	{
		this.files = files;
	}


	public Id getId()
	{
		return id;
	}


	public void setId(Id id)
	{
		this.id = id;
	}


	public List<Field> getFields()
	{
		return fields;
	}


	public void setFields(List<Field> fields)
	{
		this.fields = fields;
	}
	
	public void addExtensionField(Field field)
	{
		if (fields == null)
		{
			fields = new ArrayList<Field>();
		}
		fields.add(field);
	}


}
