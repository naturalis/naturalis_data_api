package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.client.Client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BioportalTaxonDao extends AbstractTaxonDao {

    private static final Set<String> allowedFieldNamesForSearch = new HashSet<>(Arrays.asList(
            "acceptedName.genusOrMonomial",
            "acceptedName.subgenus",
            "acceptedName.specificEpithet",
            "acceptedName.infraspecificEpithet",
            "acceptedName.experts.fullName",
            "acceptedName.experts.organization.name",
            "acceptedName.taxonomicStatus",
            "vernacularNames.name",
            "vernacularNames.experts.fullName",
            "vernacularNames.experts.organization.name",
            "synonyms.scientificName.genusOrMonomial",
            "synonyms.subgenus",
            "synonyms.scientificName.specificEpithet",
            "synonyms.scientificName.infraspecificEpithet",
            "synonyms.scientificName.expert.fullName",
            "synonyms.scientificName.expert.organization.name",
            "synonyms.taxonomicStatus",
            "defaultClassification.kingdom",
            "defaultClassification.phylum",
            "defaultClassification.className",
            "defaultClassification.order",
            "defaultClassification.family",
            "defaultClassification.genus",
            "defaultClassification.subgenus",
            "defaultClassification. specificEpithet",
            "defaultClassification.infraspecificEpithet",
            "systemClassification.name",
            "experts.fullName"
    ));

    public BioportalTaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    /**
     * Retrieves taxa matching a variable number of criteria.
     *
     * @param params A {@link QueryParams} object containing:
     *               1. fields ... . A variable number of filters for fields. For example, the
     *               QueryParams object may contain a key “defaultClassification.genus” with a
     *               value of “Homo” and a key “defaultClassification.specificEpithet” with a
     *               value of “sapiens”. Fields must be mapped according to the mapping
     *               mechanism described above. Thus, if the QueryParams object contains a
     *               key “genus”, that key must be mapped to the “defaultClassification.genus”
     *               field.
     *               2. _andOr. An enumerated value with “AND” and “OR” as valid values. “AND”
     *               means all fields must match. “OR” means some fields must match. This is
     *               an optional parameter. By default only some fields must match. Will only
     *               be set if _source equals “SPECIMEN_EXTENDED_NAME_SEARCH”. This value
     *               represents the DAO method whose query logic to re-execute.
     *               3. _sort. The field to sort on. Fields must be mapped according to the
     *               mapping mechanism described above. Special sort value: “_score” (sort by
     *               relevance). In practice sorting is only allowed on _score and on
     *               identifications.scientificName.fullScientificName. This is an optional
     *               parameter. By default sorting is done on _score.
     *
     * @return search results
     */
    public SearchResultSet<Taxon> taxonSearch(QueryParams params) {
        return search(params, allowedFieldNamesForSearch);
    }

}
