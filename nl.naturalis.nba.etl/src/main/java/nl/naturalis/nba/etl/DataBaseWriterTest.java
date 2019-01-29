package nl.naturalis.nba.etl;

import java.sql.SQLException;

public class DataBaseWriterTest {
  
  public static void main(String[] args) {
    ETLStatistics stats = new ETLStatistics();
    DataBaseWriter writer;
    
    try {
      writer = new DataBaseWriter(stats);
      writer.runTest();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
  }

}
