package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.client.Client;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class TaxonDao extends AbstractTaxonDao {

    private static final Set<String> allowedTaxonFields = new HashSet<>(asList(
            "acceptedName.genusOrMonomial",
            "acceptedName.specificEpithet",
            "acceptedName.infraspecificEpithet",
            "sourceSystemId")
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
        return search(params, allowedTaxonFields, true);
    }

    /**
     * Method that provides
     *
     * @param genusOrMonomial
     * @param specificEpithet
     * @param infraspecificEpithet
     * @return
     */
    public SearchResultSet<Taxon> lookupTaxonForScientificName(String genusOrMonomial, String specificEpithet,
                                                               String infraspecificEpithet) {
        QueryParams queryParams = new QueryParams();
        queryParams.add("_andOr", "AND"); //Operator is always AND to make sure all fields are matched.
        queryParams.add("acceptedName.genusOrMonomial", genusOrMonomial);
        queryParams.add("acceptedName.specificEpithet", specificEpithet);
        queryParams.add("acceptedName.infraspecificEpithet", infraspecificEpithet);
        return getTaxonDetail(queryParams);
    }
}
