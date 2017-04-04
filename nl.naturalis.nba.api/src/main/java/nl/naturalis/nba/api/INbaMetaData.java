package nl.naturalis.nba.api;

import java.util.Map;

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;

/**
 * Specifies methods for retrieving generic metadata about the NBA. Note that
 * there also is an interface that specifies methods for retrieving metadata
 * about a specific document type ({@link IDocumentMetaData}).
 * 
 * @author Ayco Holleman
 *
 */
public interface INbaMetaData {

	Map<NbaSetting, Object> getSettings();

	SourceSystem[] getSourceSystems();

	String[] getControlledLists();

	Sex[] getControlledListSex();

	PhaseOrStage[] getControlledListPhaseOrStage();

	TaxonomicStatus[] getControlledListTaxonomicStatus();

	SpecimenTypeStatus[] getControlledListSpecimenTypeStatus();

}
