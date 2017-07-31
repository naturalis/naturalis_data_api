package nl.naturalis.nba.etl;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.utils.ConfigObject;

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
	public void bootstrap(String... documentTypes)
	{
		if (ConfigObject.isEnabled(ETLConstants.SYSPROP_DRY_RUN)) {
			logger.info("Disabled in dry run: {}", getClass().getName());
			return;
		}
		if (documentTypes.length == 0) {
			throw new IllegalArgumentException("At least one document type name required");
		}
		LinkedHashSet<String> docTypeList = new LinkedHashSet<>(Arrays.asList(documentTypes));
		if (docTypeList.contains("all")) {
			if (docTypeList.size() != 1) {
				throw new IllegalArgumentException(
						"\"all\" cannot be combined with other arguments");
			}
			ESUtil.deleteAllIndices();
			ESUtil.createAllIndices();
		}
		else {
			for (String docType : docTypeList) {
				DocumentType<?> dt = DocumentType.forName(docType);
				ESUtil.deleteIndex(dt);
				ESUtil.createIndex(dt);
			}
		}
	}
}
