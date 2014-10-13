package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.elasticsearch.dao.util.SearchParamFieldMapping;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;
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
        SimpleQueryStringBuilder.Operator operator = getOperator(params);

        for (FieldMapping field : fields) {
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

    private void extendQueryWithField(BoolQueryBuilder boolQueryBuilder, SimpleQueryStringBuilder.Operator operator, FieldMapping field) {
        Float boostValue = field.getBoostValue();
        MatchQueryBuilder matchQueryBuilder = matchQuery(field.getFieldName(), field.getValue());
        if (boostValue != null) {
            matchQueryBuilder.boost(boostValue);
        }

        QueryBuilder builder = matchQueryBuilder;
        if (field.isNested()) {
            builder = nestedQuery(field.getNestedPath(), matchQueryBuilder);
        }

        if (operator == AND) {
            boolQueryBuilder.must(builder);
        } else {
            boolQueryBuilder.should(builder);
        }
    }

    /**
     * Get the operator from the query params.
     *
     * @param params the query params
     * @return the operator from the params, if not found {@link org.elasticsearch.index.query.SimpleQueryStringBuilder.Operator#OR} is returned
     */
    private SimpleQueryStringBuilder.Operator getOperator(QueryParams params) {
        String operatorValue = params.getParam("_andOr");
        SimpleQueryStringBuilder.Operator operator = OR;
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
