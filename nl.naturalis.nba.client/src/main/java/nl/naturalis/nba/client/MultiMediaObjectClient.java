package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.invalidQueryException;
import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Provides access to multimedia-related information. Client-side implementation
 * of the {@link IMultiMediaObjectAccess}.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
public class MultiMediaObjectClient extends NbaClient<MultiMediaObject>
		implements IMultiMediaObjectAccess {

  private static final Logger logger = LogManager.getLogger(MultiMediaObjectClient.class);
  
	MultiMediaObjectClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}
	
  @Override
  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException {
    SimpleHttpRequest request = newQuerySpecRequest("download/", querySpec);
    request.setHeader("Accept-Encoding", "gzip");
    sendRequest(request);
    int status = request.getStatus();
    if (status != HTTP_OK) {
      byte[] response = request.getResponseBody();
      ServerException exception = newServerException(status, response);
      if (exception.was(InvalidQueryException.class)) {
        throw invalidQueryException(exception);
      }
      throw exception;
    }
    InputStream in = null;
    try {
      logger.info("Downloading result");
      in = request.getResponseBodyAsStream();
      IOUtil.pipe(in, out, 4096);
      logger.info("Download complete");
    }
    finally {
      IOUtil.close(in);
    }
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

}
