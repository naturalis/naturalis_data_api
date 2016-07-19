package nl.naturalis.nba.dao.es;

public class SpecimenDaoOld extends AbstractDao {
//
//	private final TaxonDao taxonDao;
//
//
//	public SpecimenDao(Client esClient, String ndaIndexName, TaxonDao taxonDao, String baseUrl)
//	{
//		super(esClient, ndaIndexName, baseUrl);
//		this.taxonDao = taxonDao;
//	}
//
//
//	/**
//	 * Retrieves a single Specimen by its unitID.
//	 *
//	 * @param unitID
//	 *            The unitID of the {@link nl.naturalis.nda.domain.Specimen}
//	 * @return {@link nl.naturalis.nda.search.SearchResultSet} containing the
//	 *         {@link nl.naturalis.nda.domain.Specimen}
//	 */
//	public SearchResultSet<Specimen> getSpecimenDetail(String unitID, String sessionId)
//	{
//		SearchResponse response = newSearchRequest().setPreference(sessionId).setTypes(SPECIMEN_TYPE)
//				.setQuery(filteredQuery(matchAllQuery(), termFilter(UNIT_ID + ".raw", unitID))).execute().actionGet();
//
//		SearchResultSet<Specimen> resultSet = new SearchResultSet<>();
//
//		if (response.getHits().getHits().length != 0) {
//			SearchHit hit = response.getHits().getHits()[0];
//			Specimen specimen = null;
//			if (hit != null) {
//				ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
//				specimen = SpecimenTransfer.transfer(esSpecimen);
//				resultSet.addSearchResult(specimen);
//			}
//			resultSet.setTotalSize(response.getHits().getTotalHits());
//			QueryParams queryParams = new QueryParams();
//			queryParams.put(UNIT_ID, Collections.singletonList(unitID));
//			resultSet.setQueryParameters(queryParams);
//
//			List<Link> links = new ArrayList<>();
//			if (specimen != null && specimen.getIdentifications() != null) {
//				for (SpecimenIdentification specimenIdentification : specimen.getIdentifications()) {
//					ScientificName scientificName = specimenIdentification.getScientificName();
//					SearchResultSet<Taxon> taxonSearchResultSet = taxonDao.lookupTaxonForScientificName(scientificName, sessionId);
//					if (taxonSearchResultSet != null) {
//						List<SearchResult<Taxon>> searchResults = taxonSearchResultSet.getSearchResults();
//						if (searchResults != null) {
//							for (SearchResult<Taxon> searchResult : searchResults) {
//								links.add(new Link("_taxon", TAXON_DETAIL_BASE_URL
//										+ createAcceptedNameParams(searchResult.getResult().getAcceptedName())));
//							}
//						}
//					}
//				}
//			}
//			resultSet.setLinks(links);
//			return resultSet;
//		}
//		return null;
//	}
//
//
//	/**
//	 * Check if there is a specimen with the provided UnitID.
//	 * 
//	 * @param unitID
//	 * @return
//	 */
//	public boolean exists(String unitID)
//	{
//		SearchResponse response = newSearchRequest().setTypes(SPECIMEN_TYPE)
//				.setQuery(filteredQuery(matchAllQuery(), termFilter(UNIT_ID + ".raw", unitID))).execute().actionGet();
//		return response.getHits().getHits().length != 0;
//	}
//
//
//	/**
//	 * Get the plain {@code Specimen} object corresponding to the specified
//	 * UnitID.
//	 * 
//	 * @param unitID
//	 * @return
//	 */
//	public Specimen find(String unitID)
//	{
//		SearchResponse response = newSearchRequest().setTypes(SPECIMEN_TYPE)
//				.setQuery(filteredQuery(matchAllQuery(), termFilter(UNIT_ID + ".raw", unitID))).execute().actionGet();
//		if (response.getHits().getHits().length == 0) {
//			return null;
//		}
//		SearchHit hit = response.getHits().getHits()[0];
//		ESSpecimen esSpecimen = getObjectMapper().convertValue(hit.getSource(), ESSpecimen.class);
//		return SpecimenTransfer.transfer(esSpecimen);
//	}
//
//
//	/**
//	 * Get all multimedia for the specified specimen UnitID.
//	 * 
//	 * @param unitID
//	 * @return
//	 */
//	public MultiMediaObject[] getMultiMedia(String unitID)
//	{
//		TermFilterBuilder filter = termFilter("associatedSpecimenReference.raw", unitID);
//		FilteredQueryBuilder query = filteredQuery(matchAllQuery(), filter);
//		SearchRequestBuilder request = newSearchRequest().setTypes(MULTI_MEDIA_OBJECT_TYPE).setQuery(query);
//		SearchResponse response = request.execute().actionGet();
//		SearchHit[] results = response.getHits().getHits();
//		MultiMediaObject[] multimedia = new MultiMediaObject[results.length];
//		for (int i = 0; i < results.length; ++i) {
//			ESMultiMediaObject tmp = getObjectMapper().convertValue(results[i].getSource(), ESMultiMediaObject.class);
//			multimedia[i] = MultiMediaObjectTransfer.transfer(tmp);
//		}
//		return multimedia;
//	}
//
}
