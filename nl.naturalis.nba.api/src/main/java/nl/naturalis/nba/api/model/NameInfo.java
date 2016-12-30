package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class NameInfo implements INbaModelObject {

	@Analyzers({})
	private String documentType;
	@Analyzers({})
	private String field;
	@NotIndexed
	private String documentId;
	@Analyzers({})
	private String contextField0;
	private String contextValue0;
	@Analyzers({})
	private String sourceSystemCode;

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
			return documentId.equals(other.documentId) && field.equals(other.field)
					&& eq(contextValue0, other.contextValue0)
					&& eq(contextField0, other.contextField0)
					&& documentType.equals(other.documentType);
		}
		return false;
	}

	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + documentId.hashCode();
		hash = (hash * 31) + field.hashCode();
		hash = (hash * 31) + (contextValue0 == null ? 0 : contextValue0.hashCode());
		hash = (hash * 31) + (contextField0 == null ? 0 : contextField0.hashCode());
		hash = (hash * 31) + documentType.hashCode();
		return hash;
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

	public String getDocumentId()
	{
		return documentId;
	}

	public void setDocumentId(String documentId)
	{
		this.documentId = documentId;
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

	public String getSourceSystemCode()
	{
		return sourceSystemCode;
	}

	public void setSourceSystemCode(String sourceSystemCode)
	{
		this.sourceSystemCode = sourceSystemCode;
	}

}
