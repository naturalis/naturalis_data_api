package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**  
 * <h1>CORE</h1>
 *  Description: Class to create the element header of the Meta.xml file.
 * 
 *  @version 	1.0
 *  @author  	Reinier.Kartowikromo 
 *  @since		12-02-2015
 *   
 */

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
	@XmlAttribute(name="rowType")
	private String rowType;
	
	@XmlElement(name = "files")
	Files files;
	
	@XmlElement(name = "id")
	Id id;


	@XmlAttribute(name="index")
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
     */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

    /**
     * 
     * @return
     */
	public String getFieldsEnclosedBy()
	{
		return fieldsEnclosedBy;
	}

    /**
     * 
     * @param fieldsEnclosedBy
     */
	public void setFieldsEnclosedBy(String fieldsEnclosedBy)
	{
		this.fieldsEnclosedBy = fieldsEnclosedBy;
	}

    /**
     * 
     * @return
     */
	public String getFieldsTerminatedBy()
	{
		return fieldsTerminatedBy;
	}

    /**
     * 
     * @param fieldsTerminatedBy
     */
	public void setFieldsTerminatedBy(String fieldsTerminatedBy)
	{
		this.fieldsTerminatedBy = fieldsTerminatedBy;
	}

    /**
     * 
     * @return
     */
	public String getLinesTerminatedBy()
	{
		return linesTerminatedBy;
	}

    /**
     * 
     * @param linesTerminatedBy
     */
	public void setLinesTerminatedBy(String linesTerminatedBy)
	{
		this.linesTerminatedBy = linesTerminatedBy;
	}

    /**
     * 
     * @return
     */
	public String getIgnoreHeaderLines()
	{
		return ignoreHeaderLines;
	}

    /**
     * 
     * @param ignoreHeaderLines
     */
	public void setIgnoreHeaderLines(String ignoreHeaderLines)
	{
		this.ignoreHeaderLines = ignoreHeaderLines;
	}

    /**
     * 
     * @return
     */
	public String getRowtype()
	{
		return rowType;
	}

    /**
     * 
     * @param rowType
     */
	public void setRowtype(String rowType)
	{
		this.rowType = rowType;
	}

    /**
     * 
     * @return
     */
	public Id getId()
	{
		return id;
	}

    /**
     * 
     * @param id
     */
	public void setId(Id id)
	{
		this.id = id;
	}

    /**
     * 
     * @return
     */
	public String getIndex()
	{
		return index;
	}

	/**
	 * 
	 * @param index
	 */
	public void setIndex(String index)
	{
		this.index = index;
	}

    /**
     * 
     * @return
     */
	public List<Field> getField()
	{
		return fields;
	}
 
	/**
	 * 
	 * @param field
	 */
	public void setField(List<Field> field)
	{
		this.fields = field;
	}

	/**
	 * 
	 * @param field
	 */
	public void addField(Field field)
	{
		if (fields == null)
		{
			fields = new ArrayList<>();
		}
		fields.add(field);
	}
}
