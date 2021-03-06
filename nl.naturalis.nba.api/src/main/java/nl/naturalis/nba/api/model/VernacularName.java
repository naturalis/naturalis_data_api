package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;

public class VernacularName implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String name;
	private String language;
	private Boolean preferred;

	private List<Reference> references;
	private List<Expert> experts;

	public VernacularName()
	{
	}

	public VernacularName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public Boolean getPreferred()
	{
		return preferred;
	}

	public void setPreferred(Boolean preferred)
	{
		this.preferred = preferred;
	}

	public List<Reference> getReferences()
	{
		return references;
	}

	public void setReferences(List<Reference> references)
	{
		this.references = references;
	}

	public List<Expert> getExperts()
	{
		return experts;
	}

	public void setExperts(List<Expert> experts)
	{
		this.experts = experts;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof VernacularName)) {
			return false;
		}
		VernacularName other = (VernacularName) obj;
		return eq(name, other.name) && eq(language, other.language);
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + name.hashCode();
		hash = (hash * 31) + language == null ? 0 : language.hashCode();
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(64);
		sb.append(name);
		if (language != null) {
			sb.append(" (").append(language).append(')');
		}
		return sb.toString();
	}

	private static boolean eq(Object obj0, Object obj1)
	{
		if (obj0 == null) {
			if (obj1 == null)
				return true;
			return false;
		}
		return obj1 == null ? false : obj0.equals(obj1);
	}
}
