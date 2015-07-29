package nl.naturalis.nda.domain;

public class SourceSystem {

	public static final SourceSystem BRAHMS = new SourceSystem("BRAHMS", "Naturalis - Botany catalogues");
	public static final SourceSystem COL = new SourceSystem("COL", "Species 2000 - Catalogue Of Life");
	public static final SourceSystem CRS = new SourceSystem("CRS", "Naturalis - Zoology and Geology catalogues");
	public static final SourceSystem NSR = new SourceSystem("NSR", "Naturalis - Nederlands Soortenregister");

	private String code;
	private String name;


	public SourceSystem()
	{
	}


	public SourceSystem(String code, String name)
	{
		this.code = code;
		this.name = name;
	}


	public String getCode()
	{
		return code;
	}


	public void setCode(String code)
	{
		this.code = code;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}


	@Override
	public int hashCode()
	{
		return System.identityHashCode(this);
	}


	@Override
	public String toString()
	{
		return code;
	}

}
