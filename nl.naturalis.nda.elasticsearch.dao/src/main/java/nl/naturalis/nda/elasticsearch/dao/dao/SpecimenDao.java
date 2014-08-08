package nl.naturalis.nda.elasticsearch.dao.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.naturalis.nda.domain.Occurrence;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsDetermination;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESCrsSpecimen;
import nl.naturalis.nda.elasticsearch.dao.exception.InvalidQueryException;
import nl.naturalis.nda.elasticsearch.dao.transfer.SpecimenTransfer;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.search.OccurrenceSearchResultSet;

import org.domainobject.util.debug.BeanPrinter;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class SpecimenDao extends AbstractDao {

	public static void main(String[] args)
	{
		SpecimenDao dao = new SpecimenDao("nda");
		QueryParams params = new QueryParams();
		//params.add("offset", "10");
		params.add("sex", "female");
		//params.add("sourceSystemId", "6809111");
		//params.add("specimenId", "50004");
		//params.add("specimenId", "RMNH.MAM.51251");
		//params.add("locality", "San Paolo");
		//params.add("country", "Suriname");
		OccurrenceSearchResultSet ssr = dao.listSpecimens(params);
		BeanPrinter.out(ssr);
	}

	public SpecimenDao(String ndaIndexName)
	{
		super(ndaIndexName);
	}


	public SpecimenDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}


	public OccurrenceSearchResultSet listSpecimens(QueryParams properties)
	{
		return null;
//		SearchRequestBuilder srb = newSearchRequest();
//		srb.setTypes("CrsSpecimen");
//		srb.setSize(5);
//		MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
//		AndFilterBuilder andFilter = FilterBuilders.andFilter();
//		for (String[] kv : properties.keyValuePairs()) {
//			if (kv[0].equals("offset")) {
//				try {
//					srb.setFrom(Integer.parseInt(kv[1]));
//				}
//				catch (NumberFormatException e) {
//					throw new InvalidQueryException("Parameter \"offset\" must be an integer");
//				}
//			}
//			else {
//				andFilter.add(FilterBuilders.termFilter(kv[0], kv[1]));
//			}
//		}
//		FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(matchAllQuery, andFilter);
//		srb.setQuery(filteredQuery);
//		SearchResponse response = srb.execute().actionGet();
//		OccurrenceSearchResultSet result = new OccurrenceSearchResultSet();
//		result.setTotalSize(response.getHits().getTotalHits());
//		Iterator<SearchHit> iterator = response.getHits().iterator();
//		while (iterator.hasNext()) {
//			SearchHit hit = iterator.next();
//			Map<String, Object> source = hit.getSource();
//			ESCrsSpecimen crsSpecimen = getObjectMapper().convertValue(source, ESCrsSpecimen.class);
//			// BeanPrinter.out(crsSpecimen);
//			List<ESCrsDetermination> extraDeterminations = null;
//			if(crsSpecimen.getNumDeterminations() > 3) {
//				// TODO: Fetch the remaining CrsDetermination documents (the first 3
//				// are already "denormalized into" the CrsSpecimen type)
//				extraDeterminations = null; /* Fetch */
//			}
//			Occurrence specimen = SpecimenTransfer.transfer(crsSpecimen, extraDeterminations);
//			result.addSpecimen(specimen);
//		}
//		return result;
	}

}
