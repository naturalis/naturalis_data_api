package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceSystem implements INbaModelObject {

	private static final String CODE_CRS    = "CRS";
	private static final String CODE_BRAHMS = "BRAHMS";
	private static final String CODE_COL    = "COL";
	private static final String CODE_NSR    = "NSR";
	private static final String CODE_GEO    = "GEO";
	private static final String CODE_NDFF   = "NDFF";
	private static final String CODE_XC     = "XC";
	private static final String CODE_OBS    = "OBS";

	private static final String NAME_CRS    = "Naturalis - Zoology and Geology catalogues";
	private static final String NAME_BRAHMS = "Naturalis - Botany catalogues";
	private static final String NAME_COL    = "Species 2000 - Catalogue Of Life";
	private static final String NAME_NSR    = "Naturalis - Dutch Species Register";
	private static final String NAME_GEO    = "Naturalis - Geo Areas";
	private static final String NAME_NDFF   = "NDFF - Nationale Databank Flora en Fauna";
	private static final String NAME_XC     = "Xeno-canto.org - Bird sounds";
	private static final String NAME_OBS    = "Observation.org - Nature observations";

	public static final SourceSystem CRS    = new SourceSystem(CODE_CRS, NAME_CRS);
	public static final SourceSystem BRAHMS = new SourceSystem(CODE_BRAHMS, NAME_BRAHMS);
	public static final SourceSystem COL    = new SourceSystem(CODE_COL, NAME_COL);
	public static final SourceSystem NSR    = new SourceSystem(CODE_NSR, NAME_NSR);
	public static final SourceSystem GEO    = new SourceSystem(CODE_GEO, NAME_GEO);
	public static final SourceSystem NDFF   = new SourceSystem(CODE_NDFF, NAME_NDFF);
	public static final SourceSystem XC     = new SourceSystem(CODE_XC, NAME_XC);
	public static final SourceSystem OBS    = new SourceSystem(CODE_OBS, NAME_OBS);

	public static SourceSystem[] getAllSourceSystems()
	{
		return new SourceSystem[] { BRAHMS, COL, CRS, GEO, NDFF, NSR, XC, OBS };
	}

	@JsonCreator
	public static SourceSystem getInstance(@JsonProperty("code") String code,
			@JsonProperty("name") String name)
	{
		if (code != null) {
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
				case CODE_XC:
				  return XC;
				case CODE_OBS:
				  return OBS;
			}
			throw new IllegalArgumentException("No such source system: " + code);
		}
		switch (name) {
			case NAME_CRS:
				return CRS;
			case NAME_BRAHMS:
				return BRAHMS;
			case NAME_COL:
				return COL;
			case NAME_NSR:
				return NSR;
			case NAME_NDFF:
				return NDFF;
			case NAME_GEO:
				return GEO;
			case NAME_XC:
			  return XC;
			case NAME_OBS:
			  return OBS;
		}
		throw new IllegalArgumentException("No such source system: " + name);
	}

	private final String code;
	private final String name;

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
