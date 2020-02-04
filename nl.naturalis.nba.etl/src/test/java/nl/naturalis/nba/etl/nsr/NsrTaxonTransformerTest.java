package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.utils.xml.DOMUtil.getChild;
import static nl.naturalis.nba.utils.xml.DOMUtil.getChildren;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.nsr.model.Name;

import nl.naturalis.nba.etl.nsr.model.NsrTaxon;
import nl.naturalis.nba.etl.nsr.model.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.w3c.dom.Element;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.XMLRecordInfo;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test Class for {@link NsrTaxonTransformer}
 */
public class NsrTaxonTransformerTest {

    URL nsrFileURL;
    File nsrFile;
    ArrayList<NsrTaxon> nsrTaxa;

    /**
     * @throws java.lang.Exception exception
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
        nsrFileURL = AllTests.class.getResource("nsr-export--2020-01-30_1359--05.jsonl");
        nsrFile = new File(nsrFileURL.getFile());
        nsrTaxa = getNsrTaxa(nsrFile);
    }

    @After
    public void tearDown() {}

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#doTransform()}.
     * <p>
     * Test to verify if the doTransform method returns an expected {List<@Taxon>}
     *
     * @throws Exception exception
     */
    @Test
    public void testDoTransform() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        List<Taxon> transformed = null;

        LineNumberReader lnr;
        FileReader fr = new FileReader(nsrFile);
        lnr = new LineNumberReader(fr, 4096);
        String json;
        while ((json = lnr.readLine()) != null) {
            NsrTaxonTransformer taxonTransformer = new NsrTaxonTransformer(etlStatistics);
            transformed = taxonTransformer.transform(json);
        }

        Taxon actual = transformed.get(0);

        String expectedId = "D3KF0JNQ0UA@NSR";
        String expectedSourceSystemId = "D3KF0JNQ0UA";
        String expectedAuthorName = "Pontoppidan";
        String expectedFullScintificName = "Larus argentatus argentatus Pontoppidan, 1763";
        String expectedGenusOrMonomial = "Larus";
        String expectedScientificNameGroup = "larus argentatus argentatus";
        String recordUri = "http://nederlandsesoorten.nl/nsr/concept/0D3KF0JNQ0UA";
        String sourceSystemName = "Naturalis - Dutch Species Register";

        assertEquals(expectedId, actual.getId());
        assertEquals(expectedSourceSystemId, actual.getSourceSystemId());
        assertEquals(expectedAuthorName, actual.getAcceptedName().getAuthor().toString());
        assertEquals(recordUri, actual.getRecordURI().toString());
        assertEquals(expectedFullScintificName, actual.getAcceptedName().getFullScientificName());
        assertEquals(expectedGenusOrMonomial, actual.getAcceptedName().getGenusOrMonomial());
        assertEquals(expectedScientificNameGroup, actual.getAcceptedName().getScientificNameGroup());
        assertEquals(sourceSystemName, actual.getSourceSystem().getName());

    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#invalidRank(String rank)
     *
     * Test to verify if the invalidRank method returns an expected boolean value
     */
    @Test
    public void testInvalidRank() {

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        Boolean rank = null;
        Object obj = ReflectionUtil.call(nsrTaxonTransformer, "invalidRank", new Class[]{String.class}, new Object[]{"superfamilia"});
        rank = (boolean) obj;
        assertTrue("01", rank);

        rank = null;
        obj = ReflectionUtil.call(nsrTaxonTransformer, "invalidRank", new Class[]{String.class}, new Object[]{null});
        rank = (boolean) obj;
        assertTrue("02", rank);

        rank = null;
        obj = ReflectionUtil.call(nsrTaxonTransformer, "invalidRank", new Class[]{String.class}, new Object[]{"species"});
        rank = (boolean) obj;
        assertFalse("03", rank);
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#isVernacularName(String nameType)
     *
     * Test to verify if the isVernacularName method returns an expected boolean value
     */
    @Test
    public void testIsVernacularName() {

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        Boolean isVernacularName = null;
        Object obj = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "isVernacularName", new Class[]{String.class}, new Object[]{"isPreferredNameOf"});
        isVernacularName = (boolean) obj;
        assertTrue("01", isVernacularName);

        isVernacularName = null;
        obj = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "isVernacularName", new Class[]{String.class}, new Object[]{"isPreferredNameOf"});
        isVernacularName = (boolean) obj;
        assertTrue("02", isVernacularName);

