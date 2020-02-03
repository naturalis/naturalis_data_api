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
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.nsr.model.Name;

import nl.naturalis.nba.etl.nsr.model.NsrTaxon;
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
 * Test Class for NsrTaxonTransformer.java
 */
@SuppressWarnings("unchecked")
@Ignore
public class NsrTaxonTransformerTest {

    URL nsrFileURL;
    File nsrFile;

    /**
     * @throws java.lang.Exception exception
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
        nsrFileURL = AllTests.class.getResource("nsr-export--2020-01-30_1359--05.jsonl");
        nsrFile = new File(nsrFileURL.getFile());
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
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#invalidRank(String rank)}.
     * <p>
     * Test to verify if the invalidRank method returns an expected boolean value
     */
    @Test
    public void testInvalidRank() {
        ETLStatistics etlStatistics = new ETLStatistics();
        Boolean rank = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
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
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#isVernacularName(String nameType)}.
     * <p>
     * Test to verify if the isVernacularName method returns an expected boolean value
     *
     * @throws Exception
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

        // ..

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#add(Taxon taxon, ScientificName scientificName)}.
     * <p>
     * Test to verify if the add method returns an expected boolean value
     *
     * @throws Exception
     */
    @Test
    public void testAdd_01() throws Exception {

        Boolean addScientificName = null;

        ETLStatistics etlStatistics = new ETLStatistics();
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        LineNumberReader lnr;
        FileReader fr = new FileReader(nsrFile);
        lnr = new LineNumberReader(fr, 4096);
        String line;
        String extracted = "{}";
        NsrTaxon nsrTaxon = null;
        while ((line = lnr.readLine()) != null) {
            extracted = line;
            ObjectMapper objectMapper = new ObjectMapper();
            nsrTaxon = objectMapper.readValue(extracted, NsrTaxon.class);
        }

        CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
        CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "suppressErrors", false);
        //CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);
        CommonReflectionUtil.setField(NsrTaxonTransformer.class, nsrTaxonTransformer, "nsrTaxon", nsrTaxon);


        TaxonomicStatus status = TaxonomicStatus.ACCEPTED_NAME;

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");
        name.setTaxonomicStatus(status);

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(name);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");
        taxon.setValidName(name);

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "add", new Class[]{Taxon.class, ScientificName.class}, new Object[]{taxon, name});
        addScientificName = (boolean) returned;
        assertFalse(addScientificName);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#add(Taxon taxon, ScientificName scientificName)}.
     * <p>
     * Test to verify if the add method returns an expected boolean value
     *
     * @throws Exception
     */
    @Test
    public void testAdd_02() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        boolean addScientificName = false;
        LineNumberReader lnr;
        FileReader fr = new FileReader(nsrFile);
        lnr = new LineNumberReader(fr, 4096);
        String line;
        String extracted = "{}";
        NsrTaxon nsrTaxon = null;
        while ((line = lnr.readLine()) != null) {
            extracted = line;
            ObjectMapper objectMapper = new ObjectMapper();
            nsrTaxon = objectMapper.readValue(extracted, NsrTaxon.class);
        }

        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
        CommonReflectionUtil.setField(NsrTaxonTransformer.class, nsrTaxonTransformer, "nsrTaxon", nsrTaxon);

        TaxonomicStatus status = TaxonomicStatus.ACCEPTED_NAME;

        ScientificName name = new ScientificName();
        name.setFullScientificName("Larus argentatus argentatus Pontoppidan, 1763");
        name.setScientificNameGroup("larus argentatus argentatus");
        name.setGenusOrMonomial("Larus");
        name.setAuthor("Pontoppidan");
        name.setSpecificEpithet("argentatus");
        name.setInfraspecificEpithet("argentatus");
        name.setTaxonomicStatus(status);

        Taxon taxon = new Taxon();
        taxon.setAcceptedName(null);
        taxon.setSourceSystemId("D3KF0JNQ0UA");
        taxon.setId("D3KF0JNQ0UA@NSR");

        Object returned = ReflectionUtil.call(nsrTaxonTransformer, "add", new Class[]{Taxon.class, ScientificName.class}, new Object[]{taxon, name});

        addScientificName = (boolean) returned;

