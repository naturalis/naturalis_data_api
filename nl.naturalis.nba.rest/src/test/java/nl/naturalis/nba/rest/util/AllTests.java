package nl.naturalis.nba.rest.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	HttpQuerySpecBuilderTest.class,
	HttpGroupByScientificNameQuerySpecBuilderTest.class
})
public class AllTests {}
