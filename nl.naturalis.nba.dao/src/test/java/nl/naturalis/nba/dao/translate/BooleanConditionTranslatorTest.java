package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static nl.naturalis.nba.dao.DaoTestUtil.jsonEquals;

import static org.junit.Assert.assertTrue;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchNoneQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;

public class BooleanConditionTranslatorTest {
  
  @BeforeClass
  public static void init() {}
  
  @Test
  public void testTrueConditionTranslator01() {
    QueryCondition condition = new QueryCondition(true);
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    ConditionTranslator translator;
    try {
      translator = getTranslator(condition, dt);
      QueryBuilder query = translator.translate();
      assertTrue("01", query instanceof MatchAllQueryBuilder);
    } catch (InvalidConditionException e) {
      Assert.fail(String.format("01, %s", e.getMessage()));
    }
  }

  @Test
  public void testTrueConditionTranslator02() {
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    QueryCondition condition = new QueryCondition("collectionType", NOT_EQUALS, null);
    condition.and(new QueryCondition(true));
    QuerySpec qs = new QuerySpec();
    qs.addCondition(condition);
    QuerySpecTranslator translator = new QuerySpecTranslator(qs, dt);
    try {
      String jsonFile = "query/BooleanConditionTranslatorTest_testQuery_01.json";
      String jsonResult = translator.translate().toString();
      assertTrue("02", jsonEquals(this.getClass(), jsonResult, jsonFile));
    } catch (InvalidQueryException e) {
      Assert.fail(String.format("02, %s", e.getMessage()));
    }
  }

  @Test
  public void testFalseConditionTranslator01() {
    QueryCondition condition = new QueryCondition(false);
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    ConditionTranslator translator;
    try {
      translator = getTranslator(condition, dt);
      QueryBuilder query = translator.translate();
      assertTrue("03", query instanceof MatchNoneQueryBuilder);
    } catch (InvalidConditionException e) {
      Assert.fail(String.format("03, %s", e.getMessage()));
    }
  }

  @Test
  public void testFalseConditionTranslator02() {
    DocumentType<Specimen> dt = DocumentType.SPECIMEN;
    QueryCondition condition = new QueryCondition(false);
    QueryCondition passer = new QueryCondition("identifications.scientificName.genusOrMonomial", EQUALS_IC, "passer");
    QueryCondition passerItaliae = new QueryCondition(passer).and("identifications.scientificName.specificEpithet", EQUALS_IC, "italiae");
    QueryCondition passerLuteus = new QueryCondition(passer).and("identifications.scientificName.specificEpithet", EQUALS_IC, "luteus");
    condition.or(passerItaliae).or(passerLuteus);
    QuerySpec qs = new QuerySpec();
    qs.addCondition(condition);
    QuerySpecTranslator translator = new QuerySpecTranslator(qs, dt);
    try {
      String jsonFile = "query/BooleanConditionTranslatorTest_testQuery_02.json";
      String jsonResult = translator.translate().toString();
      assertTrue("04", jsonEquals(this.getClass(), jsonResult, jsonFile));
    } catch (InvalidQueryException e) {
      Assert.fail(String.format("04, %s", e.getMessage()));
    }
  }
  
}
