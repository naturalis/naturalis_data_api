package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.*;
import static nl.naturalis.nba.client.ClientUtil.getQueryResult;
import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.ArrayUtil.implode;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.*;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Abstract base class for all client-side implementations of the NBA API.
 * Implements shared functionality specified by {@link INbaAccess} and provides
 * HTTP request plumbing functionality for subclasses.
 * 
 * @author Ayco Holleman
 * @Author Tom Gilissen
 *
 */
abstract class NbaClient<T extends IDocumentObject> extends Client implements INbaAccess<T> {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NbaClient.class);

	NbaClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public T find(String id)
	{
		SimpleHttpRequest request = getJson("find/" + id);
		int status = request.getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), documentObjectClass());
	}

	@Override
	public T[] findByIds(String[] ids)
	{
		SimpleHttpRequest request = getJson("findByIds/" + implode(ids));
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), documentObjectArrayClass());
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpRequest request = newQuerySpecRequest("query", querySpec);
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
		return getQueryResult(request.getResponseBody(), queryResultTypeReference());
	}

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpRequest request = newQuerySpecRequest("count", querySpec);
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
		return ClientUtil.getLong(request.getResponseBody());
	}
	
  @Override
  public long countDistinctValues(String forField, QuerySpec querySpec) throws InvalidQueryException {
    String path = "countDistinctValues/" + forField;
    SimpleHttpRequest request = newQuerySpecRequest(path, querySpec);
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
    return ClientUtil.getLong(request.getResponseBody());
  }
  
  @Override
  public List<Map<String, Object>> countDistinctValuesPerGroup(String forField, String forGroup, QuerySpec querySpec) throws InvalidQueryException 
  {
    String path = "countDistinctValuesPerGroup/" + forField + "/" + forGroup;
    SimpleHttpRequest request = newQuerySpecRequest(path, querySpec);
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
    TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
    return getObject(request.getResponseBody(), typeRef);
  }  

	@Override
	public Map<String, Long> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		String path = "getDistinctValues/" + forField;
		SimpleHttpRequest request = newQuerySpecRequest(path, querySpec);
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
		TypeReference<Map<String, Long>> typeRef = new TypeReference<Map<String, Long>>() {};
		return getObject(request.getResponseBody(), typeRef);
	}

  @Override
  public List<Map<String, Object>> getDistinctValuesPerGroup(String forField, String forGroup, QuerySpec querySpec) throws InvalidQueryException
  {
    String path = "getDistinctValuesPerGroup/" + forField + "/" + forGroup;
    SimpleHttpRequest request = newQuerySpecRequest(path, querySpec);
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
    TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
    return getObject(request.getResponseBody(), typeRef);
  }
	
	abstract Class<T> documentObjectClass();

	abstract Class<T[]> documentObjectArrayClass();

	abstract TypeReference<QueryResult<T>> queryResultTypeReference();

}
