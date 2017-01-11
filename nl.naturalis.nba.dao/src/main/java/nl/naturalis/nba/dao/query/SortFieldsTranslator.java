package nl.naturalis.nba.dao.query;

import static org.elasticsearch.search.sort.SortOrder.ASC;
import static org.elasticsearch.search.sort.SortOrder.DESC;

import java.util.List;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;

class SortFieldsTranslator {

	private List<SortField> sortFields;
	private MappingInfo<?> mappingInfo;

	SortFieldsTranslator(List<SortField> sortFields, DocumentType<?> documentType)
	{
		this.sortFields = sortFields;
		this.mappingInfo = new MappingInfo<>(documentType.getMapping());
	}

	SortBuilder[] translate() throws InvalidQueryException
	{
		SortBuilder[] result = new SortBuilder[sortFields.size()];
		int i = 0;
		for (SortField sf : sortFields) {
			String path = sf.getPath();
			String nestedPath;
			try {
				ESField f = mappingInfo.getField(path);
				if (!(f instanceof SimpleField)) {
					throw invalidSortField(path);
				}
				nestedPath = MappingInfo.getNestedPath(f);
			}
			catch (NoSuchFieldException e) {
				throw invalidSortField(sf.getPath());
			}
			FieldSortBuilder sb = SortBuilders.fieldSort(path);
			sb.order(sf.isAscending() ? ASC : DESC);
			sb.sortMode(sf.isAscending() ? "min" : "max");
			if(nestedPath != null) {
				sb.setNestedPath(nestedPath);
			}
			result[i++] = sb;
		}
		return result;
	}

	private static InvalidQueryException invalidSortField(String field)
	{
		String fmt = "Invalid sort field: \"%s\"";
		String msg = String.format(fmt, field);
		return new InvalidQueryException(msg);
	}

}
