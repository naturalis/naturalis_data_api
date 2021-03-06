package nl.naturalis.nba.etl;

import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * An extension of the {@link Transformer} interface that narrows the input of the
 * transformer to XML records. No new methods are added to this interface; it just serves
 * to make things more specific.
 * 
 * @author Ayco Holleman
 *
 * @param <OUTPUT>
 *            The type of object that is output by the transformer.
 */
public interface XMLTransformer<OUTPUT extends IDocumentObject>
		extends Transformer<XMLRecordInfo, OUTPUT> {

	List<OUTPUT> transform(XMLRecordInfo input);

}
