package nl.naturalis.nda.search;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Quinten Krijger
 */
public class QueryParamsTest {

    @Test
    public void testCopyWithoutGeoShape() {
        // GIVEN
        QueryParams queryParams = new QueryParams();
        queryParams.add("key", "val");
        queryParams.add("_geoShape", "data");

        // WHEN
        QueryParams copyWithoutGeoShape = queryParams.copyWithoutGeoShape();

        // THEN
        assertThat(copyWithoutGeoShape.get("key").get(0), is("val"));
        assertFalse(copyWithoutGeoShape.containsKey("_geoShape"));
    }

    @Test
    public void testCopyWithoutGeoShape_emptyQueryParams() {
        // GIVEN
        QueryParams queryParams = new QueryParams();

        // WHEN
        QueryParams copyWithoutGeoShape = queryParams.copyWithoutGeoShape();

        // THEN
        assertTrue(copyWithoutGeoShape.isEmpty());
    }

}
