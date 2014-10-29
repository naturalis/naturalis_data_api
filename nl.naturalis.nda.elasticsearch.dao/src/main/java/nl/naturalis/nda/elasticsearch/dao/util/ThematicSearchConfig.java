package nl.naturalis.nda.elasticsearch.dao.util;

import java.io.IOException;
import java.util.Properties;

public class ThematicSearchConfig {

	private static ThematicSearchConfig instance;


	public static ThematicSearchConfig getInstance()
	{
		if (instance == null) {
			instance = new ThematicSearchConfig();
		}
		return instance;
	}

	private final Properties props;


	private ThematicSearchConfig()
	{
		props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("/thematic-search.properties"));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public String getThematicCollectionId(String collectionCode)
	{
		try {
			return props.getProperty(collectionCode + ".id");
		}
		catch (Throwable t) {
			throw new RuntimeException(String.format("Unable to retrieve id for collection \"%s\"", collectionCode), t);
		}
	}


	public String getThematicCollectionUserId(String collectionCode)
	{
		try {
			return props.getProperty(collectionCode + ".user-id");
		}
		catch (Throwable t) {
			throw new RuntimeException(String.format("Unable to retrieve id for collection \"%s\"", collectionCode), t);
		}
	}


	public String getThematicCollectionTypes(String collectionCode)
	{
		try {
			return props.getProperty(collectionCode + ".types");
		}
		catch (Throwable t) {
			throw new RuntimeException(String.format("Unable to retrieve id for collection \"%s\"", collectionCode), t);
		}
	}

}
