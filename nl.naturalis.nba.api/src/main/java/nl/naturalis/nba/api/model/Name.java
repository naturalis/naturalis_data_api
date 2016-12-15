package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

public class Name implements IDocumentObject {

	public static enum Type
	{
		SCIENTIFIC, SYNONYM, VERNACULAR, RANK, NAME_PART
	}

	private String id;
	private String name;
	private Type type;
	private String rankOrNamePart;
	private List<String> taxonIds;
	private List<String> specimenIds;
	private List<String> multimediaIds;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	public void addTaxonId(String id)
	{
		if (taxonIds == null) {
			taxonIds = new ArrayList<>(4);
		}
		taxonIds.add(id);
	}

	public void addSpecimenId(String id)
	{
		if (specimenIds == null) {
			specimenIds = new ArrayList<>();
		}
		specimenIds.add(id);
	}

	public void addMultiMediaId(String id)
	{
		if (multimediaIds == null) {
			multimediaIds = new ArrayList<>();
		}
		multimediaIds.add(id);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String getRankOrNamePart()
	{
		return rankOrNamePart;
	}

	public void setRankOrNamePart(String rankOrNamePart)
	{
		this.rankOrNamePart = rankOrNamePart;
	}

	public List<String> getTaxonIds()
	{
		return taxonIds;
	}

	public void setTaxonIds(List<String> taxonIds)
	{
		this.taxonIds = taxonIds;
	}

	public List<String> getSpecimenIds()
	{
		return specimenIds;
	}

	public void setSpecimenIds(List<String> specimenIds)
	{
		this.specimenIds = specimenIds;
	}

	public List<String> getMultimediaIds()
	{
		return multimediaIds;
	}

	public void setMultimediaIds(List<String> multimediaIds)
	{
		this.multimediaIds = multimediaIds;
	}

}
