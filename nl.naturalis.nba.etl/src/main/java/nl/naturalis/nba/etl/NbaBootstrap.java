package nl.naturalis.nba.etl;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;

/**
 * Class managing the creation of the indices used by the NBA.
 * 
 * @author Ayco Holleman
 * 
 */
public class NbaBootstrap {

	public static void main(String[] args)
	{
		try {
			new NbaBootstrap().bootstrap(args);
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NbaBootstrap.class);

	/**
	 * Creates or re-creates indices hosting the specified document types. Each
	 * element in the specified array must be the name of a
	 * {@link DocumentType#getName() document type}. Casing is ignored. You can
	 * also provide a single-element array containing the value "*" or "all".
	 * This will cause all indices the be (re-)created.
	 * 
	 * @param documentTypes
	 */
	@SuppressWarnings("static-method")
	public void bootstrap(String... documentTypes)
	{
		if (documentTypes.length == 0) {
			throw new IllegalArgumentException("At least one document type name required");
		}
		LinkedHashSet<String> indices = new LinkedHashSet<>(Arrays.asList(documentTypes));
		if (indices.contains("*")) {
			if (indices.size() != 1) {
				throw new IllegalArgumentException("\"*\" cannot be combined with other arguments");
			}
			ESUtil.deleteAllIndices();
			ESUtil.createAllIndices();
		}
		else if (indices.contains("all")) {
			if (indices.size() != 1) {
				throw new IllegalArgumentException(
						"\"all\" cannot be combined with other arguments");
			}
			ESUtil.deleteAllIndices();
			ESUtil.createAllIndices();
		}
		else {
			for (String index : indices) {
				DocumentType<?> dt = DocumentType.forName(index);
				ESUtil.deleteIndex(dt);
				ESUtil.createIndex(dt);
			}
		}
	}
}
