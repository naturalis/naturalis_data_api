package nl.naturalis.nba.dao.format.calc;

import static org.junit.Assert.assertEquals;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
import nl.naturalis.nba.dao.format.EntityObject;

public class SynonymIdCalculatorTest {

  private Taxon taxon;
  private SourceSystem sourceSystem;
  private ScientificName acceptedName;
  private DefaultClassification defaultClassification;
  private List<Monomial> systemClassification = new ArrayList<>();
  private List<Reference> references = new ArrayList<>();
  private List<ScientificName> synonyms = new ArrayList<>();
  
  
  @Before
  public void init() throws URISyntaxException
  {
    /*
     * nsr data:
     * <nsr_id>11E0A704FE3</nsr_id>                                                                                                               
     * <nsr_id_parent>E0CCED34D8E</nsr_id_parent>
     * <url>http://nederlandsesoorten.nl/nsr/concept/011E0A704FE3</url>
     *   
     * nba-test: http://145.136.242.167:8080/v2/taxon/find/11E0A704FE3@NSR
     */
    String nsr_id = "11E0A704FE3";
    String nsr_parent_id = "E0CCED34D8E";
    String nsr_url = "http://nederlandsesoorten.nl/nsr/concept/011E0A704FE3";
    
    taxon = new Taxon();
    sourceSystem = SourceSystem.NSR;
    taxon.setSourceSystem(sourceSystem);
    taxon.setSourceSystemId(nsr_id);
    taxon.setRecordURI(new URI(nsr_url));
    taxon.setId(nsr_id + "@" + SourceSystem.NSR.getCode());
    taxon.setSourceSystemParentId(nsr_parent_id);
    taxon.setTaxonRank("species");
    taxon.setOccurrenceStatusVerbatim("2d Exoot. Incidentele import, geen voortplanting.");

    acceptedName = new ScientificName();
    acceptedName.setFullScientificName("Sagra femorata (Drury, 1773)");
    acceptedName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
    acceptedName.setGenusOrMonomial("Sagra");
    acceptedName.setSpecificEpithet("femorata");
    acceptedName.setAuthorshipVerbatim("(Drury, 1773)");
    acceptedName.setAuthor("Drury");
    acceptedName.setYear("1773");
    acceptedName.setScientificNameGroup("sagra femorata");
    Reference reference = new Reference();
    reference.setTitleCitation("Lijst van niet-inheemse soorten");
    reference.setPublicationDate(OffsetDateTime.parse("2010-01-01T00:00:00+00:00"));
    references.add(reference);
    acceptedName.setReferences(references);
    taxon.setAcceptedName(acceptedName);
    
    defaultClassification = new DefaultClassification();
    defaultClassification.setKingdom("Animalia");
    defaultClassification.setPhylum("Arthropoda");
    defaultClassification.setClassName("Insecta");
    defaultClassification.setOrder("Coleoptera");
    defaultClassification.setFamily("Chrysomelidae");
    defaultClassification.setGenus("Sagra");
    taxon.setDefaultClassification(defaultClassification);

    systemClassification.add(new Monomial("regnum", "Animalia"));
    systemClassification.add(new Monomial("phylum", "Arthropoda"));
    systemClassification.add(new Monomial("subphylum", "Hexapoda"));
    systemClassification.add(new Monomial("classis", "Insecta"));
    systemClassification.add(new Monomial("ordo", "Coleoptera"));
    systemClassification.add(new Monomial("familia", "Chrysomelidae"));
    systemClassification.add(new Monomial("subfamilia", "Sagrinae"));
    systemClassification.add(new Monomial("genus", "Sagra"));
    taxon.setSystemClassification(systemClassification);
    
    ScientificName synonym = new ScientificName();
    synonym.setFullScientificName("Sagra purpurea (Horn)");
    synonym.setTaxonomicStatus(TaxonomicStatus.SYNONYM);
    synonym.setGenusOrMonomial("Sagra");
    synonym.setSpecificEpithet("purpurea");
    synonym.setAuthorshipVerbatim("(Horn)");
    synonym.setAuthor("Horn");
    synonym.setScientificNameGroup("sagra purpurea");
    synonyms.add(synonym);
    taxon.setSynonyms(synonyms);
  }

  @Ignore
  @Test
  public void testCalculateValue() throws CalculationException
  {
    DocumentFlattener df = new DocumentFlattener(new Path("synonyms"), 1);
    List<EntityObject> synonyms = df.flatten(taxon);
    EntityObject synonym = synonyms.get(0);

    SynonymIdCalculator calculator = new SynonymIdCalculator();
    long expected = 0xFFFFFE18FF84458AL;  // Calculated
    long gbif = 0xFFFFFE18EF453AB3L;      // Gbif "FFFFFE18EF453AB3"
    assertEquals("01", Long.toHexString(expected).toUpperCase(), calculator.calculateValue(synonym));
    
  }

}
