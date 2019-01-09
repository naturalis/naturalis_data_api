package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicRank;

/**
 * Thrown when some part of a {@link ScientificName} does not match the
 * corresponding part in a {@link DefaultClassification} or set of
 * {@link Monomial}s.
 * 
 * @author Ayco Holleman
 *
 */
public class NameMismatchException extends Exception {

  private static final long serialVersionUID = 1L;
  
	private static final String MSG_PAT = "Mismatch between %s in classification and scientific name: \"%s\", \"%s\"";

	/**
	 * Constructs a new {@code NameMismatchException}.
	 * 
	 * @param rank
	 *            The taxonomic rank for which there was a name mismatch
	 * @param dc
	 *            The classification
	 * @param sn
	 *            The scientific name
	 */
	public NameMismatchException(TaxonomicRank rank, DefaultClassification dc, ScientificName sn)
	{
		super(String.format(MSG_PAT, rank, dc.get(rank), getNamePartForRank(rank, sn)));
	}

	private static String getNamePartForRank(TaxonomicRank rank, ScientificName sn)
	{
		switch (rank) {
			case GENUS:
				return sn.getGenusOrMonomial();
			case SUBGENUS:
				return sn.getSubgenus();
			case SPECIES:
				return sn.getSpecificEpithet();
			case SUBSPECIES:
				return sn.getInfraspecificEpithet();
			default:
				return null;
		}
	}

}
