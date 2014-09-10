package nl.naturalis.nda.domain;

import java.util.Date;

/**
 * A {@code Reference} represents a literature reference for a scientific name
 * or common name.
 * 
 */
public class Reference {

	private String title;
	private String author;
	private Date date;


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getAuthor()
	{
		return author;
	}


	public void setAuthor(String author)
	{
		this.author = author;
	}


	public Date getDate()
	{
		return date;
	}


	public void setDate(Date date)
	{
		this.date = date;
	}

}
