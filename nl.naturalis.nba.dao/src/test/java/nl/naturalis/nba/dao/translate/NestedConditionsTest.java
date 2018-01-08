package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static org.junit.Assert.assertEquals;
import org.elasticsearch.client.Client;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.SpecimenDao;

@SuppressWarnings("static-method")
public class NestedConditionsTest {

  @Test
  public void test_01() throws InvalidQueryException {

    DocumentType<Specimen> dt = DocumentType.SPECIMEN;

    QueryCondition condition01 =
        new QueryCondition(new Path("identifications.scientificName.genusOrMonomial"), EQUALS, "Mammilla");
    QueryCondition condition02 =
        new QueryCondition(new Path("identifications.scientificName.specificEpithet"), EQUALS, "fibrosa");
    QueryCondition condition03 =
        new QueryCondition(new Path("identifications.scientificName.authorshipVerbatim"), EQUALS, "Souleyet, 1852");
    condition01.and(condition02);
    condition02.and(condition03);

    QuerySpec query = new QuerySpec();
    query.addCondition(condition01);
    
    QuerySpecTranslator translator = new QuerySpecTranslator(query, dt);
    System.out.println(translator.translate());
    
//    SpecimenDao dao = new SpecimenDao();
//    QueryResult<Specimen> result = dao.query(query);
    
  }

}
