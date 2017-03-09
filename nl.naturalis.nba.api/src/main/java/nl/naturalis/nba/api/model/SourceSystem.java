package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceSystem implements INbaModelObject {

	private static final String CODE_CRS = "CRS";
	private static final String CODE_BRAHMS = "BRAHMS";
	private static final String CODE_COL = "COL";
	private static final String CODE_NSR = "NSR";
	private static final String CODE_GEO = "GEO";
	private static final String CODE_NDFF = "NDFF";

	public static final SourceSystem CRS = new SourceSystem(CODE_CRS,
			"Naturalis - Zoology and Geology catalogues");
	public static final SourceSystem BRAHMS = new SourceSystem(CODE_BRAHMS,
			"Naturalis - Botany catalogues");
	public static final SourceSystem COL = new SourceSystem(CODE_COL,
			"Species 2000 - Catalogue Of Life");
	public static final SourceSystem NSR = new SourceSystem(CODE_NSR,
			"Naturalis - Nederlands Soortenregister");
	public static final SourceSystem NDFF = new SourceSystem(CODE_NDFF,
			"NDFF - Nationale Databank Flora en Fauna");
	public static final SourceSystem GEO = new SourceSystem(CODE_GEO, "Naturalis - Geo Areas");

	@JsonCreator
	public static SourceSystem forCode(@JsonProperty("code") String code)
	{
		switch (code) {
			case CODE_CRS:
				return CRS;
			case CODE_BRAHMS:
				return BRAHMS;
			case CODE_COL:
				return COL;
			case CODE_NSR:
				return NSR;
			case CODE_NDFF:
				return NDFF;
			case CODE_GEO:
				return GEO;
		}
		throw new IllegalArgumentException("No such source system: " + code);
	}

	private String code;
	private String name;

	private SourceSystem(String code, String name)
	{
		this.code = code;
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public String getName()
	{
		return name;
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
