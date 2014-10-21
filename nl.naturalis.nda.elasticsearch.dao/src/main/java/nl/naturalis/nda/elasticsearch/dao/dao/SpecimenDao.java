package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.Collections;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class SpecimenDao extends AbstractDao {

    public SpecimenDao(Client esClient, String ndaIndexName) {
        super(esClient, ndaIndexName);
    }

    /**
     * Retrieves a single Specimen by its unitID.
     *
     * @param unitID The unitID of the {@link nl.naturalis.nda.domain.Specimen}
     * @return {@link nl.naturalis.nda.search.SearchResultSet} containing the
     * {@link nl.naturalis.nda.domain.Specimen}
     */
    public SearchResultSet<Specimen> getSpecimenDetail(String unitID) {
        //todo Huidige query werkt met match query. Dit is inefficient.
        //todo Mapping aanpassen, data herindexeren en dan deze query weggooien en gecommente versie weer gebruiken
        SearchResponse response = newSearchRequest().setTypes(SPECIMEN_TYPE).setQuery(matchQuery("unitID", unitID))
                                                    .execute().actionGet();
        //        SearchResponse response = newSearchRequest()
        //                .setTypes("Specimen")
        //                .setQuery(filteredQuery(
        //                                  matchAllQuery(),
        //                                  termFilter(
        //                                          "unitID",
        //                                          unitID
        //                                  )
        //                          )
        //                )
        //                .execute().actionGet();
        SearchResultSet<Specimen> resultSet = new SearchResultSet<>();
        SearchHit hit = response.getHits().getHits()[0];
        if (hit != null) {
            ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
            Specimen specimen = SpecimenTransfer.transfer(esSpecimen);
            resultSet.addSearchResult(specimen);
        }
        resultSet.setTotalSize(response.getHits().getTotalHits());
        QueryParams queryParams = new QueryParams();
        queryParams.put("unitID", Collections.singletonList(unitID));
        resultSet.setQueryParameters(queryParams);
        return resultSet;
    }
}