        isVernacularName = null;
        obj = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "isVernacularName", new Class[]{String.class}, new Object[]{"slide"});
        isVernacularName = (boolean) obj;
        assertFalse("03", isVernacularName);

        isVernacularName = null;
        obj = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "isVernacularName", new Class[]{String.class}, new Object[]{null});
        isVernacularName = (boolean) obj;
        assertFalse("04", isVernacularName);
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getNameElements()
     * <p>
     * Test to verify if the getElements method returns an expected {List<@Element>} object
     */
    @Test
    public void testGetElements() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        Object returned = null;

        LineNumberReader lnr;
        FileReader fr = new FileReader(nsrFile);
        lnr = new LineNumberReader(fr, 4096);
        String json;
        while ((json = lnr.readLine()) != null) {
            NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
            List<Taxon> transformed = nsrTaxonTransformer.transform(json);
            returned = CommonReflectionUtil.callMethod(null, null, nsrTaxonTransformer, "getNameElements");
        }

        String[] expectedFullNameValues = {"Larus argentatus argentatus Pontoppidan, 1763", "Scandinavische zilvermeeuw"};
        String[] expectedLanguageValues = {"Scientific", "Dutch"};
        int i = 0;
        Name[] names = (Name[]) returned;
        assertEquals("01", 2, names.length);
        for (Name name : names) {
            assertEquals("0" + i + 1, expectedFullNameValues[i], name.getFullname());
            assertEquals("0" + i + 2, expectedLanguageValues[i], name.getLanguage());
            i++;
        }
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#addScientificNames(Taxon taxon).
     * <p>
     * Test to verify if the addScientificNames method returns an expected boolean value
     */
    @Ignore
    @Test
    public void testAddScientificNames() throws Exception {

        // Todo: add test method
        // ..

    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#add(Taxon taxon, ScientificName scientificName)
     *
     * Test to verify if the add method returns an expected boolean value
     */
    @Test
    public void testAdd_01() {

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        Boolean addScientificName = null;

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");
        name.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(name);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");
        taxon.setValidName(name);

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "add", new Class[]{Taxon.class, ScientificName.class}, new Object[]{taxon, name});
        addScientificName = (boolean) returned;
        assertFalse(addScientificName);

        // TODO: revisit this test and/or the method it tests. Not sure if it is fully functional

        // System.out.println(JsonUtil.toPrettyJson(taxon));
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#add(Taxon taxon, ScientificName scientificName)
     *
     * Test to verify if the add method returns an expected boolean value
     */
    @Test
    public void testAdd_02() {

        boolean addScientificName = false;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");
        name.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(null);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "add", new Class[]{Taxon.class, ScientificName.class}, new Object[]{taxon, name});

        addScientificName = (boolean) returned;
        assertTrue(addScientificName);
    }

    /**
     * Test method for <a href="nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)">nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)</a>.
     *
     * Test to verify if the hasTestGenus method returns an expected boolean value
     *
     */
    @Test
    public void testHasTestGenus_01() {

        boolean hastTestGenus = false;

        System.setProperty("nl.naturalis.nba.etl.testGenera", "larus");
        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(name);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");
        taxon.setValidName(name);

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "hasTestGenus", new Class[]{Taxon.class}, new Object[]{taxon});
        hastTestGenus = (boolean) returned;

        assertTrue(hastTestGenus);
    }

    /**
     * Test method for <a href="nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)">nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)</a>.
     *
     * Test to verify if the hasTestGenus method returns an expected boolean value
     *
     */
    @Test
    public void testHasTestGenus_02() {

        boolean hastTestGenus = true;

        System.setProperty("nl.naturalis.nba.etl.testGenera", "quatsch");
        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(name);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");
        taxon.setValidName(name);

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "hasTestGenus", new Class[]{Taxon.class}, new Object[]{taxon});
        hastTestGenus = (boolean) returned;

        assertFalse(hastTestGenus);
    }

    /**
     * Test method for <a href="nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getOccurrenceStatusVerbatim(Element element)">nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getOccurrenceStatusVerbatim(Element element)</a>.
     *
     * Test to verify if the getOccurrenceStatusVerbatim method returns an expected String object
     */
    @Test
    public void testGetOccurrenceStatusVerbatim() {

        String occurrenceStatusVerbatim = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        NsrTaxon nsrTaxon = nsrTaxa.get(0);

        Object returned = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "getOccurrenceStatusVerbatim", new Class[]{Status.class}, new Object[]{nsrTaxon.getStatus()});
        occurrenceStatusVerbatim = (String) returned;

        String expected = "1b Incidenteel/Periodiek. Minder dan 10 jaar achtereen voortplanting en toevallige gasten.";
        assertNotNull("01", occurrenceStatusVerbatim);
        assertEquals("02", expected, occurrenceStatusVerbatim);
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getScientificName(Name name).
     *
     * Test to verify if the getScientificName method returns an expected {@link ScientificName} object
     */
    @Test
    public void testGetScientificName() {

        ScientificName actual = null;
        Object returned = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrTaxon nsrTaxon = nsrTaxa.get(0);
        Name name = nsrTaxon.getNames()[0];
        returned = ReflectionUtil.call(nsrTaxonTransformer, "getScientificName", new Class[]{Name.class}, name);
        actual = (ScientificName) returned;

        String expectedAuthorName = "Pontoppidan";
        String expectedFullScintificName = "Larus argentatus argentatus Pontoppidan, 1763";
        String expectedGenusOrMonomial = "Larus";
        String expectedScientificNameGroup = "larus argentatus argentatus";
        String expectedSpecificEpithet = "argentatus";
        String expectedInfraspecificEpithet = "argentatus";
        String expectedAuthorshipVerbatim = "Pontoppidan, 1763";
        String expectedYear = "1763";

        assertNotNull("01", actual);
        assertEquals("02", expectedAuthorName, actual.getAuthor());
        assertEquals("03", expectedFullScintificName, actual.getFullScientificName());
        assertEquals("04", expectedGenusOrMonomial, actual.getGenusOrMonomial());
        assertEquals("05", expectedScientificNameGroup, actual.getScientificNameGroup());
        assertEquals("06", expectedSpecificEpithet, actual.getSpecificEpithet());
        assertEquals("07", expectedInfraspecificEpithet, actual.getInfraspecificEpithet());
        assertEquals("08", expectedAuthorshipVerbatim, actual.getAuthorshipVerbatim());
        assertEquals("09", expectedYear, actual.getYear());
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getVernacularName(Element element)
     *
     * Test to verify if the getVernacularName method returns an expected {@link VernacularName} object
     */
    @Test
    public void testGetVernacularName() {

        VernacularName actual = null;
        Object returned = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        NsrTaxon nsrTaxon = nsrTaxa.get(0);

        Name vernacularName = null;
        for (Name name : nsrTaxon.getNames()) {
            if (name.getNametype().equals("isPreferredNameOf"))
                vernacularName = name;
        }

        returned = ReflectionUtil.call(nsrTaxonTransformer, "getVernacularName", new Class[]{Name.class}, vernacularName);
        actual = (VernacularName) returned;

        String expectedName = "Scandinavische zilvermeeuw";
        boolean isPreffered = true;
        String expectedLanguage = "Dutch";

        assertNotNull("01", actual);
        assertEquals("02", expectedName, actual.getName());
        assertEquals("03", isPreffered, actual.getPreferred());
        assertEquals("04", expectedLanguage, actual.getLanguage());
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getReferenceDate(Name name)
     *
     * Test to verify if the getReferenceDate method returns an expected {@link OffsetDateTime} object
     */
    @Test
    public void testGetReferenceDate() {

        OffsetDateTime actual = null;
        Object returned = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrTaxon nsrTaxon = nsrTaxa.get(0);
        Name name = nsrTaxon.getNames()[1];
        returned = ReflectionUtil.call(nsrTaxonTransformer, "getReferenceDate", new Class[]{Name.class}, new Object[]{name});
        actual = (OffsetDateTime) returned;

        String expectedDateString = "2020-01-01T00:00Z";
        assertNotNull("01", actual);
        assertEquals("02", expectedDateString, actual.toString());
    }

    /**
     * Test method for nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getTaxonomicStatus(Name name).
     * <p>
     * Test to verify if the getTaxonomicStatus method returns an expected {@link TaxonomicStatus} object
     */
    @Test
    public void testGetTaxonomicStatus() {

        TaxonomicStatus actual = null;
        Object returned = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        Name name = nsrTaxa.get(0).getNames()[0];
        returned = ReflectionUtil.call(nsrTaxonTransformer, "getTaxonomicStatus", new Class[]{Name.class}, new Object[]{name});
        actual = (TaxonomicStatus) returned;

        String expectedDateString = "accepted name";
        assertNotNull("01", actual);
        assertEquals("02", expectedDateString, actual.toString());
    }

    private static ArrayList<NsrTaxon> getNsrTaxa(File nsrFile) throws IOException {

        ArrayList<NsrTaxon> nsrTaxa = new ArrayList<>();
        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        LineNumberReader lnr;
        FileReader fr = new FileReader(nsrFile);
        lnr = new LineNumberReader(fr, 4096);
        String jsonLine;
        NsrTaxon nsrTaxon = null;
        ObjectMapper objectMapper = new ObjectMapper();
        while ((jsonLine = lnr.readLine()) != null) {
            nsrTaxa.add(objectMapper.readValue(jsonLine, NsrTaxon.class));
        }
        return nsrTaxa;
    }

}
