package nl.naturalis.nba.dao.format.dwca;

import nl.naturalis.nba.dao.DocumentType;

/**
 * Symbolic constants for the DwCA data set types served by the NBA. Note that
 * we explicitly don't use the {@link DocumentType} class to specify data set
 * types because it is conceiveable that we will want to produce data sets
 * contain mixed or aggregated data.
 * 
 * @author Ayco Holleman
 *
 */
/*
 * For each type there must be a directory under the "dwca" directory (full path
 * usually /etc/nba/dwca) with the same name, but in lowercase.
 */
public enum DwcaDataSetType
{
	TAXON, SPECIMEN;
}
