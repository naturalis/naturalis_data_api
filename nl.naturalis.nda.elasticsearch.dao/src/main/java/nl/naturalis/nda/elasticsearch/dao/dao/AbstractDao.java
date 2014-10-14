package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.elasticsearch.dao.util.SearchParamFieldMapping;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.AND;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.OR;
import static org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator.valueOf;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

/**
 * Abstract base class for all ElasticSearch data access objects.
 *
 * @author ayco_holleman
 */
public abstract class AbstractDao {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

    /**
     * ES Config
     */
    protected static final String ES_HOST = "localhost";
    protected static final int ES_PORT = 9300;
    protected static final String CLUSTER_NAME_PROPERTY = "cluster.name";
    protected static final String CLUSTER_NAME_PROPERTY_VALUE = "naturalis-roberto";
    //todo Aparte index maken voor specimen, taxon en multimedia. Deze property wijzigen
    protected static final String SPECIMEN_INDEX_NAME = "nda";
    //todo Type is na bovenstaande todo wijziginge niet meer nodig
    protected static final String SPECIMEN_TYPE = "Specimen";
    protected static final String TAXON_TYPE = "Taxon";

    private static ObjectMapper objectMapper;
    private SearchParamFieldMapping searchParamFieldMapping;
    protected Client esClient;
    private String ndaIndexName;

    public AbstractDao(Client esClient, String ndaIndexName) {
        this.esClient = esClient;
        this.ndaIndexName = ndaIndexName;
        this.searchParamFieldMapping = SearchParamFieldMapping.getInstance();
    }


    protected static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }


    protected SearchRequestBuilder newSearchRequest() {
        return esClient.prepareSearch(ndaIndexName);
    }

    protected SearchParamFieldMapping getSearchParamFieldMapping() {
        return searchParamFieldMapping;
    }

    protected SearchResponse executeExtendedSearch(QueryParams params, List<FieldMapping> fields, String type) {
        String sortField = getScoreFieldFromQueryParams(params);
        FieldSortBuilder fieldSort = fieldSort(sortField);

        BoolQueryBuilder boolQueryBuilder = boolQuery();
        Operator operator = getOperator(params);

        Map<String, List<FieldMapping>> nestedFields = new HashMap<>();
        List<FieldMapping> nonNestedFields = new ArrayList<>();
        for (FieldMapping field : fields) {
            String nestedPath = field.getNestedPath();
            if (nestedPath != null && nestedPath.trim().length() > 0) {
                List<FieldMapping> fieldMappings = new ArrayList<>();
                if (nestedFields.containsKey(nestedPath)){
                    fieldMappings = nestedFields.get(nestedPath);
                }

                fieldMappings.add(field);
                nestedFields.put(nestedPath, fieldMappings);
            } else {
                nonNestedFields.add(field);
            }
        }

        for (String nestedPath : nestedFields.keySet()) {
            extendQueryWithNestedFieldsWithSameNestedPath(boolQueryBuilder, operator, nestedPath, nestedFields.get(nestedPath));
        }

        for (FieldMapping field : nonNestedFields) {
            extendQueryWithField(boolQueryBuilder, operator, field);
        }

        //TODO Geopoint afhandelen

        SearchRequestBuilder searchRequestBuilder = newSearchRequest().setTypes(type)
                .setQuery(filteredQuery(boolQueryBuilder, null))
                .addSort(fieldSort);

        logger.info(searchRequestBuilder.toString());

        return searchRequestBuilder
                .execute().actionGet();
    }

    /**
     * Checks if the given fields are allowed based on the provided list of allowed fields.
     *
     * @param fields the field to check
     * @param allowedFields list of allowed fields
     * @return a new list with the allowed fields
     */
    protected List<FieldMapping> filterAllowedFieldMappings(List<FieldMapping> fields, List<String> allowedFields){
        List<FieldMapping> approvedFields = new ArrayList<>();
        for (FieldMapping field : fields) {
            if (allowedFields.contains(field.getFieldName())) {
                approvedFields.add(field);
            }
        }

        return approvedFields;
    }

    //================================================ Helper methods ==================================================

    private void extendQueryWithNestedFieldsWithSameNestedPath(BoolQueryBuilder boolQueryBuilder, Operator operator, String nestedPath, List<FieldMapping> fields) {
        BoolQueryBuilder nestedBoolQueryBuilder = boolQuery();
        for (FieldMapping field : fields) {
            extendQueryWithField (nestedBoolQueryBuilder, operator, field);
        }

        NestedQueryBuilder nestedQueryBuilder = nestedQuery(nestedPath, nestedBoolQueryBuilder);
        if (operator == AND) {
            boolQueryBuilder.must(nestedQueryBuilder);
        } else {
            boolQueryBuilder.should(nestedQueryBuilder);
        }
    }

    private void extendQueryWithField(BoolQueryBuilder boolQueryBuilder, Operator operator, FieldMapping field) {
        Float boostValue = field.getBoostValue();
        MatchQueryBuilder matchQueryBuilder = matchQuery(field.getFieldName(), field.getValue());
        if (boostValue != null) {
            matchQueryBuilder.boost(boostValue);
        }

        if (operator == AND) {
            boolQueryBuilder.must(matchQueryBuilder);
        } else {
            boolQueryBuilder.should(matchQueryBuilder);
        }
    }

    /**
     * Get the operator from the query params.
     *
     * @param params the query params
     * @return the operator from the params, if not found {@link Operator#OR} is returned
     */
    private Operator getOperator(QueryParams params) {
        String operatorValue = params.getParam("_andOr");
        Operator operator = OR;
        if (operatorValue != null && !operatorValue.isEmpty()) {
            operator = valueOf(operatorValue);
        }
        return operator;
    }

    protected String getScoreFieldFromQueryParams(QueryParams params) {
        List<String> sortParam = params.get("_score");
        String sortField = "_score";
        if (sortParam != null && !sortParam.isEmpty()) {
            String sort = sortParam.get(0);
            if (sort == null || sort.trim().equalsIgnoreCase("")) {
                sortField = "_score";
            }
        }
        return sortField;
    }
}
