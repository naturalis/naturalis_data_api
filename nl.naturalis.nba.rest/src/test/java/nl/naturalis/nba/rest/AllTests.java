package nl.naturalis.nba.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.rest.util.HttpQuerySpecBuilderTest;
import nl.naturalis.nba.rest.util.HttpGroupByScientificNameQuerySpecBuilderTest;

@RunWith(Suite.class)

//@formatter:off
@SuiteClasses({
	HttpQuerySpecBuilderTest.class,
	HttpGroupByScientificNameQuerySpecBuilderTest.class
})

//@formatter:on
public class AllTests {

}
