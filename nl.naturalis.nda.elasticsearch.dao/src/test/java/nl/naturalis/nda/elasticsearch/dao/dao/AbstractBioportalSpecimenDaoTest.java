package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SpecimenIdentification;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import org.elasticsearch.common.joda.time.DateTime;
import org.junit.Before;

import java.util.Arrays;

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
        dao = new BioportalSpecimenDao(client(), INDEX_NAME, new BioportalTaxonDao(client(), INDEX_NAME),
                new TaxonDao(client(), INDEX_NAME));
    }

    //=================================================== Helpers ======================================================

    protected ESSpecimen createSpecimen() {
        ESSpecimen esSpecimen = new ESSpecimen();

        esSpecimen.setUnitID("L  0191413");
        esSpecimen.setSourceSystemId("L  0191413");
        esSpecimen.setAssemblageID("BRAHMS-577339.000000");

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

        VernacularName vernacularName = new VernacularName();
        vernacularName.setName("bosaap");
        specimenIdentification.setVernacularNames(Arrays.asList(vernacularName));

        esSpecimen.setIdentifications(asList(specimenIdentification));

        return esSpecimen;
    }

}
