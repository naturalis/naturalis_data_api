package nl.naturalis.nba.dao.es.map;

/**
 * An {@code IndexableField} is a {@link ESField} that lets you specify if and
 * how it is indexed. A {@code Field} can be either a top-level
 * {@link DocumentField} or a virtual "{@link MultiField multi-field}"
 * underneath it.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class IndexableField extends ESField {

	protected Index index;
	protected String analyzer;

	public Index getIndex()
	{
		return index;
	}

	public void setIndex(Index index)
	{
		this.index = index;
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

	public void setAnalyzer(String analyzer)
	{
		this.analyzer = analyzer;
	}

}
