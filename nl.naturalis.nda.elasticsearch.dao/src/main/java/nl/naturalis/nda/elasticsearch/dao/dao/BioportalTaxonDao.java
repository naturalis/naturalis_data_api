package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
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

public class BioportalTaxonDao extends AbstractDao {

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

    public SearchResultSet<Taxon> taxonSearch(QueryParams params) {
        return search(params, allowedFieldNamesForSearch);
    }

    /**
     * Method as generic as possible for internal use
     *
     * @param params search parameters
     * @param allowedFieldNames may be null if you don't want filtering
     * @return search results
     */
    SearchResultSet<Taxon> search(QueryParams params, Set<String> allowedFieldNames) {
        List<FieldMapping> fields = getSearchParamFieldMapping().getTaxonMappingForFields(params);
        List<FieldMapping> allowedFields = (allowedFieldNames == null)
                                           ? fields
                                           : filterAllowedFieldMappings(fields, allowedFieldNames);

        SearchResponse searchResponse = executeExtendedSearch(params, allowedFields, TAXON_TYPE, true);
        return responseToTaxonSearchResultSet(searchResponse);
    }

    private SearchResultSet<Taxon> responseToTaxonSearchResultSet(SearchResponse searchResponse) {
        SearchResultSet<Taxon> taxonSearchResultSet = new SearchResultSet<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ESTaxon esTaxon = getObjectMapper().convertValue(hit.getSource(), ESTaxon.class);
            Taxon taxon = TaxonTransfer.transfer(esTaxon);

            taxonSearchResultSet.addSearchResult(taxon);
        }

        // TODO links
        // TODO searchTerms
        taxonSearchResultSet.setTotalSize(searchResponse.getHits().getTotalHits());

        return taxonSearchResultSet;
    }
}
