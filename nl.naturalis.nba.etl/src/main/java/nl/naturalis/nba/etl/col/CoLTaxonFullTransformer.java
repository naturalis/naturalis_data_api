package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.api.model.TaxonomicRank.CLASS;
import static nl.naturalis.nba.api.model.TaxonomicRank.FAMILY;
import static nl.naturalis.nba.api.model.TaxonomicRank.GENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.KINGDOM;
import static nl.naturalis.nba.api.model.TaxonomicRank.ORDER;
import static nl.naturalis.nba.api.model.TaxonomicRank.PHYLUM;
import static nl.naturalis.nba.api.model.TaxonomicRank.SPECIES;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBGENUS;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUBSPECIES;
import static nl.naturalis.nba.api.model.TaxonomicRank.SUPERFAMILY;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLUtil.getTestGenera;
import static nl.naturalis.nba.etl.col.CoLEntityType.REFERENCE_DATA;
import static nl.naturalis.nba.etl.col.CoLEntityType.SYNONYM_NAMES;
import static nl.naturalis.nba.etl.col.CoLEntityType.VERNACULAR_NAMES;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.acceptedNameUsageID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.classRank;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.description;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.family;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.genericName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.infraspecificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.kingdom;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.order;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.phylum;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.references;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificName;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.scientificNameAuthorship;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.specificEpithet;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.subgenus;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.superfamily;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonID;
import static nl.naturalis.nba.etl.col.CoLTaxonCsvField.taxonRank;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonDescription;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.etl.AbstractCSVTransformer;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.TransformUtil;

