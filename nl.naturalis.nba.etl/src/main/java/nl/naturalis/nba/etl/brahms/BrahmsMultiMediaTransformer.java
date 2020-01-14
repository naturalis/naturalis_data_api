package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE;
import static nl.naturalis.nba.etl.ETLConstants.LICENCE_TYPE;
import static nl.naturalis.nba.etl.ETLConstants.SOURCE_INSTITUTION_ID;
import static nl.naturalis.nba.etl.MimeTypeCache.MEDIALIB_HTTPS_URL;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.DAYIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.IMAGELIST;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.MONTHIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.PLANTDESC;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.VERNACULAR;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.YEARIDENT;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getDefaultClassification;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getScientificName;
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
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.etl.*;

/**
 * The transformer component in the ETL cycle for Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsMultiMediaTransformer extends BrahmsTransformer<MultiMediaObject> {//made public for test purpose

  private static final String DEFAULT_IMAGE_QUALITY = "ac:GoodQuality";
  private static final String DEFAULT_MIME_TYPE = "image/jpeg";
  private final MimeTypeCache mimetypeCache;
  private boolean enrich = false;

  BrahmsMultiMediaTransformer(ETLStatistics stats)
	{
		super(stats);
		mimetypeCache = MimeTypeCacheFactory.getInstance().getCache();
		MedialibIdsCache.getInstance();
	}
  
  void setEnrich(boolean enrich) {
    this.enrich = enrich;
  }
  
  boolean doEnrich() {
    return enrich;
  }

	@Override
	protected List<MultiMediaObject> doTransform()
	{
		// No record-level validations for Brahms multimedia, so:
		stats.recordsAccepted++;
		try {
		  ArrayList<MultiMediaObject> result = new ArrayList<>(3);
		  String images = input.get(IMAGELIST);
		  if (images != null) {
		    String[] urls = images.split(",");
		    for (int i = 0; i < urls.length; ++i) {
		      MultiMediaObject mmo = transformOne(urls[i]);
		      if (mmo != null) {
		        result.add(mmo);
		      }
		    }
		  }
		  return result;		  
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

	private MultiMediaObject transformOne(String url)
	{
		stats.objectsProcessed++;
		try {
			URI uri = getUri(url);
			MultiMediaObject mmo = newMultiMediaObject();

			String uriHash = String.valueOf(uri.toString().hashCode()).replace('-', '0');
			ServiceAccessPoint sap = newServiceAccessPoint(uri);
			if (sap == null) return null;
			mmo.addServiceAccessPoint(sap);

			mmo.setUnitID(objectID + '_' + uriHash);
			mmo.setId(getElasticsearchId(BRAHMS, mmo.getUnitID()));
			mmo.setSourceSystemId(mmo.getUnitID());
			String specimenID = getElasticsearchId(BRAHMS, objectID);
			mmo.setAssociatedSpecimenReference(specimenID);
			List<String> themes = themeCache.lookup(objectID, MULTI_MEDIA_OBJECT, BRAHMS);
			mmo.setTheme(themes);
			String description = input.get(PLANTDESC, true);
			if (description != null) mmo.setDescription(description.replaceAll("\u00001", ""));
			mmo.setGatheringEvents(Arrays.asList(getMultiMediaGatheringEvent(input)));
			mmo.setIdentifications(Arrays.asList(getIdentification()));
			if (doEnrich()) {
				enrichIdentification(mmo);
			}
			stats.objectsAccepted++;
			return mmo;
		}
		catch (URISyntaxException e) {
			stats.objectsRejected++;
			if (!suppressErrors)
				error("Invalid image URL: " + url);
			return null;
		}
		catch (Throwable t) {
			handleError(t);
			return null;
		}
	}
	
  /*
   * Temporary (?) modification to allow for enrichment during the specimen import
   * 
   * Retrieve taxonomic data from CoL and NSR and add it to the identification(s)
   */
  private void enrichIdentification(MultiMediaObject mmo) 
  {
    // A specimen can have one or more identifications
    // We need to check all identifications
    for (MultiMediaContentIdentification identification : mmo.getIdentifications()) {
      
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

	private static MultiMediaObject newMultiMediaObject()
	{
		MultiMediaObject mmo = new MultiMediaObject();
		mmo.setSourceSystem(BRAHMS);
		mmo.setSourceInstitutionID(SOURCE_INSTITUTION_ID);
		mmo.setOwner(SOURCE_INSTITUTION_ID);
		mmo.setSourceID("Brahms");
		mmo.setLicenseType(LICENCE_TYPE);
		mmo.setLicense(LICENCE);
		mmo.setCollectionType("Botany");
		return mmo;
	}

	private MultiMediaContentIdentification getIdentification()
	{
		MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
		identification.setTypeStatus(getTypeStatus());
		String s = input.get(VERNACULAR);
		if (s != null) {
			identification.setVernacularNames(Arrays.asList(new VernacularName(s)));
		}
		String y = input.get(YEARIDENT);
		String m = input.get(MONTHIDENT);
		String d = input.get(DAYIDENT);
		identification.setDateIdentified(getDate(y, m, d));
		ScientificName sn = getScientificName(input);
		DefaultClassification dc = getDefaultClassification(input, sn);
		identification.setScientificName(sn);
		identification.setDefaultClassification(dc);
		// System classification disabled for specimens and multimedia
		// identification.setSystemClassification(getSystemClassification(dc));
		return identification;
	}

	private MultiMediaGatheringEvent getMultiMediaGatheringEvent(
			CSVRecordInfo<BrahmsCsvField> record)
	{
		MultiMediaGatheringEvent ge = new MultiMediaGatheringEvent();
		populateGatheringEvent(ge, record);
		return ge;
	}

	private static URI getUri(String url) throws URISyntaxException
	{
		url = url.trim().replaceAll(" ", "%20");
		return new URI(url);
	}

	private ServiceAccessPoint newServiceAccessPoint(URI uri)
	{
		String mimeType = DEFAULT_MIME_TYPE;
		Pattern pattern = Pattern.compile("^.*id/(.*)/format/(.*)$");
		Matcher matcher = pattern.matcher(uri.getRawPath());
		String mediaObjectId = "";
		String format = "large";
		if (matcher.matches()) {
		  mediaObjectId = matcher.group(1);
		  if (!MedialibIdsCache.contains(mediaObjectId)) {
		  	if (!suppressErrors) {
				warn("Not an existing medialib URL: %s", uri.toString());
			}
		  	return null;
		  }
		  format = matcher.group(2);
		  mimeType = mimetypeCache.getMimeType(mediaObjectId);
		} else {
		  return null;
		}
		URI httpsUri;
		try {
		  httpsUri = new URI(MEDIALIB_HTTPS_URL + mediaObjectId + "/format/" + format );
		} catch (URISyntaxException e) {
		  warn("Incorrect URI for use in ServiceAccessPoint: %s", MEDIALIB_HTTPS_URL + mediaObjectId + "/format/" + format);
		  return null;
		}
		return new ServiceAccessPoint(httpsUri, mimeType, DEFAULT_IMAGE_QUALITY);
	}

}
