import java.io.FileOutputStream;
import java.io.IOException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.client.NbaSession;
import nl.naturalis.nba.client.SpecimenClient;

public class DownloadAllDataSets {

  public static void main(String[] args) throws NoSuchDataSetException, IOException {
    NbaSession session = new NbaSession();
    SpecimenClient client = session.getSpecimenClient();
    String tmpDir = System.getProperty("java.io.tmpdir");
    for (String dataset : client.dwcaGetDataSetNames()) {
      System.out.println("Downloading dataset " + dataset);
      try (FileOutputStream fos = new FileOutputStream(tmpDir + "/" + dataset + ".zip")) {
        client.dwcaGetDataSet(dataset, fos);
      }
    }
  }

}
