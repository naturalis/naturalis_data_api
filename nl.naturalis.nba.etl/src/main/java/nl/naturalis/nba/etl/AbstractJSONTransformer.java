package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Base class for transformers that take JSON documents as their input. Used for NSR.
 *
 * @param <OUTPUT> The type of object that is output from the transformer
 */
public abstract class AbstractJSONTransformer<OUTPUT extends IDocumentObject> extends AbstractTransformer<String, OUTPUT> {

    public AbstractJSONTransformer(ETLStatistics stats)
    {
        super(stats);
    }

}
