package nl.naturalis.nda.elasticsearch.dao.dao;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.MultiMediaContentIdentification;
import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.Person;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.domain.VernacularName;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import org.junit.Before;

import java.io.IOException;
import java.util.Collections;

import static nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDaoTest.createTestTaxon;

/**
 * @author Quinten Krijger
 */
public class BioportalMultiMediaObjectTest extends DaoIntegrationTest {

    private final String genus_tricholomopsis = "Tricholomopsis";
    private final String epithet_rutilans = "rutilans";
    private BioportalTaxonDao taxonDao;
    private BioportalMultiMediaObjectDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createIndex(INDEX_NAME);
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(MULTI_MEDIA_OBJECT_INDEX_TYPE)
                .setSource(getMapping("test-multimedia-mapping.json"))
                .execute().actionGet();
        taxonDao = new BioportalTaxonDao(client(), INDEX_NAME);
        dao = new BioportalMultiMediaObjectDao(client(), INDEX_NAME, taxonDao);

        client().prepareIndex(INDEX_NAME, MULTI_MEDIA_OBJECT_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(createTestMultiMediaObject()))
                .setRefresh(true).execute().actionGet();
        client().prepareIndex(INDEX_NAME, MULTI_MEDIA_OBJECT_INDEX_TYPE, "2")
                .setSource(objectMapper.writeValueAsString(new ESMultiMediaObject()))
                .setRefresh(true).execute().actionGet();
    }

    public void testMultiMediaObjectSearch() {
        // WHEN
        QueryParams params = new QueryParams();
        params.add("class", "Basidiomycetes");

        SearchResultSet<MultiMediaObject> searchResultSet = dao.multiMediaObjectSearch(params);

        // THEN
        assertEquals(1, searchResultSet.getTotalSize());
    }

    public void testMultiMediaObjectSearch_nameResolution() throws IOException {
        // GIVEN
        setupTaxonForNameResolution();

        // WHEN
        QueryParams params = new QueryParams();
        params.add("_search", "genus_synoniem");

        SearchResultSet<MultiMediaObject> searchResultSet = dao.multiMediaObjectSearch(params);

        // THEN
        assertEquals(1, searchResultSet.getTotalSize());
    }



    private void setupTaxonForNameResolution() throws IOException {
        client().admin().indices().preparePutMapping(INDEX_NAME).setType(TAXON_INDEX_TYPE)
                .setSource(getMapping("test-taxon-mapping.json"))
                .execute().actionGet();

        ScientificName scientificName = new ScientificName();
        scientificName.setGenusOrMonomial(genus_tricholomopsis);
        scientificName.setSpecificEpithet(epithet_rutilans);

        ESTaxon esTaxon = createTestTaxon();
        esTaxon.setAcceptedName(scientificName);
        esTaxon.getSynonyms().get(0).setGenusOrMonomial("genus_synoniem");
        esTaxon.getSynonyms().get(0).setSpecificEpithet("epithet_synoniem");
        client().prepareIndex(INDEX_NAME, TAXON_INDEX_TYPE, "1")
                .setSource(objectMapper.writeValueAsString(esTaxon)).setRefresh(true).execute().actionGet();

    }

    public ESMultiMediaObject createTestMultiMediaObject() {
        ESMultiMediaObject multiMedia = new ESMultiMediaObject();

        SourceSystem sourceSystem = new SourceSystem(); {
            sourceSystem.setCode("NSR");
            sourceSystem.setName("Nationaal Soortenregister");
            multiMedia.setSourceSystem(sourceSystem);
        }
        multiMedia.setSourceSystemId("486805389:1220512581");

        // accessPoint will probably break ...
//        ServiceAccessPoint serviceAccessPoint = new ServiceAccessPoint();
//        serviceAccessPoint.setAccessUri(new URI("file://http://images.naturalis.nl/original/174477.jpg"));
        /* "serviceAccessPoints": [
                  {
                     "accessUri": "file://http://images.naturalis.nl/original/174477.jpg",
                     "format": null,
                     "variant": "MEDIUM_QUALITY"
                  }
               ], */

        MultiMediaContentIdentification multiMediaContentIdentification = new MultiMediaContentIdentification(); {

            multiMediaContentIdentification.setTaxonRank("species");

            VernacularName vernacularName = new VernacularName(); {
                vernacularName.setName("Koningsmantel");
                vernacularName.setLanguage("Dutch");
                vernacularName.setPreferred(Boolean.TRUE);
                Expert expert = new Expert(); {
                    expert.setFullName("Stalpers, J.");
                }
                vernacularName.setExperts(Collections.singletonList(expert));
            }
            multiMediaContentIdentification.setVernacularNames(Collections.singletonList(vernacularName));

            ScientificName scientificName = new ScientificName(); {
                scientificName.setTaxonomicStatus(ScientificName.TaxonomicStatus.ACCEPTED_NAME);
                scientificName.setGenusOrMonomial(genus_tricholomopsis);
                scientificName.setSpecificEpithet(epithet_rutilans);
                scientificName.setAuthor("(Schaeff.:Fr.) Singer");
                Reference reference = new Reference(); {
                    reference.setTitleCitation("Overzicht van de paddestoelen in Nederland");
                    Person person = new Person(); {
                        person.setFullName("Arnolds, E., Kuyper, Th.W. & M.E. Noordeloos.");
                    }
                    reference.setAuthor(person);
                }
                scientificName.setReferences(Collections.singletonList(reference));
                scientificName.setFullScientificName("Tricholomopsis rutilans (Schaeff.:Fr.) Singer");
            }
            multiMediaContentIdentification.setScientificName(scientificName);

            DefaultClassification defaultClassification = new DefaultClassification(); {
                defaultClassification.setKingdom("Fungi");
                defaultClassification.setPhylum("Basidiomycota");
                defaultClassification.setClassName("Basidiomycetes");
                defaultClassification.setOrder("Agaricales");
                defaultClassification.setFamily("Tricholomataceae");
                defaultClassification.setGenus("Tricholomopsis");
            }
            multiMediaContentIdentification.setDefaultClassification(defaultClassification);
        }
        multiMedia.setIdentifications(Collections.singletonList(multiMediaContentIdentification));

        return multiMedia;
    }

}
