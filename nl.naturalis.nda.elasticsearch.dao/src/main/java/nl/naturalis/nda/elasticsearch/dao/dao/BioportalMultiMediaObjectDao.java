package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.transfer.MultiMediaObjectTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.*;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.MultiMediaObjectFields.GATHERINGEVENTS_SITECOORDINATES_POINT;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.MultiMediaObjectFields.PHASES_OR_STAGES;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.MultiMediaObjectFields.SEXES;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.MultiMediaObjectFields.SPECIMEN_TYPE_STATUS;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.MULTI_MEDIA_OBJECT_TYPE;

public class BioportalMultiMediaObjectDao extends AbstractDao {

    private static final Set<String> multiMediaSearchFields = new HashSet<>(Arrays.asList(
            UNIT_ID,
            SEXES,
            SPECIMEN_TYPE_STATUS,
            PHASES_OR_STAGES,
            IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_GENUS_OR_MONOMIAL,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SUBGENUS,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_SPECIFIC_EPITHET,
            IDENTIFICATIONS_DEFAULT_CLASSIFICATION_INFRASPECIFIC_EPITHET,
            IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SUBGENUS,
            IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET,
            IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET,
            GATHERINGEVENTS_SITECOORDINATES_POINT
    ));

    private static final Set<String> multiMediaSearchFields_simpleSearchExceptions
            = Collections.singleton(GATHERINGEVENTS_SITECOORDINATES_POINT);

    private final BioportalTaxonDao bioportalTaxonDao;
    private final TaxonDao taxonDao;
    private final SpecimenDao specimenDao;

    public BioportalMultiMediaObjectDao(Client esClient, String ndaIndexName, BioportalTaxonDao bioportalTaxonDao,
                                        TaxonDao taxonDao, SpecimenDao specimenDao) {
        super(esClient, ndaIndexName);
        this.bioportalTaxonDao = bioportalTaxonDao;
        this.taxonDao = taxonDao;
        this.specimenDao = specimenDao;
    }

    /**
     * Retrieves multimedia matching a variable number of criteria. Rather than having
     * one search term and a fixed set of fields to match the search term against, the
     * fields to query and the values to look for are specified as parameters to this
     * method. Nevertheless, the fields will always belong to the list:
     * <ol>
     * <li>unitID</li>
     * <li>sexes</li>
     * <li>specimenTypeStatus</li>
     * <li>phasesOrStages</li>
     * <li>identifications.vernacularNames.name</li>
     * <li>identifications.defaultClassification.kingdom</li>
     * <li>identifications.defaultClassification.phylum</li>
     * <li>identifications.defaultClassification.className</li>
     * <li>identifications.defaultClassification.order</li>
     * <li>identifications.defaultClassification.family</li>
     * <li>identifications.defaultClassification.genusOrMonomial</li>
     * <li>identifications.defaultClassification.subgenus</li>
     * <li>identifications.defaultClassification.specificEpithet</li>
     * <li>identifications.defaultClassification.infraspecificEpithet</li>
     * <li>identifications.scientificName.genusOrMonomial</li>
     * <li>identifications.scientificName.subgenus</li>
     * <li>identifications.scientificName.specificEpithet</li>
     * <li>identifications.scientificName.infraspecificEpithet</li>
     * <li>gatheringEvents.siteCoordinates.point (= geo search)</li>
     * </ol>
     * Name resolution is used to find additional MultiMediaObject documents.
     *
     * @param params
     * @return
     */
    public SearchResultSet<MultiMediaObject> multiMediaObjectSearch(QueryParams params) {
        return search(params, multiMediaSearchFields, multiMediaSearchFields_simpleSearchExceptions);
    }

    /**
     * Retrieves a MultiMediaObject document by its unitID. The multimedia file is known and assumed to cross reference
     * a Taxon document rather than a Specimen document.
     * <p/>
     * A multimedia object retrieved through this method is always retrieved through a REST link in the response from
     * multiMediaObjectSearch. This method is aware of the result set generated by multiMediaObjectSearch and is
     * therefore capable of generating REST links to the previous and next multi in the result set. All parameters
     * passed to multiMediaObjectSearch will also be passed to this method. Basically, this method has to re-execute
     * the
     * query executed by multiMediaObjectSearch, pick out the MultiMediaObject with the specified unitID, and generate
     * REST links to the previous and next MultiMediaObject in the result set.
     *
     * @param params search parameters
     * @return the searchResultSet with the associated links
     */
    public SearchResultSet<MultiMediaObject> getTaxonMultiMediaObjectDetailWithinResultSet(QueryParams params) {
        SearchResultSet<MultiMediaObject> multiMediaObjectSearchResultSet = multiMediaObjectSearch(params);

        SearchResultSet<MultiMediaObject> resultSetWithPreviousAndNextLinks =
                createMultiMediaObjectDetailSearchResultSet(params, multiMediaObjectSearchResultSet);
        List<SearchResult<MultiMediaObject>> searchResults = resultSetWithPreviousAndNextLinks.getSearchResults();
        if (searchResults != null && searchResults.size() >= 1) {
            addAssociatedTaxonLink(searchResults.get(0).getLinks(), searchResults.get(0).getResult());
        }

        return resultSetWithPreviousAndNextLinks;
    }

