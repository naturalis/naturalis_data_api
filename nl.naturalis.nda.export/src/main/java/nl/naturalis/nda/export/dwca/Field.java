package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**  
 * <h1>FIELD</h1>
 *  Description: Class to create the Index-, term- and field element  of the Meta.xml file.
 * 
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *   
 */

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
	
	/**
	 * 
	 * @param index set index
	 * @param Term set term
	 */
    public Field(String index, String Term)
	{
		this.index = index;
		this.term = Term;
	}

   /**
    * 
    * @return index
    */
	public String getIndex()
	{
		return index;
	}

    /**
     * 
     * @param index set index
     */
	public void setIndex(String index)
	{
		this.index = index;
	}

    /**
     * 
     * @return term
     */
	public String getTerm()
	{
		return term;
	}

    /**
     * 
     * @param term set term
     */
	public void setTerm(String term)
	{
		this.term = term;
	}

    /**
     * 
     * @param indexvalue set indexvalue
     * @param termvalue set termvalue
     */
	public void setFields(String indexvalue, String termvalue)
	{
		this.index = indexvalue;
		this.term = termvalue;
	}

    /**
     * 
     * @return flds
     */
	public List<Field> getFlds()
	{
		return flds;
	}

    /**
     * 
     * @param flds set flds
     */
	public void setFlds(List<Field> flds)
	{
		this.flds = flds;
	}
	
	/**
	 * 
	 * @param field set field
	 */
	public void addFields(Field field) {
		  if (flds == null) {
			  flds = new ArrayList<>();
		  }
		  flds.add(field);
		 }
}
