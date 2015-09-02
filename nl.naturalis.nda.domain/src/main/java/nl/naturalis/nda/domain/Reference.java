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
		return eq(titleCitation, other.titleCitation) && eq(author, other.author) && eq(citationDetail, other.citationDetail)
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
		sb.append("{titleCitation: ").append(quote(titleCitation));
		sb.append(", author: ").append(author == null ? "null" : author.toString());
		sb.append(", citationDetail: ").append(quote(citationDetail));
		sb.append(", publicationDate: ").append(quote(publicationDate.toString()));
		sb.append("}");
		return sb.toString();
	}

	private static String quote(String s)
	{
		return s == null ? "null" : '"' + s + '"';
	}

	private static boolean eq(Object obj0, Object obj2)
	{
		if (obj0 == null) {
			if (obj2 == null) {
				return true;
			}
			return false;
		}
		return obj2 == null ? false : obj0.equals(obj2);
	}

}
