package nl.naturalis.nda.export.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * <h1>ID</h1>
 *  Description: Methods what is used in the DwCAExporter- and Core class method<br>
 *               public void exportDwca(String zipFileName, String namecollectiontype, String totalsize) throws Exception
 *               
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *  
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "id")
public class Id
{
	@XmlAttribute(name="index")
	private int index;

	public Id()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return
	 */
	public int getIndex()
	{
		return index;
	}
    /**
     * 
     * @param index
     */
	public void setIndex(int index)
	{
		this.index = index;
	}

}
