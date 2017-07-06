package nl.naturalis.nba.etl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;

/**
 * Deletes on or more indices managed by the NBA.
 * 
 * @author Ayco Holleman
 * 
 */
public class NbaDeleteIndex {

	public static void main(String[] args)
	{
		try {
			Set<String> argList = new LinkedHashSet<>(Arrays.asList(args));
			if (argList.contains("--all")) {
				if (argList.size() != 1) {
					String msg = "--all switch cannot be combined with other arguments";
					throw new IllegalArgumentException(msg);
				}
				ESUtil.deleteAllIndices();
			}
			else if (argList.contains("--raw")) {
				argList.remove("--raw");
				if (argList.size() == 0) {
					String msg = "At least one index name required with --raw switch";
					throw new IllegalArgumentException(msg);
				}
				for (String name : argList) {
					ESUtil.deleteIndex(name);
				}
			}
			else {
				for (String docType : argList) {
					DocumentType<?> dt = DocumentType.forName(docType);
					ESUtil.deleteIndex(dt);
				}
			}
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NbaDeleteIndex.class);

}
