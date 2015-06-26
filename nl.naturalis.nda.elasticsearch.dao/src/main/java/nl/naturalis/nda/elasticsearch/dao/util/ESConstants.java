package nl.naturalis.nda.elasticsearch.dao.util;

/**
 * @author Byron Voorbach
 */
public class ESConstants {

	/**
	 * The name of the index which we're querying against
	 */
	public static final String INDEX_NAME = "nda";

	/**
	 * The Specimen type
	 */
	public static final String SPECIMEN_TYPE = "Specimen";

	/**
	 * The MultiMediaObject type
	 */
	public static final String MULTI_MEDIA_OBJECT_TYPE = "MultiMediaObject";

	/**
	 * The Taxon type
	 */
	public static final String TAXON_TYPE = "Taxon";

	public static final String IDENTIFYING_EPITHETS_DELIMITER = "||";

	public static class Fields {
		public static final String UNIT_ID = "unitID";
		public static final String SOURCE_SYSTEM_ID = "sourceSystemId";

		public static final String IDENTIFICATIONS_VERNACULAR_NAMES_NAME = "identifications.vernacularNames.name";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM = "identifications.defaultClassification.kingdom";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM = "identifications.defaultClassification.phylum";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME = "identifications.defaultClassification.className";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER = "identifications.defaultClassification.order";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY = "identifications.defaultClassification.family";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_GENUS_OR_MONOMIAL = "identifications.defaultClassification.genusOrMonomial";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_GENUS = "identifications.defaultClassification.genus";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SUBGENUS = "identifications.defaultClassification.subgenus";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SPECIFIC_EPITHET = "identifications.defaultClassification.specificEpithet";
		public static final String IDENTIFICATIONS_DEFAULT_CLASSIFICATION_INFRASPECIFIC_EPITHET = "identifications.defaultClassification.infraspecificEpithet";
		public static final String IDENTIFICATIONS_SCIENTIFIC_NAME_FULL_SCIENTIFIC_NAME = "identifications.scientificName.fullScientificName";
		public static final String IDENTIFICATIONS_SCIENTIFIC_NAME_SUBGENUS = "identifications.scientificName.subgenus";
		public static final String IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL = "identifications.scientificName.genusOrMonomial";
		public static final String IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET = "identifications.scientificName.specificEpithet";
		public static final String IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET = "identifications.scientificName.infraspecificEpithet";

		public static class MultiMediaObjectFields {
			public static final String SEXES = "sexes";
			public static final String THEME = "theme";
			public static final String ASSOCIATED_SPECIMEN_REFERENCE = "associatedSpecimenReference";
			public static final String ASSOCIATED_TAXON_REFERENCE = "associatedTaxonReference";
			public static final String SPECIMEN_TYPE_STATUS = "specimenTypeStatus";
			public static final String PHASES_OR_STAGES = "phasesOrStages";
			public static final String GATHERINGEVENTS_SITECOORDINATES_POINT = "gatheringEvents.siteCoordinates.point";
			public static final String COLLECTIONNAMES = "collectionnames";
		}

		public static class SpecimenFields {
			public static final String ASSEMBLAGE_ID = "assemblageID";
			public static final String SEX = "sex";
			public static final String THEME = "theme";
			public static final String TYPE_STATUS = "typeStatus";
			public static final String COLLECTORS_FIELD_NUMBER = "collectorsFieldNumber";
			public static final String PHASE_OR_STAGE = "phaseOrStage";
			public static final String IDENTIFICATIONS_SYSTEM_CLASSIFICATION_NAME = "identifications.systemClassification.name";
			public static final String GATHERINGEVENT_DATE_TIME_BEGIN = "gatheringEvent.dateTimeBegin";
			public static final String GATHERINGEVENT_DATE_TIME_END = "gatheringEvent.dateTimeEnd";
			public static final String GATHERINGEVENT_SITECOORDINATES_POINT = "gatheringEvent.siteCoordinates.point";
			public static final String GATHERINGEVENT_LOCALITY_TEXT = "gatheringEvent.localityText";
			public static final String GATHERINGEVENT_GATHERING_PERSONS_FULLNAME = "gatheringEvent.gatheringPersons.fullName";
			public static final String GATHERINGEVENT_GATHERING_ORGANISATIONS_NAME = "gatheringEvent.gatheringOrganizations.name";
			/* NDA-386 By: Reinier Date: 26 june 2015 */
			public static final String INST_COLL_SUBCOLL = "collectionname";
		}

