package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;

import java.time.OffsetDateTime;
import java.util.List;

public class TaxonDescription implements INbaModelObject {


	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String description;
	private String category;
	private String language;
	private List<String> author;
	private License license;
	private OffsetDateTime publicationDate;

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public List<String> getAuthor() { return author; }

	public void setAuthor(List<String> author) { this.author = author; }

	public License getLicense() { return license; }

	public void setLicense(License license) { this.license = license; }

	public OffsetDateTime getPublicationDate() { return publicationDate; }

	public void setPublicationDate(OffsetDateTime publicationDate) { this.publicationDate = publicationDate; }

	@Override
	public String toString() {
		if (description == null) return null;
		StringBuilder sb = new StringBuilder(64);
		sb.append(description.strip());
		if (author != null || publicationDate != null) {
			sb.append(" (");
			int n = 0;
			for (String a : author) {
				sb.append(a);
				if (n < author.size()) {
					sb.append(", ");
					n++;
				}
			}
			if (publicationDate != null) {
				if (author != null) sb.append(", ");
				sb.append(publicationDate);
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
