package nl.naturalis.nba.etl;

import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * An extension of the {@link Transformer} interface that narrows the input of the
 * transformer to CSV records. No new methods are added to this interface; it just serves
 * to make things more specific.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            An enum whose constants represent the fields in the CSV file.
 * @param <OUTPUT>
 *            The type of object that is output by the transformer.
 */
public interface CSVTransformer<T extends Enum<T>, OUTPUT extends IDocumentObject>
		extends Transformer<CSVRecordInfo<T>, OUTPUT> {

	List<OUTPUT> transform(CSVRecordInfo<T> record);

}
