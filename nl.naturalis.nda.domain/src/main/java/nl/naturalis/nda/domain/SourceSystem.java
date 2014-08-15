package nl.naturalis.nda.domain;

public class SourceSystem {

	public static final SourceSystem BRAHMS = new SourceSystem("BRAHMS", "Brahms");
	public static final SourceSystem COL = new SourceSystem("COL", "Catalogue Of Life");
	public static final SourceSystem CRS = new SourceSystem("CRS", "Naturalis Collectie Registratie Systeem");
	public static final SourceSystem NSR = new SourceSystem("NSR", "Nationaal Soortenregister");

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
		return code.equals(((SourceSystem) obj).code);
	}


	@Override
	public int hashCode()
	{
		return code.hashCode();
	}


	@Override
	public String toString()
	{
		return code;
	}

}
