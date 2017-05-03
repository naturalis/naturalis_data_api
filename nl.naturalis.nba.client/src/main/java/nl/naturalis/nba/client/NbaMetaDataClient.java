package nl.naturalis.nba.client;

import java.util.Map;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.IDocumentMetaData;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;


abstract class NbaMetaDataClient<DOCUMENT_OBJECT extends IDocumentObject> implements IDocumentMetaData<DOCUMENT_OBJECT> {

	@Override
	public String[] getPaths(boolean sorted)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, FieldInfo> getFieldInfo(String... fields) throws NoSuchFieldException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOperatorAllowed(String field, ComparisonOperator operator)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
