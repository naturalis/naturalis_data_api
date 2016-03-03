package nl.naturalis.nda.elasticsearch.load;


/**
 * Base class for transformers that take XML elements as their input. Used for
 * CRS and NSR.
 * 
 * @author Ayco Holleman
 *
 * @param <OUTPUT>
 *            The type of object that is output from the transformer
 */
public abstract class AbstractXMLTransformer<OUTPUT> extends AbstractTransformer<XMLRecordInfo, OUTPUT> {

	public AbstractXMLTransformer(ETLStatistics stats)
	{
		super(stats);
	}
}
