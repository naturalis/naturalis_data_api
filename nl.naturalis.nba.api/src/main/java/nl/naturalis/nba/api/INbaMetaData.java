package nl.naturalis.nba.api;

import java.util.Map;

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;

/**
 * Specifies methods for retrieving general metadata about the NBA. Note that
 * there also is an interface that specifies methods for retrieving metadata
 * about a specific document type ({@link IDocumentMetaData}).
 * 
 * @author Ayco Holleman
 *
 */
public interface INbaMetaData {

	Map<NbaSetting, Object> getSettings();

	/**
	 * Returns all source systems feeding into the NBA.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getSourceSystems
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
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getControlledLists
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	String[] getControlledLists();

	/**
	 * Returns all valid values within the list controlling {@link PhaseOrStage}
	 * fields.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getControlledList/PhaseOrStage
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	PhaseOrStage[] getControlledListPhaseOrStage();

	/**
	 * Returns all valid values within the list controlling
	 * {@link SpecimenTypeStatus} fields.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getControlledList/SpecimenTypeStatus
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	SpecimenTypeStatus[] getControlledListSpecimenTypeStatus();

	/**
	 * Returns all valid values within the list controlling {@link Sex} fields.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getControlledList/Sex
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	Sex[] getControlledListSex();

	/**
	 * Returns all valid values within the list controlling
	 * {@link TaxonomicStatus} fields.
	 * </p>
	 * <h5>REST API</h5>
	 * <p>
	 * The NBA REST API exposes this method through a GET request with the
	 * following end point:
	 * </p>
	 * <p>
	 * <code>
	 * http://api.biodiversitydata.nl/v2/metadata/getControlledList/TaxonomicStatus
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	TaxonomicStatus[] getControlledListTaxonomicStatus();

}
