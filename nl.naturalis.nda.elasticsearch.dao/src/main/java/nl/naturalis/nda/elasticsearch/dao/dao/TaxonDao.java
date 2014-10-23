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
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.SOURCE_SYSTEM_ID;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.TaxonFields.*;
import static org.elasticsearch.index.query.QueryBuilders.*;

public class TaxonDao extends AbstractTaxonDao {

    private static final Set<String> allowedTaxonFields = new HashSet<>(asList(
            ACCEPTEDNAME_FULL_SCIENTIFIC_NAME,
            ACCEPTEDNAME_GENUS_OR_MONOMIAL,
            ACCEPTEDNAME_SPECIFIC_EPITHET,
            ACCEPTEDNAME_INFRASPECIFIC_EPITHET,
            SOURCE_SYSTEM_ID)
    );

    private static final Set<String> allowedTaxonFields_simpleSearchExceptions = Collections.emptySet();

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
        return search(params, allowedTaxonFields, allowedTaxonFields_simpleSearchExceptions, true);
    }

    /**
     * Search for a taxon based on the provided information in the scientific name
     *
     * @param scientificName scientificName containing the information for the lookup
     * @return a SearchResultSet with the taxon if found
     */
    public SearchResultSet<Taxon> lookupTaxonForScientificName(ScientificName scientificName) {
        String fullScientificName = scientificName.getFullScientificName();
        String genusOrMonomial = scientificName.getGenusOrMonomial();
        String specificEpithet = scientificName.getSpecificEpithet();
        String infraspecificEpithet = scientificName.getInfraspecificEpithet();

        BoolQueryBuilder boolQueryBuilder = boolQuery();
        if (hasText(fullScientificName)) {
            boolQueryBuilder.should(matchQuery(ACCEPTEDNAME_FULL_SCIENTIFIC_NAME, fullScientificName));
        }
        if (hasText(genusOrMonomial) || hasText(specificEpithet) || hasText(infraspecificEpithet)) {
            BoolQueryBuilder acceptNameBoolQueryBuilder = boolQuery();
            if (hasText(genusOrMonomial)) {
                acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_GENUS_OR_MONOMIAL, genusOrMonomial));
            }
            if (hasText(specificEpithet)) {
                acceptNameBoolQueryBuilder.must(matchQuery(ACCEPTEDNAME_SPECIFIC_EPITHET, specificEpithet));
            }
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