    /**
     * Retrieves a MultiMediaObject document by its unitID. The multimedia file is known and assumed to cross reference
     * a Specimen document rather than a Taxon document.
     * <p/>
     * A multimedia object retrieved through this method is always retrieved through a REST link in the response from
     * multiMediaObjectSearch. This method is aware of the result set generated by multiMediaObjectSearch and is
     * therefore capable of generating REST links to the previous and next multi in the result set. All parameters
     * passed to multiMediaObjectSearch will also be passed to this method. Basically, this method has to re-execute
     * the
     * query executed by multiMediaObjectSearch, pick out the MultiMediaObject with the specified unitID, and generate
     * REST links to the previous and next MultiMediaObject in the result set.
     *
     * @param params search parameters
     * @return the searchResultSet with the associated links
     */
    public SearchResultSet<MultiMediaObject> getSpecimenMultiMediaObjectDetailWithinResultSet(QueryParams params) {
        SearchResultSet<MultiMediaObject> multiMediaObjectSearchResultSet = multiMediaObjectSearch(params);

        SearchResultSet<MultiMediaObject> resultSetWithPreviousAndNextLinks =
                createMultiMediaObjectDetailSearchResultSet(params, multiMediaObjectSearchResultSet);
        List<SearchResult<MultiMediaObject>> searchResults = resultSetWithPreviousAndNextLinks.getSearchResults();
        if (searchResults != null && searchResults.size() >= 1) {
            SearchResult<MultiMediaObject> multiMediaObjectSearchResult = searchResults.get(0);
            addTaxonLinksByScientificName(multiMediaObjectSearchResult.getLinks(),
                    multiMediaObjectSearchResult.getResult().getAssociatedSpecimen());
        }

        return resultSetWithPreviousAndNextLinks;
    }

    //================================================= Helper methods =================================================

    /**
     * Method as generic as possible for internal use.
     * <p/>
     * Evaluates simple search parameter.
     *
     * @param params            search parameters
     * @param allowedFieldNames may be null if you don't want filtering
     * @param simpleSearchFieldNameExceptions
     * @return search results
     */
    SearchResultSet<MultiMediaObject> search(QueryParams params, Set<String> allowedFieldNames,
                                             Set<String> simpleSearchFieldNameExceptions) {
        evaluateSimpleSearch(params, allowedFieldNames, simpleSearchFieldNameExceptions);
        List<FieldMapping> fields = getSearchParamFieldMapping().getMultimediaMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                ? fields
                : filterAllowedFieldMappings(fields, allowedFieldNames);

        QueryAndHighlightFields nameResolutionQuery = buildNameResolutionQuery(fields, params.getParam("_search"),
                                                                               bioportalTaxonDao, false);
        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, MULTI_MEDIA_OBJECT_TYPE, true,
                                                              nameResolutionQuery);

