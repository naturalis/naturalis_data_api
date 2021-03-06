package nl.naturalis.nba.api;

import java.util.Map;
import javax.management.relation.RelationType;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpatialDatum;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonRelationType;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;

/**
 * Specifies methods for retrieving general metadata about the NBA. Note that there also is an
 * interface that specifies methods for retrieving metadata about a specific document type
 * ({@link INbaDocumentMetaData}).
 * 
 * @author Ayco Holleman
 *
 */
public interface INbaMetaData {

  /**
   * Returns the value of a document-independent configuration setting. Note that document-specific
   * settings are retrieved using {@link INbaDocumentMetaData#getSetting(NbaSetting)} or
   * {@link INbaDocumentMetaData#getSettings()}.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getSetting/{name}
   * </code>
   * </p>
   * 
   * @return
   */
  Object getSetting(NbaSetting setting);

  /**
   * Returns a map of all document-independent configuration settings and their values. Note that
   * document-specific settings are retrieved using
   * {@link INbaDocumentMetaData#getSetting(NbaSetting)} or
   * {@link INbaDocumentMetaData#getSettings()}.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getSettings
   * </code>
   * </p>
   * 
   * @return
   */
  Map<NbaSetting, Object> getSettings();

  /**
   * Returns all source systems feeding into the NBA.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getSourceSystems
   * </code>
   * </p>
   * 
   * @return
   */
  SourceSystem[] getSourceSystems();

  /**
   * Returns all controlled lists used within the NBA data model.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledLists
   * </code>
   * </p>
   * 
   * @return
   */
  String[] getControlledLists();

  /**
   * Returns all valid values within the list controlling {@link AreaClass} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/AreaClass
   * </code>
   * </p>
   * 
   * @return
   */
  AreaClass[] getControlledListAreaClass();
  
  /**
   * Returns all valid values within the list controlling {@link License} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/License
   * </code>
   * </p>
   * 
   * @return
   */
  License[] getControlledListLicense();

  /**
   * Returns all valid values within the list controlling {@link LicenseType} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/LicenseType
   * </code>
   * </p>
   * 
   * @return
   */
  LicenseType[] getControlledListLicenseType();
  
  /**
   * Returns all valid values within the list controlling {@link Sex} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/Sex
   * </code>
   * </p>
   * 
   * @return
   */
  Sex[] getControlledListSex();
  
  /**
   * Returns all valid values within the list controlling {@link SpatialDatum} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/SpatialDatum
   * </code>
   * </p>
   * 
   * @return
   */
  SpatialDatum[] getControlledListSpatialDatum();
  
  /**
   * Returns all valid values within the list controlling {@link RelationType} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/RelationType
   * </code>
   * </p>
   * 
   * @return
   */
  TaxonRelationType[] getControlledListTaxonRelationType();

  /**
   * Returns all valid values within the list controlling {@link SpecimenTypeStatus} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/SpecimenTypeStatus
   * </code>
   * </p>
   * 
   * @return
   */
  SpecimenTypeStatus[] getControlledListSpecimenTypeStatus();

  /**
   * Returns all valid values within the list controlling {@link TaxonomicStatus} fields.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getControlledList/TaxonomicStatus
   * </code>
   * </p>
   * 
   * @return
   */
  TaxonomicStatus[] getControlledListTaxonomicStatus();

  /**
   * Returns the date formats that you can use to query date fields. The default date format is
   * "yyyy-MM-dd'T'HH:mm:ss.SSSZ". Strings using one of the other allowed date formats are first
   * converted to this format before being embedded in an Elasticsearch query.
   * </p>
   * <h5>REST API</h5>
   * <p>
   * The NBA REST API exposes this method through a GET request with the following end point:
   * </p>
   * <p>
   * <code>
   * https://api.biodiversitydata.nl/v2/metadata/getAllowedDateFormats
   * </code>
   * </p>
   * 
   * @return
   */
  String[] getAllowedDateFormats();

}
