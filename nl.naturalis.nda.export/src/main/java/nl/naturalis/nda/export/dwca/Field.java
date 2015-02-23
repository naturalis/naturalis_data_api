package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement
public class Field
{
	private List<Field> flds;
	 
	
	@XmlAttribute(name="index")
    private String index;
	
	@XmlAttribute(name="term")
	private String term;
	
	
	public Field()
	{
		// TODO Auto-generated constructor stub
	}
	
    public Field(String index, String Term)
	{
		this.index = index;
		this.term = Term;
	}


	public String getIndex()
	{
		return index;
	}


	public void setIndex(String index)
	{
		this.index = index;
	}


	public String getTerm()
	{
		return term;
	}


	public void setTerm(String term)
	{
		this.term = term;
	}


	public void setFields(String indexvalue, String termvalue)
	{
		this.index = indexvalue;
		this.term = termvalue;
	}


	public List<Field> getFlds()
	{
		return flds;
	}


	public void setFlds(List<Field> flds)
	{
		this.flds = flds;
	}
	
	public void addFields(Field field) {
		  if (flds == null) {
			  flds = new ArrayList<Field>();
		  }
		  flds.add(field);
		 }


}
