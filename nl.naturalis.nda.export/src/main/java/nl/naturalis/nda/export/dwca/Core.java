package nl.naturalis.nda.export.dwca;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "core")
public class Core
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
	
	@XmlElement(name = "id")
	Id id;

	@XmlAttribute(name="number")
    private String number;


	@XmlAttribute(name="index")
	private String index;
	
	
	@XmlElement(name = "field")
	private List<Field> fields;
	
	
	public Core()
	{
		// TODO Auto-generated constructor stub
	}
	
	public Core(String indexValue, List<String>someListValue) 
	{
        this();
        this.number = indexValue;
  //      this.fieldsList = someListValue;  
    }


	public Files getFiles()
	{
		return files;
	}


	public void setFiles(Files files)
	{
		this.files = files;
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


	public Id getId()
	{
		return id;
	}


	public void setId(Id id)
	{
		this.id = id;
	}


	public String getNumber()
	{
		return number;
	}


	public void setNumber(String number)
	{
		this.number = number;
	}



	public String getIndex()
	{
		return index;
	}

	public void setIndex(String index)
	{
		this.index = index;
	}


	public List<Field> getField()
	{
		return fields;
	}

	public void setField(List<Field> field)
	{
		this.fields = field;
	}

	public void addField(Field field)
	{
		if (fields == null)
		{
			fields = new ArrayList<Field>();
		}
		fields.add(field);
	}


	


}