        assertTrue(addScientificName);

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)}.
     * <p>
     * Test to verify if the hasTestGenus method returns an expected boolean value
     *
     * @throws Exception
     */
    @Test
    public void testHasTestGenus_01() throws Exception {

        System.setProperty("nl.naturalis.nba.etl.testGenera", "larus");
        ETLStatistics etlStatistics = new ETLStatistics();
        boolean hastTestGenus = false;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

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

        }

        assertTrue(hastTestGenus);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#hasTestGenus(Taxon taxon)}.
     * <p>
     * Test to verify if the hasTestGenus method returns an expected boolean value
     *
     * @throws Exception
     */
    @Test
    public void testHasTestGenus_02() throws Exception {

        System.setProperty("nl.naturalis.nba.etl.testGenera", "quatsch");
        ETLStatistics etlStatistics = new ETLStatistics();
        boolean hastTestGenus = true;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);
        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

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
        }

        assertFalse(hastTestGenus);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getOccurrenceStatusVerbatim(Element element)}.
     * <p>
     * Test to verify if the getOccurrenceStatusVerbatim method returns an expected String object
     *
     * @throws Exception
     */
    @Test
    public void testGetOccurrenceStatusVerbatim() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        String occurrenceStatusVerbatim = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

            Element elemet = extracted.getRecord();

            Object returned = ReflectionUtil.callStatic(NsrTaxonTransformer.class, "getOccurrenceStatusVerbatim", new Class[]{Element.class}, new Object[]{elemet});

            occurrenceStatusVerbatim = (String) returned;

        }
        String expected = "1b Incidenteel/Periodiek. Minder dan 10 jaar achtereen voortplanting en toevallige gasten.";
        assertNotNull(occurrenceStatusVerbatim);
        assertEquals(expected, occurrenceStatusVerbatim);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getScientificName(Element element)}.
     * <p>
     * Test to verify if the getScientificName method returns an expected {@link ScientificName} object
     *
     * @throws Exception
     */
    @Test
    public void testGetScientificName() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        ScientificName actual = null;
        Object returned = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

            Element namesElem = getChild(extracted.getRecord(), "names");

            List<Element> nameElems = getChildren(namesElem);

            returned = ReflectionUtil.call(nsrTaxonTransformer, "getScientificName", new Class[]{Element.class}, new Object[]{nameElems.get(1)});
            actual = (ScientificName) returned;

        }
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
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getVernacularName(Element element)}.
     * <p>
     * Test to verify if the getVernacularName method returns an expected {@link VernacularName} object
     *
     * @throws Exception
     */
    @Test
    public void testGetVernacularName() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        VernacularName actual = null;
        Object returned = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

            Element namesElem = getChild(extracted.getRecord(), "names");

            List<Element> nameElems = getChildren(namesElem);

            returned = ReflectionUtil.call(nsrTaxonTransformer, "getVernacularName", new Class[]{Element.class}, new Object[]{nameElems.get(0)});
            actual = (VernacularName) returned;

        }
        String expectedName = "Scandinavische zilvermeeuw";
        boolean isPreffered = true;
        String expectedLanguage = "Dutch";

        assertNotNull("01", actual);
        assertEquals("02", expectedName, actual.getName());
        assertEquals("03", isPreffered, actual.getPreferred());
        assertEquals("04", expectedLanguage, actual.getLanguage());

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getReferenceDate(Element element)}.
     * <p>
     * Test to verify if the getReferenceDate method returns an expected {@link OffsetDateTime} object
     *
     * @throws Exception
     */

    @Test
    public void testGetReferenceDate() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        OffsetDateTime actual = null;
        Object returned = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

            Element namesElem = getChild(extracted.getRecord(), "names");

            List<Element> nameElems = getChildren(namesElem);

            returned = ReflectionUtil.call(nsrTaxonTransformer, "getReferenceDate", new Class[]{Element.class}, new Object[]{nameElems.get(0)});
            actual = (OffsetDateTime) returned;

        }
        String expectedDateString = "2015-01-01T00:00Z";
        assertNotNull("01", actual);
        assertEquals("02", expectedDateString, actual.toString());

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.nsr.NsrTaxonTransformer#getTaxonomicStatus(Element element)}.
     * <p>
     * Test to verify if the getTaxonomicStatus method returns an expected {@link TaxonomicStatus} object
     *
     * @throws Exception
     */
    @Test
    public void testGetTaxonomicStatus() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        TaxonomicStatus actual = null;
        Object returned = null;
        NsrTaxonTransformer nsrTaxonTransformer = new NsrTaxonTransformer(etlStatistics);

        NsrExtractor extractor = new NsrExtractor(nsrFile, etlStatistics);

        for (XMLRecordInfo extracted : extractor) {

            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "objectID", "D3KF0JNQ0UA");
            CommonReflectionUtil.setField(AbstractTransformer.class, nsrTaxonTransformer, "input", extracted);

            Element namesElem = getChild(extracted.getRecord(), "names");

            List<Element> nameElems = getChildren(namesElem);

            returned = ReflectionUtil.call(nsrTaxonTransformer, "getTaxonomicStatus", new Class[]{Element.class}, new Object[]{nameElems.get(1)});
            actual = (TaxonomicStatus) returned;

        }
        String expectedDateString = "accepted name";
        assertNotNull("01", actual);
        assertEquals("02", expectedDateString, actual.toString());

    }

}
