package nl.naturalis.nba.client;

import java.io.OutputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;

/**
 * Provides access to multimedia-related information. Client-side implementation
 * of the {@link IMultiMediaObjectAccess}.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 *
 */
public class MultiMediaObjectClient extends NbaClient<MultiMediaObject>
		implements IMultiMediaObjectAccess {

	MultiMediaObjectClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

	@Override
	Class<MultiMediaObject> documentObjectClass()
	{
		return MultiMediaObject.class;
	}

	@Override
	Class<MultiMediaObject[]> documentObjectArrayClass()
	{
		return MultiMediaObject[].class;
	}

	@Override
	TypeReference<QueryResult<MultiMediaObject>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<MultiMediaObject>>() {};
	}

  @Override
  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException {
    // TODO Auto-generated method stub
    
  }

}
