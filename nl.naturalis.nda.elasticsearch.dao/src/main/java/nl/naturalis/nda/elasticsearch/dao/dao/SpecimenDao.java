package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.search.Link;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResult;
import nl.naturalis.nda.search.SearchResultSet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.Fields.UNIT_ID;
import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.SPECIMEN_TYPE;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class SpecimenDao extends AbstractDao {

    private final TaxonDao taxonDao;

    public SpecimenDao(Client esClient, String ndaIndexName, TaxonDao taxonDao, String baseUrl) {
        super(esClient, ndaIndexName, baseUrl);
        this.taxonDao = taxonDao;
    }

    /**
     * Retrieves a single Specimen by its unitID.
     *
     * @param unitID The unitID of the {@link nl.naturalis.nda.domain.Specimen}
     * @return {@link nl.naturalis.nda.search.SearchResultSet} containing the
     * {@link nl.naturalis.nda.domain.Specimen}
     */
    public SearchResultSet<Specimen> getSpecimenDetail(String unitID, String sessionId) {
        SearchResponse response = newSearchRequest()
                .setPreference(sessionId)
                .setTypes(SPECIMEN_TYPE)
                .setQuery(filteredQuery(
                                matchAllQuery(),
                                termFilter(
                                        UNIT_ID + ".raw",
                                        unitID
                                )
                        )
                )
                .execute().actionGet();

        SearchResultSet<Specimen> resultSet = new SearchResultSet<>();

        if (response.getHits().getHits().length != 0) {
            SearchHit hit = response.getHits().getHits()[0];
            Specimen specimen = null;
            if (hit != null) {
                ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
                specimen = SpecimenTransfer.transfer(esSpecimen);
                resultSet.addSearchResult(specimen);
            }
            resultSet.setTotalSize(response.getHits().getTotalHits());
            QueryParams queryParams = new QueryParams();
            queryParams.put(UNIT_ID, Collections.singletonList(unitID));
            resultSet.setQueryParameters(queryParams);

            List<Link> links = new ArrayList<>();
            if (specimen != null && specimen.getIdentifications() != null) {
                for (SpecimenIdentification specimenIdentification : specimen.getIdentifications()) {
                    ScientificName scientificName = specimenIdentification.getScientificName();
                    SearchResultSet<Taxon> taxonSearchResultSet = taxonDao.lookupTaxonForScientificName(scientificName, sessionId);
                    if (taxonSearchResultSet != null) {
                        List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
                        if (searchResults != null) {
                            for (SearchResult<Taxon> searchResult : searchResults) {
                                links.add(new Link("_taxon", TAXON_DETAIL_BASE_URL + createAcceptedNameParams(searchResult.getResult().getAcceptedName())));
                            }
                        }
                    }
                }
            }
            resultSet.setLinks(links);
            return resultSet;
        }
        return null;
    }
}
