package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.transfer.MultiMediaObjectTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET;
import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao.SpecimenFields.IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET;

public class BioportalMultiMediaObjectDao extends AbstractDao {

    private static final Set<String> multiMediaSearchFields = new HashSet<>(Arrays.asList(
            "unitID",
            "sexes",
            "specimenTypeStatus",
            "phasesOrStages",
            "identifications.vernacularNames.name",
            "identifications.defaultClassification.kingdom",
            "identifications.defaultClassification.phylum",
            "identifications.defaultClassification.className",
            "identifications.defaultClassification.order",
            "identifications.defaultClassification.family",
            "identifications.defaultClassification.genusOrMonomial",
            "identifications.defaultClassification.subgenus",
            "identifications.defaultClassification.specificEpithet",
            "identifications.defaultClassification.infraspecificEpithet",
            "identifications.scientificName.genusOrMonomial",
            "identifications.scientificName.subgenus",
            "identifications.scientificName.specificEpithet",
            "identifications.scientificName.infraspecificEpithet",
            "gatheringEvents.siteCoordinates.point"
    ));

    private final BioportalTaxonDao taxonDao;

    public BioportalMultiMediaObjectDao(Client esClient, String ndaIndexName, BioportalTaxonDao taxonDao) {
        super(esClient, ndaIndexName);
        this.taxonDao = taxonDao;
    }

    /**
     * Retrieves multimedia matching a variable number of criteria. Rather than having
     one search term and a fixed set of fields to match the search term against, the
     fields to query and the values to look for are specified as parameters to this
     method. Nevertheless, the fields will always belong to the list:
     <ol>
        <li>unitID</li>
        <li>sexes</li>
        <li>specimenTypeStatus</li>
        <li>phasesOrStages</li>
        <li>identifications.vernacularNames.name</li>
        <li>identifications.defaultClassification.kingdom</li>
        <li>identifications.defaultClassification.phylum</li>
        <li>identifications.defaultClassification.className</li>
        <li>identifications.defaultClassification.order</li>
        <li>identifications.defaultClassification.family</li>
        <li>identifications.defaultClassification.genusOrMonomial</li>
        <li>identifications.defaultClassification.subgenus</li>
        <li>identifications.defaultClassification.specificEpithet</li>
        <li>identifications.defaultClassification.infraspecificEpithet</li>
        <li>identifications.scientificName.genusOrMonomial</li>
        <li>identifications.scientificName.subgenus</li>
        <li>identifications.scientificName.specificEpithet</li>
        <li>identifications.scientificName.infraspecificEpithet</li>
        <li>gatheringEvents.siteCoordinates.point (= geo search)</li>
     </ol>
     Name resolution is used to find additional MultiMediaObject documents.
     * @param params
     * @return
     */
    public SearchResultSet<MultiMediaObject> multiMediaObjectSearch(QueryParams params) {
        return search(params, multiMediaSearchFields);
    }

    /**
     * Method as generic as possible for internal use
     *
     * @param params search parameters
     * @param allowedFieldNames may be null if you don't want filtering
     * @return search results
     */
    SearchResultSet<MultiMediaObject> search(QueryParams params, Set<String> allowedFieldNames) {
        List<FieldMapping> fields = getSearchParamFieldMapping().getMultimediaMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                ? fields
                : filterAllowedFieldMappings(fields, allowedFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, MULTI_MEDIA_OBJECT_TYPE, true,
                buildNameResolutionQuery(allowedFields, taxonDao),
                Arrays.asList(IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
                        IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
                        IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET));

        return responseToMultiMediaObjectSearchResultSet(searchResponse);
    }

    private SearchResultSet<MultiMediaObject> responseToMultiMediaObjectSearchResultSet(SearchResponse searchResponse) {
        SearchResultSet<MultiMediaObject> searchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ESMultiMediaObject esObject = getObjectMapper().convertValue(hit.getSource(), ESMultiMediaObject.class);
            MultiMediaObject multiMediaObject = MultiMediaObjectTransfer.transfer(esObject);

            searchResultSet.addSearchResult(multiMediaObject);
        }

        // TODO links
        // TODO searchTerms
        searchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());

        return searchResultSet;
    }

}
