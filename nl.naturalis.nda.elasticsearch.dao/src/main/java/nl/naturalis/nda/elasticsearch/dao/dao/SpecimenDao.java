package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.transfer.TaxonTransfer;
import nl.naturalis.nda.search.SearchResultSet;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryParser;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class SpecimenDao extends AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(SpecimenDao.class);


	public static void main(String[] args) throws JsonProcessingException
	{
		Settings settings = ImmutableSettings.settingsBuilder().put(CLUSTER_NAME_PROPERTY, CLUSTER_NAME_PROPERTY_VALUE).build();
		Client esClient = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(ES_HOST, ES_PORT));

		SpecimenDao dao = new SpecimenDao(esClient, SPECIMEN_INDEX_NAME);

		logger.info("\n");
		String unitID = "L  0191413";
		logger.info("------ Firing 'getSpecimenDetail' query ------");
		logger.info("Retrieving specimen with unitID: '" + unitID + "'");
		SearchResultSet<Specimen> specimenDetail = dao.getSpecimenDetail(unitID);
		logger.info(getObjectMapper().writeValueAsString(specimenDetail.getSearchResults().get(0).getResult()));
	}


	public SpecimenDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}


	// [Ayco] Dit is een check op de opmerkingen van Byron bij
	// getSpecimenDetail(). Deze (filtered) query werkt zoals
	// verwacht. PS methods die een query met een unique ID doen
	// en die niets teruggeven moeten null teruggeven. De REST
	// schil vertaalt dit naar 404 (NOT FOUND).
	public SearchResultSet<Specimen> getDetail(String unitID)
	{
		SearchRequestBuilder srb = esClient.prepareSearch("nda");
		srb.setTypes(LuceneType.SPECIMEN.toString());
		FilteredQueryBuilder query = filteredQuery(matchAllQuery(), termFilter("unitID", unitID));
		srb.setQuery(query);
		SearchResponse response = srb.execute().actionGet();
		if (response.getHits().getTotalHits() == 0) {
			return null;
		}
		SearchResultSet<Specimen> resultSet = new SearchResultSet<>();
		resultSet.setTotalSize(response.getHits().getTotalHits());
		SearchHit hit = response.getHits().getHits()[0];
		ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
		Specimen specimen = SpecimenTransfer.transfer(esSpecimen);
		resultSet.addSearchResult(specimen);
		return resultSet;
	}


	/**
	 * Retrieves a single Specimen by its unitID.
	 * 
	 * @param unitID The unitID of the {@link nl.naturalis.nda.domain.Specimen}
	 * @return {@link nl.naturalis.nda.search.SearchResultSet} containing the
	 *         {@link nl.naturalis.nda.domain.Specimen}
	 */
	public SearchResultSet<Specimen> getSpecimenDetail(String unitID)
	{
		//todo Huidige query werkt met match query. Dit is inefficient.
		//todo Mapping aanpassen, data herindexeren en dan deze query weggooien en gecommente versie weer gebruiken
		SearchResponse response = newSearchRequest().setTypes(SPECIMEN_TYPE).setQuery(matchQuery("unitID", unitID)).execute().actionGet();
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

		return resultSet;
	}
}