        return responseToMultiMediaObjectSearchResultSet(searchResponse, params);
    }

    /**
     * Create a SearchResultSet with a MultiMediaObject and it's previous and next link if the items exist.
     *
     * @param params          the params that contains the unitID
     * @param searchResultSet the set that should contain the MultiMediaObject with the
     * @return the search result with the selected MultiMediaObject
     */
    protected SearchResultSet<MultiMediaObject> createMultiMediaObjectDetailSearchResultSet(QueryParams params,
                                                                                            SearchResultSet<MultiMediaObject> searchResultSet) {
        SearchResultSet<MultiMediaObject> detailResultSet = new SearchResultSet<>();

        SearchResult<MultiMediaObject> previousMultiMediaObject = null;
        SearchResult<MultiMediaObject> nextMultiMediaObject = null;

        String unitID = params.getParam(UNIT_ID);
        if (hasText(unitID)) {
            List<SearchResult<MultiMediaObject>> searchResults = searchResultSet.getSearchResults();
            for (SearchResult<MultiMediaObject> searchResult : searchResults) {
                MultiMediaObject multiMediaObject = searchResult.getResult();
                if (multiMediaObject.getUnitID().equals(unitID)) {
                    int indexFoundMultiMediaObject = searchResults.indexOf(searchResult);
                    int searchResultSize = searchResults.size();
                    if (searchResultSize > 1) {
                        if (indexFoundMultiMediaObject == 0) {
                            // first item, no previous
                            nextMultiMediaObject = searchResults.get(1);
                        } else if (indexFoundMultiMediaObject == (searchResultSize - 1)) {
                            // last item, no next
                            previousMultiMediaObject = searchResults.get(indexFoundMultiMediaObject - 1);
                        } else {
                            nextMultiMediaObject = searchResults.get(indexFoundMultiMediaObject + 1);
                            previousMultiMediaObject = searchResults.get(indexFoundMultiMediaObject - 1);
                        }
                    }

                    if (previousMultiMediaObject != null) {
                        searchResult.addLink(new Link(SPECIMEN_DETAIL_BASE_URL + previousMultiMediaObject.getResult().getUnitID(),
                                "_previous"));
                    }
                    if (nextMultiMediaObject != null) {
                        searchResult.addLink(new Link(SPECIMEN_DETAIL_BASE_URL + nextMultiMediaObject.getResult().getUnitID(),
                                "_next"));
                    }

                    detailResultSet.addSearchResult(searchResult);
                }
            }
        }
        detailResultSet.setQueryParameters(params.copyWithoutGeoShape());
        return detailResultSet;
    }

    private void addAssociatedTaxonLink(List<Link> links, MultiMediaObject multiMediaObject) {
        String sourceSystemId = multiMediaObject.getAssociatedTaxonReference();
        SearchResultSet<Taxon> taxonDetail = taxonDao.lookupTaxonForSystemSourceId(sourceSystemId);
        if (taxonDetail.getSearchResults() != null && taxonDetail.getSearchResults().get(0) != null) {
            Taxon taxon = taxonDetail.getSearchResults().get(0).getResult();
            multiMediaObject.setAssociatedTaxon(taxon);
            links.add(new Link(TAXON_DETAIL_BASE_URL + taxon.getAcceptedName().getFullScientificName(), "_taxon"));
        }
    }

    private void addAssociatedSpecimenLink(List<Link> links, MultiMediaObject multiMediaObject, boolean addTaxonLinksByScientificName) {
        String associatedSpecimenReference = multiMediaObject.getAssociatedSpecimenReference();
        if (hasText(associatedSpecimenReference)) {
            SearchResultSet<Specimen> specimenDetail = specimenDao.getSpecimenDetail(associatedSpecimenReference);
            if (specimenDetail != null) {
                if (specimenDetail.getSearchResults() != null && specimenDetail.getSearchResults().get(0) != null) {
                    Specimen specimen = specimenDetail.getSearchResults().get(0).getResult();
                    multiMediaObject.setAssociatedSpecimen(specimen);
                    links.add(new Link(SPECIMEN_DETAIL_BASE_URL + specimen.getUnitID(), "_specimen"));

                    if (specimen.getIdentifications() != null && addTaxonLinksByScientificName) {
                        addTaxonLinksByScientificName(links, specimen);
                    }
                }
            }
        }
    }

    private void addTaxonLinksByScientificName(List<Link> links, Specimen specimen) {
        if (specimen != null && specimen.getIdentifications() != null) {
            for (SpecimenIdentification specimenIdentification : specimen.getIdentifications()) {
                ScientificName scientificName = specimenIdentification.getScientificName();
                SearchResultSet<Taxon> taxonSearchResultSet = taxonDao.lookupTaxonForScientificName(scientificName);

                List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
                for (SearchResult<Taxon> searchResult : searchResults) {
                    links.add(new Link(TAXON_DETAIL_BASE_URL + searchResult.getResult().getAcceptedName().getFullScientificName(), "_taxon"));
                }
            }
        }
    }

    private SearchResultSet<MultiMediaObject> responseToMultiMediaObjectSearchResultSet(SearchResponse searchResponse,
                                                                                        QueryParams params) {
        SearchResultSet<MultiMediaObject> searchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ESMultiMediaObject esObject = getObjectMapper().convertValue(hit.getSource(), ESMultiMediaObject.class);
            MultiMediaObject multiMediaObject = MultiMediaObjectTransfer.transfer(esObject);
            SearchResult<MultiMediaObject> multiMediaObjectSearchResult = new SearchResult<>();
            List<Link> links = new ArrayList<>();
            multiMediaObjectSearchResult.setResult(multiMediaObject);

            if (hasText(multiMediaObject.getAssociatedTaxonReference())) {
                addAssociatedTaxonLink(links, multiMediaObject);
            }
            if (hasText(multiMediaObject.getAssociatedSpecimenReference())) {
                addAssociatedSpecimenLink(links, multiMediaObject, false);
            }

            links.add(new Link(MULTIMEDIA_DETAIL_BASE_URL + multiMediaObject.getUnitID(), "_multimedia"));
            multiMediaObjectSearchResult.setLinks(links);
            searchResultSet.addSearchResult(multiMediaObjectSearchResult);
        }

        searchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());
        searchResultSet.setQueryParameters(params.copyWithoutGeoShape());

        return searchResultSet;
    }
}
