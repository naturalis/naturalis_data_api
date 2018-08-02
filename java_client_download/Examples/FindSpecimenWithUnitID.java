
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.client.NbaSession;
import nl.naturalis.nba.client.SpecimenClient;

public class FindSpecimenWithUnitID {

  public static void main(String[] args) {
    NbaSession session = new NbaSession();
    SpecimenClient client = session.getSpecimenClient();
    // N.B. UnitID is not defined to be strictly unique, but
    // in practice always is.
    Specimen[] specimens = client.findByUnitID("ZMA.AVES.385");
    if (specimens.length == 1) {
      System.out
          .println("Record basis for specimen ZMA.AVES.385: " + specimens[0].getRecordBasis());
    }
  }

}
