package nl.naturalis.nba.dao.es;

/**
 * Abstract base class for all ElasticSearch data access objects.
 *
 * @author ayco_holleman
 */
public abstract class AbstractDao {
//
//	private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);
//
//	public static String BASE_URL;
//	public static String TAXON_DETAIL_BASE_URL;
//	public static String TAXON_DETAIL_BASE_URL_IN_RESULT_SET;
//	public static String SPECIMEN_DETAIL_BASE_URL;
//	public static String SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET;
//	public static String MULTIMEDIA_DETAIL_BASE_URL_TAXON;
//	public static String MULTIMEDIA_DETAIL_BASE_URL_SPECIMEN;
//
//	private static ObjectMapper objectMapper;
//	private SearchParamFieldMapping searchParamFieldMapping;
//
//	protected final Client esClient;
//	protected final String ndaIndexName;
//
//	public AbstractDao(Client esClient, String ndaIndexName, String baseUrl)
//	{
//		this.esClient = esClient;
//		this.ndaIndexName = ndaIndexName;
//		this.searchParamFieldMapping = SearchParamFieldMapping.getInstance();
//		BASE_URL = baseUrl;
//		TAXON_DETAIL_BASE_URL = BASE_URL + "/taxon/get-taxon/?";
//		TAXON_DETAIL_BASE_URL_IN_RESULT_SET = BASE_URL + "/taxon/get-taxon-within-result-set/?";
//		SPECIMEN_DETAIL_BASE_URL = BASE_URL + "/specimen/get-specimen/?unitID=";
//		SPECIMEN_DETAIL_BASE_URL_IN_RESULT_SET = BASE_URL
//				+ "/specimen/get-specimen-within-result-set/?unitID=";
//		MULTIMEDIA_DETAIL_BASE_URL_TAXON = BASE_URL
//				+ "/multimedia/get-multimedia-object-for-taxon-within-result-set/?unitID=";
//		MULTIMEDIA_DETAIL_BASE_URL_SPECIMEN = BASE_URL
//				+ "/multimedia/get-multimedia-object-for-specimen-within-result-set/?unitID=";
//	}
//
//	protected static ObjectMapper getObjectMapper()
//	{
//		if (objectMapper == null) {
//			objectMapper = new ObjectMapper();
//		}
//		return objectMapper;
//	}
//
//	public static boolean hasText(String string)
//	{
//		return string != null && !string.trim().isEmpty();
//	}
//
//	private static boolean hasFieldWithTextWithOneOfNames(List<FieldMapping> fields,
//			String... names)
//	{
//		List<String> nameList = Arrays.asList(names);
//		for (FieldMapping field : fields) {
//			if (hasText(field.getFieldName()) && nameList.contains(field.getFieldName())
//					&& hasText(field.getValue())) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	protected SearchRequestBuilder newSearchRequest()
//	{
//		return esClient.prepareSearch(ndaIndexName);
//	}
//
//	protected SearchParamFieldMapping getSearchParamFieldMapping()
//	{
//		return searchParamFieldMapping;
//	}
//
//	protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields,
//			String type, boolean highlighting, String sessionId)
//	{
//		return executeExtendedSearch(params, fields, type, highlighting, null, sessionId);
//	}
//
//	/**
//	 * @param params
//	 * @param fields
//	 * @param type
//	 * @param highlighting
//	 *            whether to use highlighting
//	 * @param prebuiltQuery
//	 *            ignored if null, appended with AND or OR (from _andOr in
//	 *            params) else
//	 * @param sessionId
//	 * @return
//	 */
//	protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields,
//			String type, boolean highlighting, QueryAndHighlightFields prebuiltQuery,
//			String sessionId)
//	{
//
//		BoolQueryBuilder nonPrebuiltQuery = boolQuery();
//		Operator operator = getOperator(params);
//
//		LinkedHashMap<String, List<FieldMapping>> nestedFields = new LinkedHashMap<>();
//		List<FieldMapping> nonNestedFields = new ArrayList<>();
//		for (FieldMapping field : fields) {
//			String nestedPath = field.getNestedPath();
//			if (nestedPath != null && nestedPath.trim().length() > 0) {
//				List<FieldMapping> fieldMappings = new ArrayList<>();
//				if (nestedFields.containsKey(nestedPath)) {
//					fieldMappings = nestedFields.get(nestedPath);
//				}
//
//				fieldMappings.add(field);
//				nestedFields.put(nestedPath, fieldMappings);
//			}
//			else {
//				nonNestedFields.add(field);
//			}
//		}
//
//		boolean atLeastOneFieldToQuery = false;
//
//		Map<String, HighlightBuilder.Field> highlightFields = prebuiltQuery == null
//				|| prebuiltQuery.getHighlightFields() == null
//				|| prebuiltQuery.getHighlightFields().isEmpty() ? new HashMap<String, HighlightBuilder.Field>()
//				: prebuiltQuery.getHighlightFields();
//
//		for (String nestedPath : nestedFields.keySet()) {
//			extendQueryWithNestedFieldsWithSameNestedPath(nonPrebuiltQuery, operator, nestedPath,
//					nestedFields.get(nestedPath), highlightFields, highlighting);
//			atLeastOneFieldToQuery = true;
//		}
//
//		for (FieldMapping field : nonNestedFields) {
//			if (!field.getFieldName().contains("dateTime")) {
//				extendQueryWithField(nonPrebuiltQuery, operator, field, highlightFields,
//						highlighting);
//				atLeastOneFieldToQuery = true;
//			}
//		}
//
//		atLeastOneFieldToQuery = extractRangeQuery(params, nonPrebuiltQuery, atLeastOneFieldToQuery);
//
//		BoolQueryBuilder completeQuery;
//
//		if (prebuiltQuery != null && prebuiltQuery.getQuery() != null) {
//			completeQuery = boolQuery();
//			extendQueryWithQuery(completeQuery, OR, nonPrebuiltQuery);
//			extendQueryWithQuery(completeQuery, OR, prebuiltQuery.getQuery());
//			atLeastOneFieldToQuery = true;
//		}
//		else {
//			completeQuery = nonPrebuiltQuery;
//		}
//
//		NestedFilterBuilder geoShape = null;
//		boolean geoSearch = false;
//		if (params.containsKey("_geoShape")) {
//			geoShape = createGeoShapeFilter(params.getParam("_geoShape"));
//			geoSearch = true;
//		}
//
//		SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(type)
//				.setQuery(filteredQuery(completeQuery, geoShape)).addSort(createFieldSort(params))
//				.setTrackScores(true);
//
//		Integer offSet = getOffSetFromParams(params);
//		if (offSet != null) {
//			searchRequestBuilder.setFrom(offSet);
//		}
//		setSize(params, searchRequestBuilder);
//
//		if (!highlightFields.isEmpty()) {
//			for (HighlightBuilder.Field highlightField : highlightFields.values()) {
//				searchRequestBuilder.addHighlightedField(highlightField);
//			}
//			searchRequestBuilder.setHighlighterPreTags("<span class=\"search_hit\">")
//					.setHighlighterPostTags("</span>");
//		}
//
//		searchRequestBuilder.setPreference(sessionId);
//
//		if (geoSearch && !atLeastOneFieldToQuery) {
//			searchRequestBuilder.setQuery(filteredQuery(matchAllQuery(), geoShape));
//			logger.info(searchRequestBuilder.toString());
//			return searchRequestBuilder.execute().actionGet();
//		}
//
//		if (!atLeastOneFieldToQuery) {
//			return new SearchResponse(InternalSearchResponse.empty(), "", 0, 0, 0, null);
//		}
//
//		logger.info(searchRequestBuilder.toString());
//		return searchRequestBuilder.execute().actionGet();
//	}
//
//	//================================================ Helper methods ==================================================
//
//	private FieldSortBuilder createFieldSort(QueryParams params)
//	{
//		String sortField = getSortFieldFromQueryParams(params);
//		FieldSortBuilder fieldSort = fieldSort(sortField);
//		SortOrder sortOrder = getSortOrderFromQueryParams(params);
//		if (sortOrder != null) {
//			fieldSort.order(sortOrder);
//		}
//		return fieldSort;
//	}
//
//	private boolean extractRangeQuery(QueryParams params, BoolQueryBuilder boolQueryBuilder,
//			boolean atLeastOneFieldToQuery)
//	{
//		if (params.containsKey("gatheringEvent.dateTimeBegin")
//				|| params.containsKey("gatheringEvent.dateTimeEnd")) {
//			extendQueryWithRangeFilter(boolQueryBuilder, params, "gatheringEvent.dateTimeBegin",
//					"gatheringEvent.dateTimeEnd");
//			atLeastOneFieldToQuery = true;
//		}
//
//		if (params.containsKey("gatheringEvents.dateTimeBegin")
//				|| params.containsKey("gatheringEvents.dateTimeEnd")) {
//			extendQueryWithRangeFilter(boolQueryBuilder, params, "gatheringEvents.dateTimeBegin",
//					"gatheringEvents.dateTimeEnd");
//			atLeastOneFieldToQuery = true;
//		}
//
//		return atLeastOneFieldToQuery;
//	}
//
//	private void extendQueryWithRangeFilter(BoolQueryBuilder boolQueryBuilder, QueryParams params,
//			String dateTimeBegin, String dateTimeEnd)
//	{
//		String begin = params.getParam(dateTimeBegin);
//		String end = params.getParam(dateTimeEnd);
//		if (begin != null) {
//			RangeFilterBuilder dateTimeBeginRangeFilterBuilder = rangeFilter(dateTimeBegin).from(
//					begin);
//			boolQueryBuilder.must(filteredQuery(matchAllQuery(), dateTimeBeginRangeFilterBuilder));
//		}
//		if (end != null) {
//			RangeFilterBuilder dateTimeEndRangeFilterBuilder = rangeFilter(dateTimeEnd).to(end);
//			boolQueryBuilder.must(filteredQuery(matchAllQuery(), dateTimeEndRangeFilterBuilder));
//		}
//	}
//
//	private NestedFilterBuilder createGeoShapeFilter(String geoShape)
//	{
//		GeoJsonObject geo;
//		ShapeBuilder shapeBuilder = null;
//		try {
//			geo = getObjectMapper().readValue(geoShape, GeoJsonObject.class);
//			if (geo instanceof MultiPolygon) {
//				List<List<List<LngLatAlt>>> coordinatesMultiPolygon = ((MultiPolygon) geo)
//						.getCoordinates();
//				if (coordinatesMultiPolygon != null) {
//					MultiPolygonBuilder multiPolygonBuilder = newMultiPolygon();
//					for (List<List<LngLatAlt>> lists : coordinatesMultiPolygon) {
//						Coordinate[] polygon = getCoordinatesFromPolygon(lists);
//						BasePolygonBuilder basePolygonBuilder = new PolygonBuilder();
//						basePolygonBuilder.points(polygon);
//						multiPolygonBuilder.polygon(basePolygonBuilder);
//					}
//					shapeBuilder = multiPolygonBuilder;
//				}
//			}
//			else if (geo instanceof Polygon) {
//				List<List<LngLatAlt>> coordinates = ((Polygon) geo).getCoordinates();
//				if (coordinates != null) {
//					Coordinate[] polygon = getCoordinatesFromPolygon(coordinates);
//					shapeBuilder = newPolygon().points(polygon);
//				}
//			}
//		}
//		catch (IOException e) {
//			logger.info(
//					String.format("Could not get coordinates from provided geoShape %s", geoShape),
//					e);
//		}
//
//		if (shapeBuilder != null) {
//			return nestedFilter(
//					"gatheringEvent.siteCoordinates",
//					geoShapeFilter("gatheringEvent.siteCoordinates.point", shapeBuilder,
//							ShapeRelation.WITHIN));
//		}
//		return null;
//	}
//
//	private Coordinate[] getCoordinatesFromPolygon(List<List<LngLatAlt>> coordinatesPolygon)
//	{
//		List<Coordinate> coordinates = new ArrayList<>();
//		for (List<LngLatAlt> lngLatAlts : coordinatesPolygon) {
//			for (LngLatAlt lngLatAlt : lngLatAlts) {
//				double longitude = lngLatAlt.getLongitude();
//				double latitude = lngLatAlt.getLatitude();
//				coordinates.add(new Coordinate(longitude, latitude));
//			}
//		}
//		return coordinates.toArray(new Coordinate[coordinates.size()]);
//	}
//
//	/**
//	 * Checks if the given fields are allowed based on the provided list of
//	 * allowed fields.
//	 *
//	 * @param fields
//	 *            the field to check
//	 * @param allowedFields
//	 *            list of allowed fields
//	 * @return a new list with the allowed fields
//	 */
//	protected List<FieldMapping> filterAllowedFieldMappings(List<FieldMapping> fields,
//			Set<String> allowedFields)
//	{
//		List<FieldMapping> approvedFields = new ArrayList<>();
//		for (FieldMapping field : fields) {
//			if (allowedFields.contains(field.getFieldName())) {
//				approvedFields.add(field);
//			}
//		}
//
//		return approvedFields;
//	}
//
//	private void setSize(QueryParams params, SearchRequestBuilder searchRequestBuilder)
//	{
//		if (params.containsKey("_maxResults")) {
//			String maxResultsAsString = params.getFirst("_maxResults");
//			try {
//				Integer maxResults = Integer.valueOf(maxResultsAsString);
//				searchRequestBuilder.setSize(maxResults);
//			}
//			catch (NumberFormatException e) {
//				logger.debug("Could not parse _maxResults value '" + maxResultsAsString
//						+ "'. Using 50.");
//				searchRequestBuilder.setSize(50);
//			}
//		}
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		if (params.containsKey("_groupMaxResults")) {
//			String groupMaxResultsAsString = params.getFirst("_groupMaxResults");
//			Integer groupMaxResults = 0;
//			try {
//				groupMaxResults = Integer.valueOf(groupMaxResultsAsString);
//				searchRequestBuilder.setSize(groupMaxResults);
//			}
//			catch (NumberFormatException e) {
//				logger.debug("Could not parse _groupMaxResults value '" + groupMaxResultsAsString
//						+ "'. Using 10.");
//				groupMaxResults = 10;
//				searchRequestBuilder.setSize(groupMaxResults);
//			}
//		}
//	}
//
//	private void extendQueryWithNestedFieldsWithSameNestedPath(BoolQueryBuilder boolQueryBuilder,
//			Operator operator, String nestedPath, List<FieldMapping> fields,
//			Map<String, HighlightBuilder.Field> highlightFields, boolean highlight)
//	{
//		BoolQueryBuilder nestedBoolQueryBuilder = boolQuery();
//		for (FieldMapping field : fields) {
//			if (!field.getFieldName().contains("dateTime")) {
//				extendQueryWithField(nestedBoolQueryBuilder, operator, field, highlightFields,
//						highlight);
//			}
//		}
//
//		NestedQueryBuilder nestedQueryBuilder = nestedQuery(nestedPath, nestedBoolQueryBuilder);
//
//		extendQueryWithQuery(boolQueryBuilder, operator, nestedQueryBuilder);
//	}
//
//	private void extendQueryWithField(BoolQueryBuilder boolQueryBuilder, Operator operator,
//			FieldMapping field, Map<String, HighlightBuilder.Field> highlightFields,
//			boolean highlight)
//	{
//		if (field.getValue() != null) {
//
//			if (field.getValue().equals("NOT_NULL")) {
//				ExistsFilterBuilder filter = FilterBuilders.existsFilter(field.getFieldName());
//				FilteredQueryBuilder query = QueryBuilders.filteredQuery(
//						QueryBuilders.matchAllQuery(), filter);
//				Float boostValue = field.getBoostValue();
//				if (boostValue != null) {
//					query.boost(boostValue);
//				}
//				if (operator == AND) {
//					boolQueryBuilder.must(query);
//				}
//				else {
//					boolQueryBuilder.should(query);
//				}
//			}
//
//			else if (field.getValue().equals("NULL")) {
//				MissingFilterBuilder fltr = FilterBuilders.missingFilter(field.getFieldName());
//				FilteredQueryBuilder query = QueryBuilders.filteredQuery(
//						QueryBuilders.matchAllQuery(), fltr);
//				Float boostValue = field.getBoostValue();
//				if (boostValue != null) {
//					query.boost(boostValue);
//				}
//				if (operator == AND) {
//					boolQueryBuilder.must(query);
//				}
//				else {
//					boolQueryBuilder.should(query);
//				}
//			}
//
//			else {
//
//				MatchQueryBuilder fieldMatchQuery = matchQuery(field.getFieldName(),
//						field.getValue());
//				Float boostValue = field.getBoostValue();
//				if (boostValue != null) {
//					fieldMatchQuery.boost(boostValue);
//				}
//
//				if (field.hasNGram() != null && field.hasNGram()) {
//					extendQueryWithQuery(boolQueryBuilder, OR, fieldMatchQuery);
//					MatchQueryBuilder ngramFieldMatchQuery = matchQuery(field.getFieldName()
//							+ ".ngram", field.getValue());
//					if (boostValue != null) {
//						ngramFieldMatchQuery.boost(boostValue);
//					}
//					extendQueryWithQuery(boolQueryBuilder, OR, ngramFieldMatchQuery);
//					if (highlight) {
//						highlightFields.put(
//								field.getFieldName() + ".ngram",
//								createHighlightField(
//										field.getFieldName() + ".ngram",
//										matchQuery(field.getFieldName() + ".ngram",
//												field.getValue())));
//					}
//				}
//				else {
//					extendQueryWithQuery(boolQueryBuilder, operator, fieldMatchQuery);
//				}
//				if (!highlightFields.containsKey(field.getFieldName())) {
//					highlightFields.put(
//							field.getFieldName(),
//							createHighlightField(field.getFieldName(),
//									matchQuery(field.getFieldName(), field.getValue())));
//				}
//
//			}
//
//		}
//	}
//
//	private void extendQueryWithQuery(BoolQueryBuilder boolQueryBuilder, Operator operator,
//			QueryBuilder nameResolutionQuery)
//	{
//		if (operator == AND) {
//			boolQueryBuilder.must(nameResolutionQuery);
//		}
//		else {
//			boolQueryBuilder.should(nameResolutionQuery);
//		}
//	}
//
//	/**
//	 * Get the operator from the query params.
//	 *
//	 * @param params
//	 *            the query params
//	 * @return the operator from the params, if not found {@link Operator#OR} is
//	 *         returned
//	 */
//	protected Operator getOperator(QueryParams params)
//	{
//		String operatorValue = params.getParam("_andOr");
//		Operator operator = AND;
//		if (operatorValue != null && !operatorValue.isEmpty()) {
//			operator = valueOf(operatorValue);
//		}
//		return operator;
//	}
//
//	/**
//	 * 
//	 * @param params
//	 *            getSortFieldFromQuery
//	 * @return
//	 */
//	protected String getSortFieldFromQueryParams(QueryParams params)
//	{
//		String sortParam = params.getParam("_sort");
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		String sortGroupParam = params.getParam("_groupSort");
//		String sortField = "_score";
//		if (sortParam != null && !sortParam.isEmpty() && !sortParam.equalsIgnoreCase("_score")) {
//			sortField = sortParam + ".raw";
//		}
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		if (sortGroupParam != null && !sortGroupParam.isEmpty()
//				&& !sortGroupParam.equalsIgnoreCase("_score")) {
//			sortField = sortGroupParam + ".raw";
//		}
//
//		return sortField;
//	}
//
//	/**
//	 * Get the offSet from the params. If no offSet is provided, null will be
//	 * returned.
//	 *
//	 * @param params
//	 *            the query params
//	 * @return the offSet if available, null otherwise
//	 */
//	private Integer getOffSetFromParams(QueryParams params)
//	{
//		String offSetParam = params.getParam("_offset");
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		String groupoffSetParam = params.getParam("_groupOffset");
//		if (hasText(offSetParam)) {
//			return Integer.parseInt(offSetParam);
//		}
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		if (hasText(groupoffSetParam)) {
//			return Integer.parseInt(groupoffSetParam);
//		}
//
//		return null;
//	}
//
//	/**
//	 * Get the sort order from the params. If no order is provided, null will be
//	 * returned.
//	 *
//	 * @param params
//	 *            the query params
//	 * @return the sort order if available, null otherwise
//	 */
//	private SortOrder getSortOrderFromQueryParams(QueryParams params)
//	{
//		String sortOrderParam = params.getParam("_sortDirection");
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		String sortOrderDirParam = params.getParam("_groupSortDirection");
//		SortOrder sortOrder = null;
//		if (hasText(sortOrderParam)) {
//			if (SortOrder.ASC.name().equals(sortOrderParam)) {
//				sortOrder = SortOrder.ASC;
//			}
//			if (SortOrder.DESC.name().equals(sortOrderParam)) {
//				sortOrder = SortOrder.DESC;
//			}
//		}
//		/* NDA-449 Datum: 08-10-2015 Door: R.Kartowikromo */
//		if (hasText(sortOrderDirParam)) {
//			if (SortOrder.ASC.name().equals(sortOrderDirParam)) {
//				sortOrder = SortOrder.ASC;
//			}
//			if (SortOrder.DESC.name().equals(sortOrderDirParam)) {
//				sortOrder = SortOrder.DESC;
//			}
//		}
//
//		return sortOrder;
//	}
//
//	/**
//	 * @param fields
//	 *            parameters for the query
//	 * @param simpleSearch
//	 * @param taxonDao
//	 *            @return null in case of no valid param_keys or no taxons
//	 *            matching the supplied values
//	 * @param highlight
//	 * @param operator
//	 *            only used in case of extended search
//	 * @param sessionId
//	 */
//	protected QueryAndHighlightFields buildNameResolutionQuery(List<FieldMapping> fields,
//			String simpleSearch, BioportalTaxonDao taxonDao, boolean highlight, Operator operator,
//			String sessionId)
//	{
//		if (!hasFieldWithTextWithOneOfNames(fields, IDENTIFICATIONS_VERNACULAR_NAMES_NAME,
//				IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM,
//				IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM,
//				IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME,
//				IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER,
//				IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY)
//				&& !hasText(simpleSearch)) {
//			return null;
//		}
//
//		// nameRes = name resolution
//		QueryParams nameResTaxonQueryParams = new QueryParams();
//		if (hasText(simpleSearch)) {
//			nameResTaxonQueryParams.add("vernacularNames.name", simpleSearch);
//			nameResTaxonQueryParams.add("synonyms.genusOrMonomial", simpleSearch);
//			nameResTaxonQueryParams.add("synonyms.specificEpithet", simpleSearch);
//			nameResTaxonQueryParams.add("synonyms.infraspecificEpithet", simpleSearch);
//		}
//		for (FieldMapping field : fields) {
//			switch (field.getFieldName()) {
//				case IDENTIFICATIONS_VERNACULAR_NAMES_NAME:
//					nameResTaxonQueryParams.add("vernacularNames.name", field.getValue());
//					break;
//				case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_KINGDOM:
//					nameResTaxonQueryParams.add("defaultClassification.kingdom", field.getValue());
//					break;
//				case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_CLASS_NAME:
//					nameResTaxonQueryParams
//							.add("defaultClassification.className", field.getValue());
//					break;
//				case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_FAMILY:
//					nameResTaxonQueryParams.add("defaultClassification.family", field.getValue());
//					break;
//				case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_ORDER:
//					nameResTaxonQueryParams.add("defaultClassification.order", field.getValue());
//					break;
//				case IDENTIFICATIONS_DEFAULT_CLASSIFICATION_PHYLUM:
//					nameResTaxonQueryParams.add("defaultClassification.phylum", field.getValue());
//					break;
//			}
//		}
//		if (nameResTaxonQueryParams.size() == 0) {
//			return null; // otherwise we would get an all-query
//		}
//		if (hasText(simpleSearch)) {
//			nameResTaxonQueryParams.add("_andOr", "OR");
//		}
//		else {
//			nameResTaxonQueryParams.add("_andOr", operator.name());
//		}
//		nameResTaxonQueryParams.add("_maxResults", "50");
//		SearchResultSet<Taxon> nameResTaxons = taxonDao.searchReturnsResultSet(
//				nameResTaxonQueryParams, null, null, true, sessionId); // no field filtering
//		if (nameResTaxons.getTotalSize() == 0) {
//			return null;
//		}
//
//		QueryAndHighlightFields queryAndHighlightFields = new QueryAndHighlightFields();
//		BoolQueryBuilder nameResQueryBuilder = boolQuery();
//		for (SearchResult<Taxon> taxonSearchResult : nameResTaxons.getSearchResults()) {
//			Taxon taxon = taxonSearchResult.getResult();
//			BoolQueryBuilder scientificNameQuery = boolQuery();
//
//			addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery,
//					highlight, taxon.getValidName().getGenusOrMonomial(),
//					IDENTIFICATIONS_SCIENTIFIC_NAME_GENUS_OR_MONOMIAL);
//			addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery,
//					highlight, taxon.getValidName().getSpecificEpithet(),
//					IDENTIFICATIONS_SCIENTIFIC_NAME_SPECIFIC_EPITHET);
//			addMustQueryWithHighlightSupport(queryAndHighlightFields, scientificNameQuery,
//					highlight, taxon.getValidName().getInfraspecificEpithet(),
//					IDENTIFICATIONS_SCIENTIFIC_NAME_INFRASPECIFIC_EPITHET);
//
//			nameResQueryBuilder.should(scientificNameQuery);
//		}
//		NestedQueryBuilder nestedNameResQuery = nestedQuery("identifications", nameResQueryBuilder);
//		nestedNameResQuery.boost(0.0f); /*
//										 * Changed from 0.5f to 0.0f to see if
//										 * that solves NDA-285
//										 */
//
//		queryAndHighlightFields.setQuery(nestedNameResQuery);
//		return queryAndHighlightFields;
//	}
//
//	private void addMustQueryWithHighlightSupport(QueryAndHighlightFields highlightFieldsContainer,
//			BoolQueryBuilder query, boolean highlight, String fieldValue, String fieldName)
//	{
//		if (fieldValue != null) {
//			MatchQueryBuilder localQuery = matchQuery(fieldName, fieldValue);
//			query.must(localQuery);
//			if (highlight) {
//				highlightFieldsContainer.addHighlightField(fieldName,
//						createHighlightField(fieldName, localQuery));
//			}
//		}
//	}
//
//	private HighlightBuilder.Field createHighlightField(String fieldName,
//			QueryBuilder highlightQuery)
//	{
//		HighlightBuilder.Field field = new HighlightBuilder.Field(fieldName);
//		field.highlightQuery(highlightQuery);
//		return field;
//	}
//
//	/**
//	 * @param params
//	 *            parameters as passed to dao, may or may not include a _search
//	 *            entry
//	 * @param searchFieldNames
//	 *            all fields on which can be search in (extended) search
//	 * @param simpleSearchFieldNameExceptions
//	 *            do no use these in simple search
//	 */
//	protected void evaluateSimpleSearch(QueryParams params, Set<String> searchFieldNames,
//			Set<String> simpleSearchFieldNameExceptions)
//	{
//		String simpleSearchTerm = params.getParam("_search");
//		if (searchFieldNames == null || searchFieldNames.isEmpty()) {
//			return;
//		}
//		if (simpleSearchFieldNameExceptions == null) {
//			simpleSearchFieldNameExceptions = Collections.emptySet();
//		}
//		if (hasText(simpleSearchTerm)) {
//			for (String searchField : searchFieldNames) {
//				if (!simpleSearchFieldNameExceptions.contains(searchField)) {
//					params.add(searchField, simpleSearchTerm);
//				}
//			}
//		}
//	}
//
//	protected void enhanceSearchResultWithMatchInfoAndScore(SearchResult<?> searchResult,
//			SearchHit hit)
//	{
//
//		/*
//		 * NDA-294: Missing scores and percentages Door: Reinier Datum: 22 juli
//		 * 2015 Doel: Controle of the value een cijfer(float) is.
//		 */
//		if (Float.isNaN(hit.getScore())) {
//			searchResult.setScore(Float.valueOf("No score")); // Wordt toch 0
//		}
//		else {
//			searchResult.setScore(hit.getScore());
//		}
//
//		if (hit.getHighlightFields() != null) {
//			LinkedHashMap<String, StringMatchInfo> stringMatchInfos = new LinkedHashMap<>();
//			for (Map.Entry<String, HighlightField> highlightFieldEntry : hit.getHighlightFields()
//					.entrySet()) {
//				StringMatchInfo stringMatchInfo = new StringMatchInfo();
//
//				String fieldName = getFieldName(highlightFieldEntry);
//				stringMatchInfo.setPath(fieldName);
//
//				StringBuilder match = new StringBuilder();
//				for (Text matchText : highlightFieldEntry.getValue().fragments()) {
//					match.append(matchText.string());
//				}
//				stringMatchInfo.setValueHighlighted(match.toString());
//
//				// TODO: setValue
//				stringMatchInfos.put(fieldName, stringMatchInfo);
//			}
//			List<StringMatchInfo> stringMatchInfoList = new ArrayList<>(stringMatchInfos.size());
//			stringMatchInfoList.addAll(stringMatchInfos.values());
//			searchResult.setMatchInfo(stringMatchInfoList);
//		}
//	}
//
//	private String getFieldName(Map.Entry<String, HighlightField> highlightFieldEntry)
//	{
//		String key = highlightFieldEntry.getKey();
//
//		int lastIndexOfNgramSuffix = key.lastIndexOf(SearchParamFieldMapping.NGRAM_SUFFIX);
//		if (lastIndexOfNgramSuffix > 0) {
//			return key.substring(0, lastIndexOfNgramSuffix);
//		}
//		return key;
//	}
//
//	public static String queryParamsToUrl(QueryParams params)
//	{
//		params.remove("_search");
//		String url = "";
//		for (Map.Entry<String, List<String>> parameters : params.entrySet()) {
//			url = url + "&" + parameters.getKey() + "=" + parameters.getValue();
//		}
//		return url;
//	}
//
//	protected String createAcceptedNameParams(ScientificName acceptedName)
//	{
//		StringBuilder stringBuilder = new StringBuilder();
//
//		if (acceptedName != null) {
//			boolean found = false;
//			if (hasText(acceptedName.getGenusOrMonomial())) {
//				found = true;
//				stringBuilder.append("genus=").append(acceptedName.getGenusOrMonomial());
//			}
//			if (hasText(acceptedName.getSubgenus())) {
//				if (found) {
//					stringBuilder.append("&");
//				}
//				stringBuilder.append("subgenus=").append(acceptedName.getSubgenus());
//				found = true;
//			}
//			if (hasText(acceptedName.getSpecificEpithet())) {
//				if (found) {
//					stringBuilder.append("&");
//				}
//				stringBuilder.append("specificEpithet=").append(acceptedName.getSpecificEpithet());
//				found = true;
//			}
//			if (hasText(acceptedName.getInfraspecificEpithet())) {
//				if (found) {
//					stringBuilder.append("&");
//				}
//				stringBuilder.append("infraspecificEpithet=").append(
//						acceptedName.getInfraspecificEpithet());
//			}
//		}
//		return stringBuilder.toString();
//	}
//
}
