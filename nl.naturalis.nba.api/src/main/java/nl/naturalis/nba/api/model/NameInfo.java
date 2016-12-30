package nl.naturalis.nba.api.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class NameInfo implements INbaModelObject {

	@Analyzers({})
	private String documentType;
	@Analyzers({})
	private String field;
	@Analyzers({})
	private String contextField0;
	private String contextValue0;

	@NotIndexed
	private Set<String> documentIds;

	public NameInfo()
	{
	}

	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj.getClass() == NameInfo.class) {
			NameInfo other = (NameInfo) obj;
			return field.equals(other.field) && eq(contextValue0, other.contextValue0)
					&& eq(contextField0, other.contextField0)
					&& documentType.equals(other.documentType);
		}
		return false;
	}

	@JsonIgnore
	private int hash = 0;

	public int hashCode()
	{
		int h = hash;
		if (h == 0) {
			h = 17;
			h = (h * 31) + field.hashCode();
			h = (h * 31) + (contextValue0 == null ? 0 : contextValue0.hashCode());
			h = (h * 31) + (contextField0 == null ? 0 : contextField0.hashCode());
			h = (h * 31) + documentType.hashCode();
			hash = h;
		}
		return h;
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

	public String getDocumentType()
	{
		return documentType;
	}

	public void setDocumentType(String documentType)
	{
		this.documentType = documentType;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public String getContextField0()
	{
		return contextField0;
	}

	public void setContextField0(String contextField0)
	{
		this.contextField0 = contextField0;
	}

	public String getContextValue0()
	{
		return contextValue0;
	}

	public void setContextValue0(String contextValue0)
	{
		this.contextValue0 = contextValue0;
	}

	public Set<String> getDocumentIds()
	{
		return documentIds;
	}

	public void setDocumentIds(Set<String> documentIds)
	{
		this.documentIds = documentIds;
	}

}
