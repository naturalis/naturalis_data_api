package nl.naturalis.nba.etl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;

public class DataBaseWriter<T extends IDocumentObject> implements DocumentObjectWriter<T> {

  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "";
  private static final String DB_PASSWORD = "";
  private static Connection connection;
  
  private final ETLStatistics stats;
  
  private static final String TAXON01 = "{\"sourceSystem\":{\"code\":\"COL\",\"name\":\"Species 2000 - Catalogue Of Life\"},\"sourceSystemId\":\"316423\",\"recordURI\":\"http://www.catalogueoflife.org/annual-checklist/2018/2015/details/species/id/6a3ba2fef8659ce9708106356d875285\",\"id\":\"316423@COL\",\"taxonRank\":\"species\",\"acceptedName\":{\"fullScientificName\":\"Limacoccus brasiliensis (Hempel, 1934)\",\"taxonomicStatus\":\"accepted name\",\"genusOrMonomial\":\"Limacoccus\",\"specificEpithet\":\"brasiliensis\",\"authorshipVerbatim\":\"(Hempel, 1934)\",\"scientificNameGroup\":\"limacoccus brasiliensis\"},\"defaultClassification\":{\"kingdom\":\"Animalia\",\"phylum\":\"Arthropoda\",\"className\":\"Insecta\",\"order\":\"Hemiptera\",\"superFamily\":\"Coccoidea\",\"family\":\"Beesoniidae\",\"genus\":\"Limacoccus\",\"specificEpithet\":\"brasiliensis\"},\"systemClassification\":[{\"rank\":\"kingdom\",\"name\":\"Animalia\"},{\"rank\":\"phylum\",\"name\":\"Arthropoda\"},{\"rank\":\"class\",\"name\":\"Insecta\"},{\"rank\":\"order\",\"name\":\"Hemiptera\"},{\"rank\":\"superfamily\",\"name\":\"Coccoidea\"},{\"rank\":\"family\",\"name\":\"Beesoniidae\"},{\"rank\":\"genus\",\"name\":\"Limacoccus\"},{\"rank\":\"species\",\"name\":\"brasiliensis\"}]}";
  private static final String TAXON02 = "{\"sourceSystem\":{\"code\":\"COL\",\"name\":\"Species 2000 - Catalogue Of Life\"},\"sourceSystemId\":\"316424\",\"recordURI\":\"http://www.catalogueoflife.org/annual-checklist/2018/2015/details/species/id/943be7970684aa3f0b7200d1e8e12040\",\"id\":\"316424@COL\",\"taxonRank\":\"species\",\"acceptedName\":{\"fullScientificName\":\"Coccus bromeliae Bouché, 1833\",\"taxonomicStatus\":\"accepted name\",\"genusOrMonomial\":\"Coccus\",\"specificEpithet\":\"bromeliae\",\"authorshipVerbatim\":\"Bouché, 1833\",\"scientificNameGroup\":\"coccus bromeliae\"},\"defaultClassification\":{\"kingdom\":\"Animalia\",\"phylum\":\"Arthropoda\",\"className\":\"Insecta\",\"order\":\"Hemiptera\",\"superFamily\":\"Coccoidea\",\"family\":\"Coccidae\",\"genus\":\"Coccus\",\"specificEpithet\":\"bromeliae\"},\"systemClassification\":[{\"rank\":\"kingdom\",\"name\":\"Animalia\"},{\"rank\":\"phylum\",\"name\":\"Arthropoda\"},{\"rank\":\"class\",\"name\":\"Insecta\"},{\"rank\":\"order\",\"name\":\"Hemiptera\"},{\"rank\":\"superfamily\",\"name\":\"Coccoidea\"},{\"rank\":\"family\",\"name\":\"Coccidae\"},{\"rank\":\"genus\",\"name\":\"Coccus\"},{\"rank\":\"species\",\"name\":\"bromeliae\"}]}";
  private static final String TAXON03 = "{\"sourceSystem\":{\"code\":\"COL\",\"name\":\"Species 2000 - Catalogue Of Life\"},\"sourceSystemId\":\"316425\",\"recordURI\":\"http://www.catalogueoflife.org/annual-checklist/2018/2015/details/species/id/aa24fdca87177ac16bcd7627bed636b4\",\"id\":\"316425@COL\",\"taxonRank\":\"species\",\"acceptedName\":{\"fullScientificName\":\"Apiomorpha pomaphora Gullan & Jones, 1989\",\"taxonomicStatus\":\"accepted name\",\"genusOrMonomial\":\"Apiomorpha\",\"specificEpithet\":\"pomaphora\",\"authorshipVerbatim\":\"Gullan & Jones, 1989\",\"scientificNameGroup\":\"apiomorpha pomaphora\"},\"defaultClassification\":{\"kingdom\":\"Animalia\",\"phylum\":\"Arthropoda\",\"className\":\"Insecta\",\"order\":\"Hemiptera\",\"superFamily\":\"Coccoidea\",\"family\":\"Eriococcidae\",\"genus\":\"Apiomorpha\",\"specificEpithet\":\"pomaphora\"},\"systemClassification\":[{\"rank\":\"kingdom\",\"name\":\"Animalia\"},{\"rank\":\"phylum\",\"name\":\"Arthropoda\"},{\"rank\":\"class\",\"name\":\"Insecta\"},{\"rank\":\"order\",\"name\":\"Hemiptera\"},{\"rank\":\"superfamily\",\"name\":\"Coccoidea\"},{\"rank\":\"family\",\"name\":\"Eriococcidae\"},{\"rank\":\"genus\",\"name\":\"Apiomorpha\"},{\"rank\":\"species\",\"name\":\"pomaphora\"}]}";
  