		public static class TaxonFields {
			public static final String IDENTIFYING_EPITHETS = "identifyingEpithets";
			public static final String ACCEPTEDNAME_FULL_SCIENTIFIC_NAME = "acceptedName.fullScientificName";
			public static final String ACCEPTEDNAME_GENUS_OR_MONOMIAL = "acceptedName.genusOrMonomial";
			public static final String ACCEPTEDNAME_SUBGENUS = "acceptedName.subgenus";
			public static final String ACCEPTEDNAME_SPECIFIC_EPITHET = "acceptedName.specificEpithet";
			public static final String ACCEPTEDNAME_INFRASPECIFIC_EPITHET = "acceptedName.infraspecificEpithet";
			public static final String ACCEPTEDNAME_EXPERTS_FULLNAME = "acceptedName.experts.fullName";
			public static final String ACCEPTEDNAME_EXPERTS_ORGANIZATION_NAME = "acceptedName.experts.organization.name";
			public static final String ACCEPTEDNAME_TAXONOMIC_STATUS = "acceptedName.taxonomicStatus";
			public static final String VERNACULARNAMES_NAME = "vernacularNames.name";
			public static final String VERNACULARNAMES_EXPERTS_FULLNAME = "vernacularNames.experts.fullName";
			public static final String VERNACULARNAMES_EXPERTS_ORGANIZATION_NAME = "vernacularNames.experts.organization.name";
			public static final String SYNONYMS_GENUSORMONOMIAL = "synonyms.genusOrMonomial";
			public static final String SYNONYMS_SUBGENUS = "synonyms.subgenus";
			public static final String SYNONYMS_SPECIFIC_EPITHET = "synonyms.specificEpithet";
			public static final String SYNONYMS_INFRASPECIFIC_EPITHET = "synonyms.infraspecificEpithet";
			public static final String SYNONYMS_EXPERTS_FULLNAME = "synonyms.experts.fullName";
			public static final String SYNONYMS_EXPERTS_ORGANIZATION_NAME = "synonyms.experts.organization.name";
			public static final String SYNONYMS_EXPERT_FULLNAME = "synonyms.expert.fullName";
			public static final String SYNONYMS_EXPERT_ORGANIZATION_NAME = "synonyms.expert.organization.name";
			public static final String SYNONYMS_TAXONOMIC_STATUS = "synonyms.taxonomicStatus";
			public static final String DEFAULT_CLASSIFICATION_KINGDOM = "defaultClassification.kingdom";
			public static final String DEFAULT_CLASSIFICATION_PHYLUM = "defaultClassification.phylum";
			public static final String DEFAULT_CLASSIFICATION_CLASS_NAME = "defaultClassification.className";
			public static final String DEFAULT_CLASSIFICATION_ORDER = "defaultClassification.order";
			public static final String DEFAULT_CLASSIFICATION_FAMILY = "defaultClassification.family";
			public static final String DEFAULT_CLASSIFICATION_GENUS = "defaultClassification.genus";
			public static final String DEFAULT_CLASSIFICATION_SUBGENUS = "defaultClassification.subgenus";
			public static final String DEFAULT_CLASSIFICATION_SPECIFIC_EPITHET = "defaultClassification.specificEpithet";
			public static final String DEFAULT_CLASSIFICATION_INFRASPECIFIC_EPITHET = "defaultClassification.infraspecificEpithet";
			public static final String SYSTEM_CLASSIFICATION_NAME = "systemClassification.name";
			public static final String EXPERTS_FULLNAME = "experts.fullName";
		}
	}
}