/**
 * The transformer component in the CoL ETL cycle.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
class CoLTaxonFullTransformer extends AbstractCSVTransformer<CoLTaxonCsvField, Taxon> {

  private static final List<String> allowedTaxonRanks;

  static {
    allowedTaxonRanks = Arrays.asList("species", "infraspecies");
  }

  private Connection connection;
  private String[] testGenera;
  private ArrayList<String> taxonIds;
  private HashMap<String, HashMap<CoLEntityType, List<String>>> cache;

  public CoLTaxonFullTransformer(ETLStatistics stats, Connection connection) {
    super(stats);
    this.connection = connection;
    testGenera = getTestGenera();
  }

  public void createLookupTable(ArrayList<String> taxonIds) throws SQLException {
    this.taxonIds = taxonIds;
    this.cache = new HashMap<>(taxonIds.size());
    createCache();
  }

  @Override
  protected boolean skipRecord() {
    /*
     * The acceptedNameUsageID field is a foreign key to an accepted name record in the same CSV file.
     * If the field is empty, it means the record is itself an accepted name record, so we must process
     * it.
     */
    if (input.get(acceptedNameUsageID) != null) {
      return true;
    }
    if (testGenera != null && !isTestSetGenus()) {
      return true;
    }
    return false;
  }

  @Override
  protected String getObjectID() {
    return input.get(taxonID);
  }

  @Override
  protected List<Taxon> doTransform() {
    String rank = input.get(taxonRank);
    if (!allowedTaxonRanks.contains(rank)) {
      stats.recordsSkipped++;
      if (logger.isDebugEnabled())
        debug("Ignoring taxon with rank \"%s\"", rank);
      return null;
    }
    String sourceSystemId = getSourceSystemId(input.get(references));
    if (sourceSystemId == null) {
      stats.recordsSkipped++;
      if (logger.isDebugEnabled())
        debug("Ignoring taxon with no unique source id (reference uri \"%s\"", input.get(taxonID));
      return null;
    }      
    try {
      stats.recordsAccepted++;
      stats.objectsProcessed++;
      Taxon taxon = new Taxon();
      taxon.setId(getElasticsearchId(COL, sourceSystemId));
      taxon.setSourceSystem(COL);
      taxon.setSourceSystemId(sourceSystemId);
      taxon.setTaxonRank(input.get(taxonRank));
      taxon.setAcceptedName(getScientificName());
      taxon.setDefaultClassification(getClassification());
      taxon.setSynonyms(getSynonyms(input.get(taxonID)));
      taxon.setVernacularNames(getVernacularNames(input.get(taxonID)));
      taxon.setReferences(getReferences(input.get(taxonID)));
      addMonomials(taxon);
      setRecordURI(taxon);
      setTaxonDescription(taxon);
      stats.objectsAccepted++;
      return Arrays.asList(taxon);
    } catch (Throwable t) {
      handleError(t);
      return null;
    }
  }
  
  /*
   * Utility method to extraxt the id from the CoL record URI
   */
  private static String getSourceSystemId(String uri) {
    if (uri == null || uri.contains("/synonym/")) return null;
    String[] chunks = uri.split("/details/species/id/");
    return chunks[1];
  }

  private void setTaxonDescription(Taxon taxon) {
    String descr = input.get(description);
    if (descr != null) {
      TaxonDescription td = new TaxonDescription();
      td.setDescription(descr);
      taxon.addDescription(td);
    }
  }

  private void setRecordURI(Taxon taxon) {
    String refs = input.get(references);
    if (refs == null) {
      if (!suppressErrors)
        warn("RecordURI not set. Missing Catalogue Of Life URL");
    } else {
      try {
        taxon.setRecordURI(URI.create(refs));
      } catch (IllegalArgumentException e) {
        if (!suppressErrors)
          warn("RecordURI not set. Invalid URL: \"%s\"", refs);
      }
    }
  }

  private DefaultClassification getClassification() {
    DefaultClassification dc = new DefaultClassification();
    dc.setKingdom(input.get(kingdom));
    dc.setPhylum(input.get(phylum));
    dc.setClassName(input.get(classRank));
    dc.setOrder(input.get(order));
    dc.setSuperFamily(input.get(superfamily));
    dc.setFamily(input.get(family));
    dc.setGenus(input.get(genericName));
    dc.setSubgenus(input.get(subgenus));
    dc.setSpecificEpithet(input.get(specificEpithet));
    dc.setInfraspecificEpithet(input.get(infraspecificEpithet));
    return dc;
  }

  private ScientificName getScientificName() {
    ScientificName sn = new ScientificName();
    sn.setFullScientificName(input.get(scientificName));
    sn.setGenusOrMonomial(input.get(genericName));
    sn.setSubgenus(input.get(subgenus));
    sn.setSpecificEpithet(input.get(specificEpithet));
    sn.setInfraspecificEpithet(input.get(infraspecificEpithet));
    sn.setAuthorshipVerbatim(input.get(scientificNameAuthorship));
    sn.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
    TransformUtil.setScientificNameGroup(sn);
    return sn;
  }

   private void createCache() throws SQLException {
     String ids = taxonIds.stream().map(id -> "'".concat(id).concat("'")).collect(Collectors.joining(","));
     for (CoLEntityType entityType : CoLEntityType.values()) {
       String sql = String.format("SELECT taxonId, document FROM %s WHERE taxonId in (%s);", entityType.toString(), ids);
       Statement stmt = connection.createStatement();
       ResultSet resultSet = stmt.executeQuery(sql);
       while (resultSet.next()) {
         String taxonId = resultSet.getString("taxonId");
         if (!cache.containsKey(taxonId)) {
           HashMap<CoLEntityType, List<String>> additionalData = new HashMap<>();
           additionalData.put(SYNONYM_NAMES, new ArrayList<>());
           additionalData.put(VERNACULAR_NAMES, new ArrayList<>());
           additionalData.put(REFERENCE_DATA, new ArrayList<>());
           cache.put(taxonId, additionalData);
         }
         cache.get(taxonId).get(entityType).add(resultSet.getString("document"));
       }
     }
   }

  private List<ScientificName> getSynonyms(String taxonId) {
    if (cache == null || !cache.containsKey(taxonId) || cache.get(taxonId).get(SYNONYM_NAMES) == null)
      return null;
    List<ScientificName> synonyms = new ArrayList<>();
    List<String> documents = cache.get(taxonId).get(SYNONYM_NAMES);
    if (documents.size() == 0)
      return null;
    for (String document : documents) {
      ScientificName synonym = JsonUtil.deserialize(document, ScientificName.class);
      synonyms.add(synonym);
    }
    return synonyms;
  }

  private List<VernacularName> getVernacularNames(String taxonId) {
    if (cache == null || !cache.containsKey(taxonId) || cache.get(taxonId).get(VERNACULAR_NAMES) == null)
      return null;
    List<VernacularName> vernacularNames = new ArrayList<>();
    List<String> documents = cache.get(taxonId).get(VERNACULAR_NAMES);
    if (documents.size() == 0)
      return null;
    for (String document : documents) {
      VernacularName name = JsonUtil.deserialize(document, VernacularName.class);
      vernacularNames.add(name);
    }
    return vernacularNames;
  }

  private List<Reference> getReferences(String taxonId) {
    if (cache == null || !cache.containsKey(taxonId) || cache.get(taxonId).get(REFERENCE_DATA) == null)
      return null;
    List<Reference> references = new ArrayList<>();
    List<String> documents = cache.get(taxonId).get(REFERENCE_DATA);
    if (documents.size() == 0)
      return null;
    for (String document : documents) {
      Reference reference = JsonUtil.deserialize(document, Reference.class);
      references.add(reference);
    }
    return references;
  }

  private static void addMonomials(Taxon taxon) {
    DefaultClassification dc = taxon.getDefaultClassification();
    Monomial m;
    if (dc.getKingdom() != null) {
      m = new Monomial(KINGDOM, dc.getKingdom());
      taxon.addMonomial(m);
    }
    if (dc.getPhylum() != null) {
      m = new Monomial(PHYLUM, dc.getPhylum());
      taxon.addMonomial(m);
    }
    if (dc.getClassName() != null) {
      m = new Monomial(CLASS, dc.getClassName());
      taxon.addMonomial(m);
    }
    if (dc.getOrder() != null) {
      m = new Monomial(ORDER, dc.getOrder());
      taxon.addMonomial(m);
    }
    if (dc.getSuperFamily() != null) {
      m = new Monomial(SUPERFAMILY, dc.getSuperFamily());
      taxon.addMonomial(m);
    }
    if (dc.getFamily() != null) {
      m = new Monomial(FAMILY, dc.getFamily());
      taxon.addMonomial(m);
    }
    // Tribe not used in Catalogue of Life.
    if (dc.getGenus() != null) {
      m = new Monomial(GENUS, dc.getGenus());
      taxon.addMonomial(m);
    }
    if (dc.getSubgenus() != null) {
      m = new Monomial(SUBGENUS, dc.getSubgenus());
      taxon.addMonomial(m);
    }
    if (dc.getSpecificEpithet() != null) {
      m = new Monomial(SPECIES, dc.getSpecificEpithet());
      taxon.addMonomial(m);
    }
    if (dc.getInfraspecificEpithet() != null) {
      m = new Monomial(SUBSPECIES, dc.getInfraspecificEpithet());
      taxon.addMonomial(m);
    }
  }

  private boolean isTestSetGenus() {
    String genus = input.get(genericName);
    if (genus == null) {
      return false;
    }
    genus = genus.toLowerCase();
    for (String s : testGenera) {
      if (s.equals(genus)) {
        return true;
      }
    }
    return false;
  }

}
