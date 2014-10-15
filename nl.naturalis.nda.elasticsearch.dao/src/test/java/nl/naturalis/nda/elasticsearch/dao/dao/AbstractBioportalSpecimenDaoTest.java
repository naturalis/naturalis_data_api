package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import org.elasticsearch.common.joda.time.DateTime;
import org.junit.Before;

import static java.util.Arrays.asList;

/**
 * @author Quinten Krijger
 */
public class AbstractBioportalSpecimenDaoTest extends DaoIntegrationTest {

    protected BioportalSpecimenDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new BioportalSpecimenDao(client(), INDEX_NAME, new BioportalTaxonDao(client(), INDEX_NAME));
        documentCreator = new TestDocumentCreator();
    }

    //=================================================== Helpers ======================================================

    protected ESSpecimen createSpecimen() {
        ESSpecimen esSpecimen = new ESSpecimen();

        esSpecimen.setUnitID("L  0191413");
        esSpecimen.setSourceSystemId("L  0191413");

        ESGatheringEvent gatheringEvent = new ESGatheringEvent();
        gatheringEvent.setGatheringPersons(asList(new Person("Meijer, W.")));
        gatheringEvent.setSiteCoordinates(asList(new ESGatheringSiteCoordinates(9.6373151, 55.7958149)));
        gatheringEvent.setDateTimeBegin(new DateTime().withMillis(-299725200000L).toDate());
        gatheringEvent.setDateTimeEnd(new DateTime().withMillis(-299725200000L).toDate());
        esSpecimen.setGatheringEvent(gatheringEvent);

        SpecimenIdentification specimenIdentification = new SpecimenIdentification();

        DefaultClassification defaultClassification = new DefaultClassification();
        defaultClassification.setKingdom("Plantae");
        specimenIdentification.setDefaultClassification(defaultClassification);

        ScientificName scientificName = new ScientificName();
        scientificName.setGenusOrMonomial("Xylopia");
        scientificName.setSpecificEpithet("ferruginea");
        specimenIdentification.setScientificName(scientificName);

        esSpecimen.setIdentifications(asList(specimenIdentification));

        return esSpecimen;
    }

}
