package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.ESConstants;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.Collections;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SOURCE_SYSTEM_ID;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.TaxonFields.ACCEPTEDNAME_FULL_SCIENTIFIC_NAME;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.TaxonFields.ACCEPTEDNAME_GENUS_OR_MONOMIAL;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.TaxonFields.ACCEPTEDNAME_INFRASPECIFIC_EPITHET;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.TaxonFields.ACCEPTEDNAME_SPECIFIC_EPITHET;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class TaxonDao extends AbstractTaxonDao {

    public TaxonDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    /**
     * Retrieves Taxon documents by scientific name. Since the Taxon document type is populated from two source systems
     * (CoL and NSR), a search by scientific name may result in 0, 1 or at most 2 search results.
     *
     * @param params params containing the the fields with their values
     * @return the search results
     */
    public SearchResultSet<Taxon> getTaxonDetail(QueryParams params) {
        String fullScientificName = params.getParam(ACCEPTEDNAME_FULL_SCIENTIFIC_NAME);
        String genus = params.getParam(ACCEPTEDNAME_GENUS_OR_MONOMIAL);
        String specificEpithet = params.getParam(ACCEPTEDNAME_SPECIFIC_EPITHET);
        String infraSpecificEpithet = params.getParam(ACCEPTEDNAME_INFRASPECIFIC_EPITHET);

        boolean validArgument = hasText(fullScientificName) || (hasText(genus) && hasText(specificEpithet));
        if (!validArgument) {
            throw new IllegalArgumentException("In accepted name, there should be a full scientific name or a" +
                    " genus and specific epithet.");
        }

        ScientificName scientificName = new ScientificName();
        scientificName.setFullScientificName(fullScientificName);
        scientificName.setGenusOrMonomial(genus);
        scientificName.setSpecificEpithet(specificEpithet);
        scientificName.setInfraspecificEpithet(infraSpecificEpithet);
        return lookupTaxonForScientificName(scientificName);
    }

    SearchResultSet<Taxon> lookupTaxonForSystemSourceId(String sourceSystemId) {
        if (!hasText(sourceSystemId)) {
            return new SearchResultSet<>();
        }
        QueryParams params = new QueryParams();
        params.add(SOURCE_SYSTEM_ID, sourceSystemId);
        return search(params, Collections.singleton(SOURCE_SYSTEM_ID), Collections.<String>emptySet(), false);
    }

    /**
     * Search for a taxon based on the provided information in the scientific name
     *
     * @param scientificName scientificName containing the information for the lookup
     * @return a SearchResultSet with the taxon if found
     */
    SearchResultSet<Taxon> lookupTaxonForScientificName(ScientificName scientificName) {
        String fullScientificName = scientificName.getFullScientificName();
        String genusOrMonomial = scientificName.getGenusOrMonomial();
        String specificEpithet = scientificName.getSpecificEpithet();
        String infraspecificEpithet = scientificName.getInfraspecificEpithet();

        BoolQueryBuilder boolQueryBuilder = boolQuery();
        if (hasText(fullScientificName)) {
            boolQueryBuilder.should(matchQuery(ACCEPTEDNAME_FULL_SCIENTIFIC_NAME, fullScientificName));
        }
        if (hasText(genusOrMonomial) && hasText(specificEpithet)) {
            BoolQueryBuilder acceptNameBoolQueryBuilder = boolQuery();
            acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_GENUS_OR_MONOMIAL, genusOrMonomial));
            acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_SPECIFIC_EPITHET, specificEpithet));
            if (hasText(infraspecificEpithet)) {
                acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_INFRASPECIFIC_EPITHET, infraspecificEpithet));
            }
            boolQueryBuilder.should(acceptNameBoolQueryBuilder);
        }

        SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(ESConstants.TAXON_TYPE).setQuery(
                filteredQuery(boolQueryBuilder, null)
        );

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        return responseToTaxonSearchResultSet(searchResponse, new QueryParams());
    }
}
