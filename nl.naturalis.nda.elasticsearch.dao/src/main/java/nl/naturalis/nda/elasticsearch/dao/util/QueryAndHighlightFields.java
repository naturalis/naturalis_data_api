package nl.naturalis.nda.elasticsearch.dao.util;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;

/**
 * @author Quinten Krijger
 */
public class QueryAndHighlightFields {

    private final Map<String, HighlightBuilder.Field> highlightFields = new HashMap<>();
    private QueryBuilder query;

    public void addHighlightField(String fieldName, HighlightBuilder.Field field) {
        highlightFields.put(fieldName, field);
    }

    public Map<String, HighlightBuilder.Field> getHighlightFields() {
        return highlightFields;
    }

    public QueryBuilder getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder query) {
        this.query = query;
    }
}
