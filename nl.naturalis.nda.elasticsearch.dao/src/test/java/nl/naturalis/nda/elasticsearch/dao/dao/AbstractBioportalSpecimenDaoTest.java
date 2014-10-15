package nl.naturalis.nda.elasticsearch.dao.dao;

import org.junit.Before;

/**
 * @author Quinten Krijger
 */
public class AbstractBioportalSpecimenDaoTest extends DaoIntegrationTest {
    protected static final String SPECIMEN_TYPE = "Specimen";
    protected static final String TAXON_TYPE = "Taxon";
    protected BioportalSpecimenDao dao;
    protected TestDocumentCreator documentCreator;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalSpecimenDao(client(), INDEX_NAME, new BioportalTaxonDao(client(), INDEX_NAME));
        documentCreator = new TestDocumentCreator();
    }
}
