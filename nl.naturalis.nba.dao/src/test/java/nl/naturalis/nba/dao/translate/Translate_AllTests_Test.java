package nl.naturalis.nba.dao.translate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  BetweenConditionTranslatorTest.class,
  BooleanConditionTranslatorTest.class,
  ConditionCollectorTest.class,
  ConditionPreprocessorTest.class,
  ConditionTranslatorFactoryTest.class,
  ConditionTranslatorTranslateTest.class,
  EqualsConditionTranslatorTest.class,
  EqualsIgnoreCaseConditionTranslatorTest.class,
  ExploderTest.class,
  InConditionTranslatorTest.class,
  LikeConditionTranslatorTest.class,
  MatchesConditionTranslatorTest.class,
  //ShapeInShapeConditionTranslatorTest.class
})
public class Translate_AllTests_Test {}
