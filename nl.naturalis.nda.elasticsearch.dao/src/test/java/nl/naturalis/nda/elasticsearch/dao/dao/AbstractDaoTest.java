package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.elasticsearch.dao.util.FieldMapping;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for {@link nl.naturalis.nda.elasticsearch.dao.dao.AbstractDao}.
 *
 * @author Roberto van der Linden
 */
public class AbstractDaoTest {

    private AbstractDao abstractDao;

    @Before
    public void setUp() throws Exception {
        abstractDao = new AbstractDao(null, "fakeIndex", "http://test.nl/v0/") {
        };
    }

    @Test
    public void testFilterAllowedFieldMappings() throws Exception {
        List<FieldMapping> fields = new ArrayList<>();
        fields.add(new FieldMapping("field1", 0f, "value1", "", false, false));
        fields.add(new FieldMapping("field2", 0f, "value2", "", false, false));
        fields.add(new FieldMapping("field3", 0f, "value3", "", false, false));

        Set<String> allowedFields = new HashSet<>();
        allowedFields.add("field1");
        allowedFields.add("field3");

        List<FieldMapping> fieldMappings = abstractDao.filterAllowedFieldMappings(fields, allowedFields);

        assertEquals(2, fieldMappings.size());
        assertEquals("field1", fieldMappings.get(0).getFieldName());
        assertEquals("field3", fieldMappings.get(1).getFieldName());
    }
}