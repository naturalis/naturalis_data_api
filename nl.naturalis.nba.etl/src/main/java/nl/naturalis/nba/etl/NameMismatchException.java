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
		super(String.format(MSG_PAT, rank, dc.get(rank), sn.getNamePartForRank(rank)));
	}
}
