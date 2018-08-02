import java.io.FileOutputStream;
import java.io.IOException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.client.NbaSession;
import nl.naturalis.nba.client.SpecimenClient;

public class DownloadAsDWCA {

  public static void main(String[] args) throws IOException, InvalidQueryException {
    NbaSession session = new NbaSession();
    SpecimenClient client = session.getSpecimenClient();
    QueryCondition condition =
        new QueryCondition("identifications.scientificName.genusOrMonomial", "=", "Larus");
    QuerySpec query = new QuerySpec();
    query.addCondition(condition);
    String tmpDir = System.getProperty("java.io.tmpdir");
    try (FileOutputStream fos = new FileOutputStream(tmpDir + "/gulls.zip")) {
      client.dwcaQuery(query, fos);
    }
  }

}
