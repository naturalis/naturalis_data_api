package nl.naturalis.nba.dao.format.calc;

import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;

import org.junit.Test;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.EntityObject;

public class VerbatimEventDateCalculatorTest {

  private static Specimen specimen;
  
  {
    specimen = new Specimen();
    specimen.setSourceSystem(SourceSystem.CRS);
    specimen.setSourceSystemId("RMNH.AVES.56322");
    specimen.setId("RMNH.AVES.56322@CRS");
  }
  
  @Test
  public void test_calculate_01()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    String date = calculator.calculateValue(entity);
    assertTrue("01", date.equals(""));    
  }
  
  @Test 
  public void test_calculate_02()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime end = OffsetDateTime.parse("1956-02-23T00:00:00+01:00");
    event.setDateTimeEnd(end);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);
    assertTrue("02", date.equals(""));
  }

  @Test 
  public void test_calculate_03()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime begin = OffsetDateTime.parse("1956-02-23T00:00:00+01:00");
    event.setDateTimeBegin(begin);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);
    assertTrue("03", date.equals("1956-02-23"));
  }

  @Test 
  public void test_calculate_04()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime begin = OffsetDateTime.parse("1956-02-23T10:00:00+01:00");
    OffsetDateTime end =   OffsetDateTime.parse("1956-02-23T16:11:00+01:00");
    event.setDateTimeBegin(begin);
    event.setDateTimeEnd(end);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);
    assertTrue("04", date.equals("1956-02-23"));    
  }
  
  @Test 
  public void test_calculate_05()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime begin = OffsetDateTime.parse("1956-02-23T00:00:00+01:00");
    OffsetDateTime end =   OffsetDateTime.parse("1956-02-24T00:00:00+01:00");
    event.setDateTimeBegin(begin);
    event.setDateTimeEnd(end);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);
    assertTrue("05", date.equals("1956-02-23 / 1956-02-24"));
  }

  @Test 
  public void test_calculate_06()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime begin = OffsetDateTime.parse("1956-02-23T23:00:00+01:00");
    OffsetDateTime end =   OffsetDateTime.parse("1956-02-24T00:30:00+02:00");
    event.setDateTimeBegin(begin);
    event.setDateTimeEnd(end);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);    
    assertTrue("06", date.equals("1956-02-23"));
  }

  @Test 
  public void test_calculate_07()
  {
    EntityObject entity = new EntityObject(specimen);
    VerbatimEventDateCalculator calculator = new VerbatimEventDateCalculator();
    
    GatheringEvent event = new GatheringEvent();
    OffsetDateTime begin = OffsetDateTime.parse("1956-02-23T23:00:00+01:00");
    OffsetDateTime end =   OffsetDateTime.parse("1956-02-24T01:05:00+02:00");
    event.setDateTimeBegin(begin);
    event.setDateTimeEnd(end);
    specimen.setGatheringEvent(event);
    
    String date = calculator.calculateValue(entity);
    System.out.println(date);
    assertTrue("07", date.equals("1956-02-23 / 1956-02-24"));
  }
  
  
}
