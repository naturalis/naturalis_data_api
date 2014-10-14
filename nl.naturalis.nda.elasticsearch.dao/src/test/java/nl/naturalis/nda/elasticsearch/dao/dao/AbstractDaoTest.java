package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link nl.naturalis.nda.elasticsearch.dao.dao.AbstractDao}.
 *
 * @author Roberto van der Linden
 */
public class AbstractDaoTest {

    private AbstractDao abstractDao;

    @Before
    public void setUp() throws Exception {
        abstractDao = new AbstractDao(null, "fakeIndex") {};
    }

    @Test
    public void testFilterAllowedFieldMappings() throws Exception {
        List<FieldMapping> fields = new ArrayList<>();
        fields.add(new FieldMapping("field1", 0f, "value1", ""));
        fields.add(new FieldMapping("field2", 0f, "value2", ""));
        fields.add(new FieldMapping("field3", 0f, "value3", ""));

        List<String> allowedFields = Arrays.asList("field1", "field3");

//        List<FieldMapping> fieldMappings = abstractDao.filterAllowedFieldMappings(fields, allowedFields);

//        assertEquals(2, fieldMappings.size());
//        assertEquals("field1", fieldMappings.get(0).getFieldName());
//        assertEquals("field3", fieldMappings.get(1).getFieldName());
    }
}