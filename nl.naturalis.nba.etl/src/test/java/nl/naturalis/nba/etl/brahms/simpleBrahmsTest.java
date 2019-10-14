package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.AUTHOR2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.GENUS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.RANK2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SPECIES;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.etl.CSVRecordInfo;

public class simpleBrahmsTest {

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getScientificName(nl.naturalis.nba.etl.CSVRecordInfo)}.
   * 
   * Test to verify getScientificName method returns the expected {@link ScientificName} object
   */
  @Test
  public void testGetScientificName_01() {
    
    // TODO: unfinished unit test
    
    @SuppressWarnings("unchecked")
    // CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
    CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
    when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
    when(record.get(AUTHOR2)).thenReturn("L.");
    when(record.get(GENUS)).thenReturn("Rhododendron");
    when(record.get(SP1)).thenReturn("ferrugineum");
    when(record.get(RANK2)).thenReturn("");
    when(record.get(SP2)).thenReturn("");

    ScientificName expected = new ScientificName();
    expected.setFullScientificName("Rhododendron ferrugineum L.");
    expected.setAuthorshipVerbatim("L.");
    expected.setGenusOrMonomial("Rhododendron");
    expected.setSpecificEpithet("ferrugineum");
    expected.setInfraspecificMarker("");
    expected.setInfraspecificEpithet("");

    ScientificName actual = BrahmsImportUtil.getScientificName(record);
    assertNotNull("01",actual);
    assertEquals("02",expected.getFullScientificName(), actual.getFullScientificName());

  }
  
  @Test
  public void testGetAuthor() throws Exception {

    ScientificName expected = new ScientificName();

    @SuppressWarnings("unchecked")
    CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
    when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
    when(record.get(AUTHOR2)).thenReturn("L.");
    when(record.get(GENUS)).thenReturn("Rhododendron");
    when(record.get(SP1)).thenReturn("ferrugineum");
    when(record.get(RANK2)).thenReturn("");
    when(record.get(SP2)).thenReturn("");

    expected = BrahmsImportUtil.getScientificName(record);
    expected.getAuthorshipVerbatim();

    assertNotNull("01",expected);
    assertEquals("02","L.", expected.getAuthorshipVerbatim());

  }


}
