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
			System.exit(1);
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NbaBootstrap.class);

	/**
	 * Creates the Elasticsearch indices hosting the specified NBA document
	 * types. Each element in the specified array must be the name of an
	 * {@link DocumentType#getName() NBA document type}. Casing is ignored. You
	 * can also pass a single, special value to this method: "all". This will
	 * cause all indices the be created. If any index already existed, it will
	 * be deleted first.
	 * 
	 * @param documentTypes The document types
	 */
	public void bootstrap(String... documentTypes)
	{
		if (ConfigObject.isEnabled(ETLConstants.SYSPROP_DRY_RUN)) {
			logger.info("Bootstrap skipped dry run mode");
			return;
		}
		if (documentTypes.length == 0) {
			throw new IllegalArgumentException("At least one document type name required");
		}
		LinkedHashSet<String> docTypeList = new LinkedHashSet<>(Arrays.asList(documentTypes));
		if (docTypeList.contains("--all")) {
			if (docTypeList.size() != 1) {
				String msg = "Option --all cannot be combined with other arguments";
				throw new IllegalArgumentException(msg);
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
