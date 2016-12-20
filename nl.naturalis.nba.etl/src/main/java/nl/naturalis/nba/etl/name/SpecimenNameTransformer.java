package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.api.model.NameInfo;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.ETLStatistics;

public class SpecimenNameTransformer extends AbstractNameTransformer<Specimen> {

	static final String FLD_SCIENTIFIC = "identifications.scientificName.fullScientificName";
	static final String FLD_GENUS = "identifications.scientificName.genusOrMonomial";
	static final String FLD_SUBGENUS = "identifications.scientificName.subgenus";
	static final String FLD_SPECIFIC = "identifications.scientificName.specificEpithet";
	static final String FLD_INFRASPECIFIC = "identifications.scientificName.infraspecificEpithet";
	static final String FLD_MONOMIAL_RANK = "identifications.systemClassification.rank";
	static final String FLD_MONOMIAL_NAME = "identifications.systemClassification.name";

	private final NameLoader loader;

	public SpecimenNameTransformer(ETLStatistics stats, NameLoader loader)
	{
		super(stats);
		this.loader = loader;
	}

	@Override
	protected String getObjectID()
	{
		return input.getId();
	}

	@Override
	protected List<Name> doTransform()
	{
		stats.recordsAccepted++;
		stats.objectsProcessed++;
		try {
			List<Name> result = new ArrayList<>();
			result.addAll(getScientificNames());
			result.addAll(getRanks());
			return result;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private List<Name> getScientificNames()
	{
		List<Name> names = new ArrayList<>(4);
		for (SpecimenIdentification si : input.getIdentifications()) {
			Name name = getName(si.getScientificName().getFullScientificName());
			name.addNameInfo(newNameInfo(FLD_SCIENTIFIC));
			names.add(name);
			names.addAll(getAcceptedNameNameParts(si.getScientificName()));
		}
		return names;
	}

	private List<Name> getRanks()
	{
		List<Name> ranks = new ArrayList<>(8);
		for (SpecimenIdentification si : input.getIdentifications()) {
			for (Monomial m : si.getSystemClassification()) {
				Name name = getName(m.getName());
				NameInfo nameInfo = newNameInfo(FLD_MONOMIAL_NAME);
				nameInfo.setContextField0(FLD_MONOMIAL_RANK);
				nameInfo.setContextValue0(m.getRank());
				name.addNameInfo(nameInfo);
				ranks.add(name);
			}
		}
		return ranks;
	}

	private List<Name> getAcceptedNameNameParts(ScientificName sn)
	{
		List<Name> parts = new ArrayList<>(4);
		if (sn.getGenusOrMonomial() != null) {
			parts.add(getNamePart(FLD_GENUS, sn.getGenusOrMonomial()));
		}
		if (sn.getSubgenus() != null) {
			parts.add(getNamePart(FLD_SUBGENUS, sn.getSubgenus()));
		}
		if (sn.getSpecificEpithet() != null) {
			parts.add(getNamePart(FLD_SPECIFIC, sn.getSpecificEpithet()));
		}
		if (sn.getInfraspecificEpithet() != null) {
			parts.add(getNamePart(FLD_INFRASPECIFIC, sn.getInfraspecificEpithet()));
		}
		return parts;
	}

	private Name getNamePart(String field, String value)
	{
		Name name = getName(value);
		name.addNameInfo(newNameInfo(field));
		return name;
	}

	private NameInfo newNameInfo(String field)
	{
		NameInfo nameInfo = new NameInfo();
		nameInfo.setSourceSystemCode(input.getSourceSystem().getCode());
		nameInfo.setDocumentId(input.getId());
		nameInfo.setField(field);
		return nameInfo;
	}

	private Name getName(String id)
	{
		Name name = loader.findInQueue(id);
		if (name == null) {
			name = ESUtil.find(NAME, id);
		}
		if (name == null) {
			name = new Name(id);
		}
		return name;
	}

}
