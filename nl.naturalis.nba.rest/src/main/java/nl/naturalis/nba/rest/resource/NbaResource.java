package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.NbaDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

public abstract class NbaResource<T extends IDocumentObject, U extends NbaDao<T>> {

  U dao; // NbaResource, e.g. SpecimenResource, TaxonResource ...

  @EJB
  Registry registry;

  NbaResource(U dao) {
    this.dao = dao;
  }

  public T find(String id, UriInfo uriInfo) {
    try {
      T result = dao.find(id);
      if (result == null) {
        throw new HTTP404Exception(uriInfo, DocumentType.SPECIMEN, id);
      }
      return result;
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public T[] findByIds(String ids, UriInfo uriInfo) {
    try {
      String[] idArray = StringUtil.split(ids, ",");
      return dao.findByIds(idArray);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public QueryResult<T> queryHttpGet(UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.query(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public QueryResult<T> queryHttpPostForm(MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.query(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public QueryResult<T> queryHttpPostJson(QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.query(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public long countHttpGet(UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.count(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public long countHttpPostForm(MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.count(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public long countHttpPostJson(QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.count(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public long countDistinctValuesHttpGet(String field, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.countDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public Long countDistinctValuesHttpPostForm(String field, MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.countDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public Long countDistinctValuesHttpPostJson(String field, QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.countDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> countDistinctValuesPerGroupHttpGet(String field, String group, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.countDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostForm(String field, String group, MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.countDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostJson(String field, String group, QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.countDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }
  
  public Map<String, Long> getDistinctValuesHttpGet(String field, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.getDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public Map<String, Long> getDistinctValuesHttpPostForm(String field, MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.getDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public Map<String, Long> getDistinctValuesHttpPostJson(String field, QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.getDistinctValues(field, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> getDistinctValuesPerGroupHttpGet(String field, String group, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      return dao.getDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> getDistinctValuesPerGroupHttpPost(String field, String group, MultivaluedMap<String, String> form, UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      return dao.getDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  public List<Map<String, Object>> getDistinctValuesPerGroupHttpJson(String field, String group, QuerySpec qs, UriInfo uriInfo) {
    try {
      return dao.getDistinctValuesPerGroup(field, group, qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

}
