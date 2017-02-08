package nl.naturalis.nba.api;

import java.util.List;

/**
 * <p>
 * Models an NBA query. All information required by the various {@code query}
 * methods in the API take a {@link SearchSpec} object to drive the query
 * process.
 * </p>
 * <h3>Providing query specifications through the REST API</h3>
 * <p>
 * Whenever a method in the formal API (the set of interfaces in the
 * {@code nl.naturalis.nba.api} package) takes a {@code QuerySpec} object, the
 * corresponding REST API gives you two options the encode the {@code QuerySpec}
 * object in the URL. One option is to provide a {@code _querySpec} query
 * parameter whose value is the JSON-encoded {@code QuerySpec} object (i.e. the
 * {@code QuerySpec} object serialized to JSON). For example:
 * </p>
 * <p>
 * <code>
 * http://api.biodiversitydata.nl/v2/specimen/query?_querySpec=%7B%22conditions%22%3A%5B%7B%22field%22%3A%22sourceSystem.code%22%2C%22operator%22%3A%22EQUALS%22%2C%22value%22%3A%22BRAHMS%22%7D%5D%2C%22from%22%3A0%2C%22size%22%3A0%7D<br>
 * </code>
 * </p>
 * <p>
 * Since these URLs are hard to read and construct for humans, you can also
 * encode the query specification as follows:
 * </p>
 * <p>
 * <ol>
 * <li>Every query parameter that does not start with an underscore is turned
 * into a {@link QueryCondition query condition}. For example:<br>
 * <code>
 * http://api.biodiversitydata.nl/v2/specimen/query?sourceSystem.code=CRS&recordBasis=FossileSpecimen<br>
 * </code>
 * <li>The {@code _fields} parameter can be used to set the fields you want
 * returned in the response. You can specify multiple fields by separating them
 * with a comma. See {@link #setFields(List) setFields}.
 * <li>The {@code _from} parameter can be used to specify an result set offset.
 * See {@link #setFrom(Integer) setFrom}.
 * <li>The {@code _size} parameter can be used to specify the maximum number of
 * documents to return. See {@link #setSize(Integer) setSize}.
 * <li>The {@code _sortFields} parameter can be used to specify the fields on
 * which to sort. You can specify multiple fields as well as sort directions by
 * using commas to separate the fields and colons the separate field from sort
 * direction. See {@link #setSortFields(List) setSortFields}. For example:<br>
 * <code>
 * http://api.biodiversitydata.nl/v2/specimen/query?sourceSystem.code=CRS&_sortFields=recordBasis:ASC,unitID:DESC<br>
 * </code>
 * <li>The {@code _logicalOperator} parameter can be used to specify the logical
 * operator joining the query conditions (either AND or OR). See
 * {@link #setLogicalOperator(LogicalOperator) setLogicalOperator}.
 * <li>The {@code _ignoreCase} parameter can be used to issue a case-insensitive
 * search.
 * </ol>
 * </p>
 * <p>
 * You cannot mix the two encoding options. You must <b>either</b> provide a
 * {@code _querySpec} query parameter <b>or</b> use the combination of
 * parameters listed above. Complex queries with operators other than
 * {@link ComparisonOperator#EQUALS} or with nested query conditions are not
 * possible with the second option.
 * </p>
 * <h3>Example</h3>
 * <p>
 * Here is an example of building a query specification.
 * </p>
 * <p>
 * <b>First, by way of reference, the SQL equivalent:</b>
 * </p>
 * <p>
 * <code>
 *&nbspSELECT *<br>
 *&nbsp;&nbsp;&nbsp;FROM Specimen<br>
 *&nbsp;&nbsp;WHERE (gatheringEvent.gatheringPersons.fullName LIKE '%burg%' AND unitID = 'ZMA.MAM.100')<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;OR UPPER(phaseOrStage) = 'EGG'<br>
 *&nbsp;&nbsp;ORDER BY unitID DESC<br>
 *&nbsp;&nbsp;LIMIT 100, 50
 * </code>
 * </p>
 * <p>
 * <b>Next, using a {@code QuerySpec} object:</b>
 * </p>
 * <p>
 * <code>
 * Condition condition1 = new Condition("gatheringEvent.gatheringPersons.fullName", LIKE, "burg");<br>
 * condition1.and("unitID", "=", "ZMA.MAM.100");<br>
 * Condition condition2 = new Condition("phaseOrStage", EQUALS_IC, "EGG");<br>
 * QuerySpec query = new QuerySpec();<br>
 * query.addCondition(condition1);<br>
 * query.addCondition(condition2);<br>
 * query.setLogicalOperator(OR);<br>
 * query.sortBy("unitID", false);<br>
 * query.setFrom(100);<br>
 * query.setSize(50);
 * </code>
 * </p>
 * <p>
 * <b>Finally, the JSON representation of the {@code QuerySpec} (needed for the
 * REST API):</b>
 * </p>
 * <p>
 * <code>
 * {<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"conditions" : [ {<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"field" : "gatheringEvent.gatheringPersons.fullName",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"operator" : "LIKE",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : "burg",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"and" : [ {<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"field" : "unitID",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"operator" : "EQUALS",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : "ZMA.MAM.100"<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} ]<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}, {<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"field" : "phaseOrStage",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"operator" : "EQUALS_IC",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"value" : "EGG"<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} ],<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"logicalOperator" : "OR",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sortFields" : [ {<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path" : "unitID",<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ascending" : false<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} ],<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"from" : 100,<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"size" : 50<br>
 * }
 * </code>
 * </p>
 * 
 * @author Ayco Holleman
 *
 */
public class SearchSpec extends AbstractSearchSpec<SearchCondition> {

	private boolean constantScore;

	public boolean isConstantScore()
	{
		return constantScore;
	}

	public void setConstantScore(boolean constantScore)
	{
		this.constantScore = constantScore;
	}

}
