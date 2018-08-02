import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.client.NbaSession;
import nl.naturalis.nba.client.TaxonClient;

public class QueryWithMultipleConditions {

  public static void main(String[] args) throws InvalidQueryException {
    NbaSession session = new NbaSession();
    TaxonClient client = session.getTaxonClient();
    QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
    condition.and("acceptedName.specificEpithet", "=", "fuscus");
    QuerySpec query = new QuerySpec();
    query.addCondition(condition);
    QueryResult<Taxon> taxa = client.query(query);
    for (QueryResultItem<Taxon> taxon : taxa) {
      System.out.println(taxon.getItem().getAcceptedName().getFullScientificName());
    }
  }

}
