package nl.naturalis.nba.api.model;

import java.util.Date;

/**
 * A {@code Reference} represents a literature reference or common name.
 * 
 * @see http://wiki.tdwg.org/twiki/bin/view/ABCD/AbcdConcept0282
 * 
 */
public class Reference extends NBADomainObject {

	private String titleCitation;
	private String citationDetail;
	private String uri;
	private Person author;
	private Date publicationDate;

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
		return eq(titleCitation, other.titleCitation) && eq(author, other.author)
				&& eq(citationDetail, other.citationDetail)
				&& eq(publicationDate, other.publicationDate);
	}

	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + (titleCitation == null ? 0 : titleCitation.hashCode());
		hash = (hash * 31) + (author == null ? 0 : author.hashCode());
		hash = (hash * 31) + (citationDetail == null ? 0 : citationDetail.hashCode());
		hash = (hash * 31) + (publicationDate == null ? 0 : publicationDate.hashCode());
		// TODO: add more properties
		return hash;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder(64);
		if (titleCitation == null) {
			sb.append("### Title Not Available ###");
		}
		else {
			sb.append(titleCitation);
		}
		sb.append(';');
		if (citationDetail != null) {
			sb.append(' ').append(citationDetail);
		}
		sb.append(';');
		if (author != null) {
			if (author.getFullName() != null)
				sb.append(' ').append(author.getFullName());
			else if(author.getAgentText() != null)
				sb.append(' ').append(author.getAgentText());
		}
		sb.append(';');
		if(publicationDate != null) {
			sb.append(' ').append(publicationDate);
		}
		return sb.toString();
	}


	private static boolean eq(Object obj0, Object obj1)
	{
		if (obj0 == null) {
			if (obj1 == null) {
				return true;
			}
			return false;
		}
		return obj1 == null ? false : obj0.equals(obj1);
	}

}
