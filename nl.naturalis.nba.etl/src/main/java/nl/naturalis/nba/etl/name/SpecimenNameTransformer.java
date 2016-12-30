package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.api.model.NameInfo;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.ETLStatistics;

class SpecimenNameTransformer extends AbstractNameTransformer<Specimen> {

	static final String FLD_SCIENTIFIC = "identifications.scientificName.fullScientificName";
	static final String FLD_GENUS = "identifications.scientificName.genusOrMonomial";
	static final String FLD_SUBGENUS = "identifications.scientificName.subgenus";
	static final String FLD_SPECIFIC = "identifications.scientificName.specificEpithet";
	static final String FLD_INFRASPECIFIC = "identifications.scientificName.infraspecificEpithet";
	static final String FLD_MONOMIAL_RANK = "identifications.systemClassification.rank";
	static final String FLD_MONOMIAL_NAME = "identifications.systemClassification.name";

	private HashMap<String, Name> nameObjects;

	SpecimenNameTransformer(ETLStatistics stats)
	{
		super(stats);
	}

	@Override
	protected String getObjectID()
	{
		return input.getId();
	}

	@Override
	protected List<Name> doTransform()
	{
		// No record-level validations, so:
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

	@Override
	void initializeOutputObjects(List<Specimen> specimens)
	{
		HashMap<String, Name> objs;
		objs = new HashMap<String, Name>((int) (specimens.size() / .75) + 1, 1F);
		for (Specimen specimen : specimens) {
			for (SpecimenIdentification si : specimen.getIdentifications()) {
				String name = si.getScientificName().getFullScientificName();
				objs.put(name, null);
				for (Monomial m : si.getSystemClassification()) {
					objs.put(m.getName(), null);
				}
				ScientificName sn = si.getScientificName();
				if (sn.getGenusOrMonomial() != null) {
					objs.put(sn.getGenusOrMonomial(), null);
				}
				if (sn.getSubgenus() != null) {
					objs.put(sn.getSubgenus(), null);
				}
				if (sn.getSpecificEpithet() != null) {
					objs.put(sn.getSpecificEpithet(), null);
				}
				if (sn.getInfraspecificEpithet() != null) {
					objs.put(sn.getInfraspecificEpithet(), null);
				}
			}
		}
		loadOrCreateNameObjects(objs);
		nameObjects = objs;
	}

	private static void loadOrCreateNameObjects(HashMap<String, Name> objs)
	{
		List<Name> nameObjects = ESUtil.find(NAME, objs.keySet());
		for (Name name : nameObjects) {
			objs.put(name.getName(), name);
		}
		Set<Map.Entry<String, Name>> entries = objs.entrySet();
		for (Map.Entry<String, Name> entry : entries) {
			if (entry.getValue() == null) {
				entry.setValue(new Name(entry.getKey()));
			}
		}
	}

	private List<Name> getScientificNames()
	{
		List<Name> names = new ArrayList<>(3);
		for (SpecimenIdentification si : input.getIdentifications()) {
			Name name = nameObjects.get(si.getScientificName().getFullScientificName());
			name.addNameInfo(newNameInfo(FLD_SCIENTIFIC));
			names.add(name);
			names.addAll(getAcceptedNameNameParts(si.getScientificName()));
		}
		return names;
	}

	private List<Name> getRanks()
	{
		List<Name> ranks = new ArrayList<>(12);
		for (SpecimenIdentification si : input.getIdentifications()) {
			for (Monomial m : si.getSystemClassification()) {
				Name name = nameObjects.get(m.getName());
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
		Name name = nameObjects.get(value);
		name.addNameInfo(newNameInfo(field));
		return name;
	}

	private NameInfo newNameInfo(String field)
	{
		NameInfo nameInfo = new NameInfo();
		nameInfo.setDocumentType(SPECIMEN.getName());
		nameInfo.setSourceSystemCode(input.getSourceSystem().getCode());
		nameInfo.setDocumentId(input.getId());
		nameInfo.setField(field);
		return nameInfo;
	}

}
