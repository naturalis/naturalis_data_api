package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.search.sort.SortBuilders.fieldSort;
import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

import java.util.List;

import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.SortField;
import nl.naturalis.nba.common.es.map.PrimitiveField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.dao.es.DocumentType;

public class SortFieldsTranslator {

	private List<SortField> sortFields;
	private MappingInfo mappingInfo;

	public SortFieldsTranslator(List<SortField> sortFields, DocumentType documentType)
	{
		this.sortFields = sortFields;
		this.mappingInfo = new MappingInfo(documentType.getMapping());
	}

	public SortBuilder[] translate() throws InvalidQueryException
	{
		SortBuilder[] result = new SortBuilder[sortFields.size()];
		int i = 0;
		for (SortField sf : sortFields) {
			String path = sf.getPath();
			try {
				ESField f = mappingInfo.getField(path);
				if (!(f instanceof PrimitiveField)) {
					throw invalidSortField(path);
				}
				if (MappingInfo.isArrayOrDescendendantOfArray(f)) {
					throw sortOnMultiValuedField(path);
				}
			}
			catch (NoSuchFieldException e) {
				throw invalidSortField(sf.getPath());
			}
			SortOrder order = sf.isAscending() ? ASC : DESC;
			result[i++] = fieldSort(path).order(order);
		}
		return result;
	}

	private static InvalidQueryException invalidSortField(String field)
	{
		String fmt = "Invalid sort field: \"%s\"";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}

	private static InvalidQueryException sortOnMultiValuedField(String field)
	{
		String fmt = "Invalid sort field: \"%s\". The field is multi-valued "
				+ "or embedded within an object array.";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}
}
