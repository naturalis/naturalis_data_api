package nl.naturalis.nda.elasticsearch.dao.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nda.elasticsearch.dao.util.SearchParamFieldMapping;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;

/**
 * Abstract base class for all ElasticSearch data access objects.
 *
 * @author ayco_holleman
 */
public abstract class AbstractDao {

    /**
     * ES Config
     */
    //todo Upgrade ES naar versie 1.3.4. Heb nu tijdelijk 1.2.2 gebruikt lokaal om door te kunnen
    protected static final String ES_HOST = "localhost";
    protected static final int ES_PORT = 9300;
    protected static final String CLUSTER_NAME_PROPERTY = "cluster.name";
    protected static final String CLUSTER_NAME_PROPERTY_VALUE = "naturalis-roberto";
    //todo Aparte index maken voor specimen, taxon en multimedia. Deze property wijzigen
    protected static final String SPECIMEN_INDEX_NAME = "nda";
    //todo Type is na bovenstaande todo wijziginge niet meer nodig
    protected static final String SPECIMEN_TYPE = "Specimen";

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
}
