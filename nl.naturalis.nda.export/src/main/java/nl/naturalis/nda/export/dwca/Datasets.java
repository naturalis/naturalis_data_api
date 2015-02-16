package nl.naturalis.nda.export.dwca;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "dataset")
@XmlType(propOrder =
{ "title", "description", "metadatalanguage", "resourcelanguage", "type", "subtype", "contacts" })
public class Datasets
{
	private String title;
	private String description;
	private String metadatalanguage;
	private String resourcelanguage;
	private String type;
	private String subtype;

	public Datasets()
	{
		// TODO Auto-generated constructor stub
	}

//	Boolean childrenAllowed;
//
//	public Boolean getChildrenAllowed()
//	{
//		return childrenAllowed;
//	}
//
//	@XmlAttribute(name = "children_allowed")
//	public void setChildrenAllowed(Boolean childrenAllowed)
//	{
//		this.childrenAllowed = childrenAllowed;
//	}

	public String getTitle()
	{
		return title;
	}

	@XmlElement(name = "title")
	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	@XmlElement(name = "description")
	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getMetadatalanguage()
	{
		return metadatalanguage;
	}

	@XmlElement(name = "metadatalanguage")
	public void setMetadatalanguage(String metadatalanguage)
	{
		this.metadatalanguage = metadatalanguage;
	}

	public String getResourcelanguage()
	{
		return resourcelanguage;
	}

	@XmlElement(name = "resourcelanguage")
	public void setResourcelanguage(String resourcelanguage)
	{
		this.resourcelanguage = resourcelanguage;
	}

	public String getType()
	{
		return type;
	}

	@XmlElement(name = "type")
	public void setType(String type)
	{
		this.type = type;
	}

	public String getSubtype()
	{
		return subtype;
	}

	@XmlElement(name = "subtype")
	public void setSubtype(String subtype)
	{
		this.subtype = subtype;
	}
	
	Contacts contacts; 
	
    public Contacts getContacts()
    {
        return contacts;
    }

    @XmlElement( name = "contact" )
    public void setContacts( Contacts contacts )
    {
        this.contacts = contacts;
    }

	

}
