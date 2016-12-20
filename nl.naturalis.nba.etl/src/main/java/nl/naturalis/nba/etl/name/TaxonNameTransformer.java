package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;
import static nl.naturalis.nba.dao.DocumentType.TAXON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.api.model.NameInfo;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * Transforms Taxon documents into Name documents.
 * 
 * @author Ayco Holleman
 *
 */
class TaxonNameTransformer extends AbstractNameTransformer<Taxon> {

	static final String FLD_ACCEPTED_FULL_NAME = "acceptedName.fullScientificName";
	static final String FLD_ACCEPTED_GENUS = "acceptedName.genusOrMonomial";
	static final String FLD_ACCEPTED_SUBGENUS = "acceptedName.subgenus";
	static final String FLD_ACCEPTED_SPECIFIC = "acceptedName.specificEpithet";
	static final String FLD_ACCEPTED_INFRASPECIFIC = "acceptedName.infraspecificEpithet";
	static final String FLD_SYNONYM_FULL_NAME = "synonyms.fullScientificName";
	static final String FLD_SYNONYM_TAXONOMIC_STATUS = "synonyms.taxonomicStatus";
	static final String FLD_SYNONYM_GENUS = "synonyms.genusOrMonomial";
	static final String FLD_SYNONYM_SUBGENUS = "synonyms.subgenus";
	static final String FLD_SYNONYM_SPECIFIC = "synonyms.specificEpithet";
	static final String FLD_SYNONYM_INFRASPECIFIC = "synonyms.infraspecificEpithet";
	static final String FLD_VERNACULAR = "vernacularNames.name";
	static final String FLD_MONOMIAL_RANK = "systemClassification.rank";
	static final String FLD_MONOMIAL_NAME = "systemClassification.name";

	private NameLoader loader;

	TaxonNameTransformer(ETLStatistics stats, NameLoader loader)
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
			result.addAll(getAcceptedName());
			result.addAll(getNonAcceptedNames());
			result.addAll(getVernacularNames());
			result.addAll(getRanks());
			return result;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}

	private List<Name> getAcceptedName()
	{
		List<Name> names = new ArrayList<>(4);
		Name name = getName(input.getAcceptedName().getFullScientificName());
		name.addNameInfo(newNameInfo(FLD_ACCEPTED_FULL_NAME));
		names.add(name);
		names.addAll(getAcceptedNameNameParts(input.getAcceptedName()));
		return names;
	}

	private List<Name> getNonAcceptedNames()
	{
		if (input.getSynonyms() == null) {
			return Collections.emptyList();
		}
		List<Name> names = new ArrayList<>(4);
		for (ScientificName syn : input.getSynonyms()) {
			Name name = getName(syn.getFullScientificName());
			NameInfo nameInfo = newNameInfo(FLD_SYNONYM_FULL_NAME);
			nameInfo.setContextField0(FLD_SYNONYM_TAXONOMIC_STATUS);
			nameInfo.setContextValue0(syn.getTaxonomicStatus().toString());
			name.addNameInfo(nameInfo);
			names.add(name);
			names.addAll(getSynonymNameParts(syn));
		}
		return names;
	}

	private List<Name> getRanks()
	{
		List<Name> ranks = new ArrayList<>(8);
		for (Monomial m : input.getSystemClassification()) {
			Name name = getName(m.getName());
			NameInfo nameInfo = newNameInfo(FLD_MONOMIAL_NAME);
			nameInfo.setContextField0(FLD_MONOMIAL_RANK);
			nameInfo.setContextValue0(m.getRank());
			ranks.add(name);
		}
		return ranks;
	}

	private List<Name> getAcceptedNameNameParts(ScientificName sn)
	{
		List<Name> parts = new ArrayList<>(4);
		if (sn.getGenusOrMonomial() != null) {
			parts.add(getNamePart(FLD_ACCEPTED_GENUS, sn.getGenusOrMonomial()));
		}
		if (sn.getSubgenus() != null) {
			parts.add(getNamePart(FLD_ACCEPTED_SUBGENUS, sn.getSubgenus()));
		}
		if (sn.getSpecificEpithet() != null) {
			parts.add(getNamePart(FLD_ACCEPTED_SPECIFIC, sn.getSpecificEpithet()));
		}
		if (sn.getInfraspecificEpithet() != null) {
			parts.add(getNamePart(FLD_ACCEPTED_INFRASPECIFIC, sn.getInfraspecificEpithet()));
		}
		return parts;
	}

	private List<Name> getSynonymNameParts(ScientificName sn)
	{
		List<Name> parts = new ArrayList<>(4);
		if (sn.getGenusOrMonomial() != null) {
			parts.add(getNamePart(FLD_SYNONYM_GENUS, sn.getGenusOrMonomial()));
		}
		if (sn.getSubgenus() != null) {
			parts.add(getNamePart(FLD_SYNONYM_SUBGENUS, sn.getSubgenus()));
		}
		if (sn.getSpecificEpithet() != null) {
			parts.add(getNamePart(FLD_SYNONYM_SPECIFIC, sn.getSpecificEpithet()));
		}
		if (sn.getInfraspecificEpithet() != null) {
			parts.add(getNamePart(FLD_SYNONYM_INFRASPECIFIC, sn.getInfraspecificEpithet()));
		}
		return parts;
	}

	private Name getNamePart(String field, String value)
	{
		Name name = getName(value);
		name.addNameInfo(newNameInfo(field));
		return name;
	}

	private List<Name> getVernacularNames()
	{
		if (input.getVernacularNames() == null) {
			return Collections.emptyList();
		}
		List<Name> names = new ArrayList<>(4);
		for (VernacularName vn : input.getVernacularNames()) {
			Name name = getName(vn.getName());
			name.addNameInfo(newNameInfo(FLD_VERNACULAR));
			names.add(name);
		}
		return names;
	}

	private NameInfo newNameInfo(String field)
	{
		NameInfo nameInfo = new NameInfo();
		nameInfo.setDocumentType(TAXON.getName());
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
