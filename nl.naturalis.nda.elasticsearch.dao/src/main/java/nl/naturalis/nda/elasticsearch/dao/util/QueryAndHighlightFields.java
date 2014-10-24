package nl.naturalis.nda.elasticsearch.dao.util;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinten Krijger
 */
public class QueryAndHighlightFields {

    private final List<HighlightBuilder.Field> highlightFields = new ArrayList<>();
    private QueryBuilder query;

    public void addHighlightField(HighlightBuilder.Field field) {
        highlightFields.add(field);
    }

    public List<HighlightBuilder.Field> getHighlightFields() {
        return highlightFields;
    }

    public QueryBuilder getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder query) {
        this.query = query;
    }
}
