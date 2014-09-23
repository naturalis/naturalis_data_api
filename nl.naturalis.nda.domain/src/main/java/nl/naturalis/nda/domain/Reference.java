package nl.naturalis.nda.domain;

import java.util.Date;

/**
 * A {@code Reference} represents a literature reference or common name.
 * 
 * @see http://wiki.tdwg.org/twiki/bin/view/ABCD/AbcdConcept0282
 * 
 */
public class Reference {

	private String titleCitation;
	private String citationDetail;
	private String uri;
	private Person author;
	private Date publicationDate;


	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Reference)) {
			return false;
		}
		Reference other = (Reference) obj;
		// TODO: compare more properties
		return titleCitation.equals(other.titleCitation);
	}


	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + titleCitation.hashCode();
		// TODO: add more properties
		return hash;
	}


	public String getTitleCitation()
	{
		return titleCitation;
	}


	public void setTitleCitation(String titleCitation)
	{
		this.titleCitation = titleCitation;
	}


	public String getCitationDetail()
	{
		return citationDetail;
	}


	public void setCitationDetail(String citationDetail)
	{
		this.citationDetail = citationDetail;
	}


	public String getUri()
	{
		return uri;
	}


	public void setUri(String uri)
	{
		this.uri = uri;
	}


	public Person getAuthor()
	{
		return author;
	}


	public void setAuthor(Person author)
	{
		this.author = author;
	}


	public Date getPublicationDate()
	{
		return publicationDate;
	}


	public void setPublicationDate(Date publicationDate)
	{
		this.publicationDate = publicationDate;
	}

}
