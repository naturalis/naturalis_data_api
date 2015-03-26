package nl.naturalis.nda.export.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dataset")
public class Dataset
{
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "description")
	private String description;
	@XmlElement(name = "metadatalanguage")
	private String metadatalanguage;
	@XmlElement(name = "resourcelanguage")
	private String resourcelanguage;
	@XmlElement(name = "type")
	private String type;
	@XmlElement(name = "subtype")
	private String subtype;
	@XmlElement(name = "contact")
	Contact contacts;
	@XmlElement(name = "creator")
	Creator creator;
	@XmlElement(name = "provider")
	Provider provider;	
	
	public Dataset()
	{
		// TODO Auto-generated constructor stub
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getMetadatalanguage()
	{
		return metadatalanguage;
	}

	public void setMetadatalanguage(String metadatalanguage)
	{
		this.metadatalanguage = metadatalanguage;
	}

	public String getResourcelanguage()
	{
		return resourcelanguage;
	}

	public void setResourcelanguage(String resourcelanguage)
	{
		this.resourcelanguage = resourcelanguage;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getSubtype()
	{
		return subtype;
	}

	public void setSubtype(String subtype)
	{
		this.subtype = subtype;
	}


	
//	 public void addContact( Contact contact )
//	    {
//	        if( this.contacts == null )
//	        {
//	            this.contacts = new ArrayList<Contact>();
//	        }
//	        this.contacts.add( contact );
//
//	    }

	public Contact getContacts()
	{
		return contacts;
	}

	public void setContacts(Contact contacts)
	{
		this.contacts = contacts;
	}

	public Creator getCreator()
	{
		return creator;
	}

	public void setCreator(Creator creator)
	{
		this.creator = creator;
	}

	public Provider getProvider()
	{
		return provider;
	}

	public void setProvider(Provider provider)
	{
		this.provider = provider;
	}

	/*
	 List<Dataset> datasets;

		public List<Dataset> getDatasets()
		{
			return datasets;
		}

		
		public void setDatasets(List<Dataset> datasets)
		{
			this.datasets = datasets;
		}

		public void add(Dataset dataset)
		{
			if (this.datasets == null)
			{
				this.datasets = new ArrayList<Dataset>();
			}
			this.datasets.add(dataset);

		}*/

}
