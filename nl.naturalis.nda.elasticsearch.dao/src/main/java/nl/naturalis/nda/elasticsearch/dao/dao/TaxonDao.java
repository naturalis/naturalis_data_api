package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.client.Client;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class TaxonDao extends AbstractTaxonDao {

    private static final Set<String> scienticNameFields = new HashSet<>(asList(
            "acceptedName.genusOrMonomial",
            "acceptedName.specificEpithet",
            "acceptedName.infraspecificEpithet")
    );

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
        params.add("_andOr", "AND"); //Operator is always AND to make sure all fields are matched.
        return search(params, scienticNameFields);
    }
}