  public DataBaseWriter(ETLStatistics stats)  {
    connection = getDBConnection();
    createTable();
    this.stats = stats;
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(Collection<T> objects) {
    
    if (objects == null || objects.size() == 0) {
      return;
    }

    for (T object : objects) {
      if (object != null) {
        Statement stmt = null;
        try {          
          connection.setAutoCommit(false);
          stmt = connection.createStatement();
          String id = object.getId();
          String document = JsonUtil.toJson(object).replaceAll("'", "''");
          stmt.execute(String.format("INSERT INTO TAXON(taxonId, document) VALUES('%s', '%s')", id, document));        
          stmt.close();
          connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } 
      }
//      Statement stmt = null;
//      stmt = connection.createStatement();
//      ResultSet rs = stmt.executeQuery("select * from TAXON");
//      while (rs.next()) {
//          System.out.println(rs.getString("taxonID") + ", " + rs.getString("document"));
//      }
//      stmt.execute("DROP TABLE TAXON");
//      stmt.close();
//      connection.commit();

    }
  }

  @Override
  public void suppressErrors(boolean suppressErrors) {
    // TODO Auto-generated method stub

  }

  private static Connection getDBConnection() {
    Connection dbConnection = null;
    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
    try {
      dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
      return dbConnection;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return dbConnection;
  }

  private static void createTable() {
    //Connection connection = getDBConnection();
    Statement stmt = null;
    try {
      connection.setAutoCommit(false);
      
      stmt = connection.createStatement();
      stmt.execute("CREATE TABLE TAXON(taxonId varchar(255) not null primary key, document LONGTEXT)");
      stmt.close();
      
      connection.commit();
    } catch (SQLException e) {
      System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } 
//    finally {
//      connection.close();
//    }
  }
  
  public void runTest() throws SQLException {
    Statement stmt = null;
    try {
        connection.setAutoCommit(false);
        stmt = connection.createStatement();
        
        stmt.execute("INSERT INTO TAXON(taxonId, document) VALUES('" + "316423@COL" + "', '" + TAXON01 + "')"); 
        stmt.execute(String.format("INSERT INTO TAXON(taxonId, document) VALUES('%s', '%s')", "316424@COL", TAXON02));
        stmt.execute("INSERT INTO TAXON(taxonId, document) VALUES('316425@COL', '" + TAXON03 + "')");
        
        ResultSet rs = stmt.executeQuery("select * from TAXON");
        while (rs.next()) {
            System.out.println(rs.getString("taxonId") + ", " + rs.getString("document"));
        }

        stmt.execute("DROP TABLE TAXON");
        stmt.close();
        connection.commit();
    } catch (SQLException e) {
        System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
        e.printStackTrace();
    } 
    finally {
        connection.close();
    }
  }
}
