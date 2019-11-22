package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.BRAHMS_ABCD_COLLECTION_TYPE;
import static nl.naturalis.nba.etl.ETLConstants.BRAHMS_ABCD_SOURCE_ID;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.ETLConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.ETLUtil.getSpecimenPurl;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_HTTP_URL;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_HTTPS_URL;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.ACCESSION;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.CATEGORY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.COLLECTOR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAYIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DETBY;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.IMAGELIST;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTHIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.NOTONLINE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.NUMBER;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.OLDBARCODE;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PREFIX;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SUFFIX;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.VERNACULAR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEARIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getScientificName;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getTaxonRank;
import static nl.naturalis.nba.etl.enrich.EnrichmentUtil.createEnrichments;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.MimeTypeCache;
import nl.naturalis.nba.etl.MimeTypeCacheFactory;
import nl.naturalis.nba.etl.ThemeCache;

/**
 * The transformer component in the Brahms ETL cycle for specimens.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
class BrahmsSpecimenTransformer extends BrahmsTransformer<Specimen> {

  private static final ThemeCache themeCache;
  private static final String DEFAULT_IMAGE_QUALITY = "ac:GoodQuality";
  private static final String DEFAULT_MIME_TYPE = "image/jpeg";
  private final MimeTypeCache mimetypeCache;
  private boolean enrich = false;

  static {
    themeCache = ThemeCache.getInstance();
  }

  BrahmsSpecimenTransformer(ETLStatistics stats) //constructor made public for test.
  {
    super(stats);
    mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
  }
  
  void setEnrich(boolean enrich) {
    this.enrich = enrich;
  }
  
  boolean doEnrich() {
    return enrich;
  }

  @Override
  protected List<Specimen> doTransform()
  {
    // No record-level validations, so:
    stats.recordsAccepted++;
    stats.objectsProcessed++;
    try {
      Specimen specimen = new Specimen();
      specimen.setId(getElasticsearchId(BRAHMS, objectID));
      specimen.setSourceSystemId(objectID);
      specimen.setUnitID(objectID);
      specimen.setUnitGUID(getSpecimenPurl(objectID));
      setConstants(specimen);
      List<String> themes = themeCache.lookup(objectID, SPECIMEN, BRAHMS);
      specimen.setTheme(themes);
      String s = input.get(CATEGORY);
      if (s == null)
        specimen.setRecordBasis("Preserved Specimen");
      else
        specimen.setRecordBasis(s);
      specimen.setAssemblageID(getAssemblageID());
      String notes = input.get(PLANTDESC, true);
      if (notes != null) specimen.setNotes(notes.replaceAll("\u00001", ""));
      specimen.setPreviousUnitsText(getPreviousUnitsText());
      s = input.get(NOTONLINE);
      if (s == null || s.equals("0"))
        specimen.setObjectPublic(true);
      else
        specimen.setObjectPublic(false);
      specimen.setCollectorsFieldNumber(getCollectorsFieldNumber());
      specimen.setGatheringEvent(getGatheringEvent(input));
      specimen.addIndentification(getSpecimenIdentification(input));
      if (doEnrich()) {
        enrichIdentification(specimen);
      }
      specimen.setAssociatedMultiMediaUris(getServiceAccessPoints());
      stats.objectsAccepted++;
      return Arrays.asList(specimen);
    }
    catch (Throwable t) {
      stats.objectsRejected++;
      if (!suppressErrors) {
        error(t.getMessage());
        error(input.getLine());
      }
      return null;
    }
  }
  
  /*
   * Temporary (?) modification to allow for enrichment during the specimen import
   * 
   * Retrieve taxonomic data from CoL and NSR and add it to the identification(s)
   */
  private void enrichIdentification(Specimen specimen) 
  {  
    // A specimen can have one or more identifications
    // We need to check all identifications
    for (SpecimenIdentification identification : specimen.getIdentifications()) {
      
      // The scientificNameGroup is the "id" to link with the taxon documents
      String scientificNameGroup = identification.getScientificName().getScientificNameGroup();
      
      String field = "acceptedName.scientificNameGroup";
      QueryCondition condition = new QueryCondition(field, "EQUALS_IC", scientificNameGroup);
      QuerySpec query = new QuerySpec();
      query.addCondition(condition);
      query.setConstantScore(true);
      
      TaxonDao dao = new TaxonDao();
      QueryResult<Taxon> result;
      try {
        result = dao.query(query);
      }
      catch (InvalidQueryException e) {
        throw new ETLRuntimeException(e);
      }
      
      if (result.getTotalSize() == 0) {
        // No enrichment data available
        continue;
      }

      List<Taxon> taxa = new ArrayList<>();
      for (QueryResultItem<Taxon> item : result) {
        taxa.add(item.getItem());
      }
      
      List<TaxonomicEnrichment> enrichment = null;
      enrichment = createEnrichments(taxa);
      
      if (enrichment != null) {
        identification.setTaxonomicEnrichments(enrichment);
      }   
    }
  }


  private GatheringEvent getGatheringEvent(CSVRecordInfo<BrahmsCsvField> record)
  {
    GatheringEvent ge = new GatheringEvent();
    populateGatheringEvent(ge, record);
    return ge;
  }

  private SpecimenIdentification getSpecimenIdentification(CSVRecordInfo<BrahmsCsvField> record)
  {
    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setTypeStatus(getTypeStatus());
    String s = record.get(DETBY);
    if (s != null)
      identification.addIdentifier(new Agent(s));
    s = record.get(VERNACULAR);
    if (s != null)
      identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
    String y = record.get(YEARIDENT);
    String m = record.get(MONTHIDENT);
    String d = record.get(DAYIDENT);
    identification.setDateIdentified(getDate(y, m, d));
    ScientificName sn = getScientificName(record);
    DefaultClassification dc = getDefaultClassification(record, sn);
    identification.setTaxonRank(getTaxonRank(record));
    identification.setScientificName(sn);
    identification.setDefaultClassification(dc);
    // System classification disabled for specimens and multimedia
    // identification.setSystemClassification(getSystemClassification(dc));
    return identification;
  }

  private static void setConstants(Specimen specimen)
  {
    specimen.setSourceSystem(BRAHMS);
    specimen.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
    specimen.setOwner(SOURCE_INSTITUTION_ID);
    specimen.setSourceID(BRAHMS_ABCD_SOURCE_ID);
    specimen.setLicenseType(LICENCE_TYPE);
    specimen.setLicense(LICENCE);
    specimen.setCollectionType(BRAHMS_ABCD_COLLECTION_TYPE);
  }

  /*
   * Returns the id of the "botanical" record, which in Brahms is the
   * relational parent of the specimen record. Multiple specimens (twigs,
   * leaves, etc.) can belong to the same botanical record. Because we need to
   * make sure this id is not just unique within Brahms but NBA-wide, we
   * append the Brahm system code to it.
   */
  private String getAssemblageID()
  {
    Float f = getFloat(input, BrahmsCsvField.BRAHMS);
    if (f == null) {
      return null;
    }
    return ESUtil.getElasticsearchId(BRAHMS, f.intValue());
  }

  private String getCollectorsFieldNumber()
  {
    StringBuilder sb = new StringBuilder(64);
    CSVRecordInfo<BrahmsCsvField> rec = input;
    sb.append(rec.get(COLLECTOR, false).trim()).append(' ');
    sb.append(rec.get(PREFIX, false).trim()).append(' ');
    sb.append(rec.get(NUMBER, false).trim()).append(' ');
    sb.append(rec.get(SUFFIX, false).trim());
    return sb.toString();
  }
  
  /*
   * Returns the value from OLDBARCODE concatenated with the value of
   * ACCESSION separated by ' | '. Empty values or null are excluded.
   */
  private String getPreviousUnitsText()
  {
    CSVRecordInfo<BrahmsCsvField> rec = input;
    String oldBarcode = rec.get(OLDBARCODE, true);
    String accession = rec.get(ACCESSION, true);
    String sep = "";
    if (oldBarcode != null && oldBarcode.trim().length() > 0) {
      sep = " | ";
    }
    if (accession != null && accession.trim().length() > 0) {
      if (sep.length() > 0) {
        return oldBarcode.trim() + sep + accession.trim();
      }
      return accession.trim();
    }
    return sep.length() > 0 ? oldBarcode.trim() : null;
  }

  private List<ServiceAccessPoint> getServiceAccessPoints()
  {
    String images = input.get(IMAGELIST);
    if (images == null) {
      return null;
    }
    String[] urls = images.split(",");
    List<ServiceAccessPoint> saps = new ArrayList<>(urls.length);
    for (int i = 0; i < urls.length; ++i) {
      String url = urls[i].trim().replaceAll(" ", "%20");
      // Change http urls to https urls, but leave the rest as they are 
      if (url.startsWith(MEDIALIB_HTTP_URL) && !url.startsWith(MEDIALIB_HTTPS_URL)) {
        url = url.replace(MEDIALIB_HTTP_URL, MEDIALIB_HTTPS_URL);
      }
      // Try to retrieve the mimetype from the mimetype cache
      String mimeType = DEFAULT_MIME_TYPE;
      Pattern pattern = Pattern.compile("^.*id/(.*)/format/.*$");
      Matcher matcher = pattern.matcher(url);
      String mediaObjectId = "";
      if (matcher.matches()) {
        mediaObjectId = matcher.group(1);
        mimeType = mimetypeCache.getMimeType(mediaObjectId);
      }
      
      try {
        URI uri = new URI(url);
        saps.add(new ServiceAccessPoint(uri, mimeType, DEFAULT_IMAGE_QUALITY));
      }
      catch (URISyntaxException e) {
        if (!suppressErrors) {
          warn("Invalid multimedia URL: " + url);
        }
      }
    }
    return saps.size() == 0 ? null : saps;
  }

}
