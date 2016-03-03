package nl.naturalis.nda.elasticsearch.load;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonomicRank;

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
