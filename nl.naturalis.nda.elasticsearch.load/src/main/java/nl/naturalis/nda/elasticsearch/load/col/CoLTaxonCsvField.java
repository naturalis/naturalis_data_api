package nl.naturalis.nda.elasticsearch.load.col;

/**
 * Enumerates the CSV fields in the taxa.txt file of a CoL DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
enum CoLTaxonCsvField
{
	taxonID,
	identifier,
	datasetID,
	datasetName,
	acceptedNameUsageID,
	parentNameUsageID,
	taxonomicStatus,
	taxonRank,
	verbatimTaxonRank,
	scientificName,
	kingdom,
	phylum,
	classRank,
	order,
	superfamily,
	family,
	genericName,
	genus,
	subgenus,
	specificEpithet,
	infraspecificEpithet,
	scientificNameAuthorship,
	source,
	namePublishedIn,
	nameAccordingTo,
	modified,
	description,
	taxonConceptID,
	scientificNameID,
	references
}