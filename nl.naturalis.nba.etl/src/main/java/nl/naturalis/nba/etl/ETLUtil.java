package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_TEST_GENERA;
import static nl.naturalis.nba.utils.StringUtil.zpad;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * Utility class providing common functionality used throughout this library.
 * 
 * @author Ayco Holleman
 *
 */
public final class ETLUtil {

  private static final Logger logger = getLogger(Loader.class);

  private static final URIBuilder purlBuilder;
  private static final String purlSpecimenPath;

  static {
    purlBuilder = getPurlBuilder();
    // TODO: why is this???
//    if (purlBuilder.getPath() != null) {
//      purlSpecimenPath = purlBuilder.getPath() + "/naturalis/specimen/";
//    } else {
//      purlSpecimenPath = "/naturalis/specimen/";
//    }
    purlSpecimenPath = "/naturalis/specimen/";
  }

   public ETLUtil() {}

  /**
   * Get root cause of the specified {@code Throwable}. Returns the {@code Throwable} itself if it
   * doesn't have a cause.
   * 
   * @param t
   * @return
   */
  public static Throwable getRootCause(Throwable t) {
    while (t.getCause() != null) {
      t = t.getCause();
    }
    return t;
  }

  /**
   * Logs a nice message about how long an import program took.
   * 
   * @param logger The logger to log to
   * @param cls The main class of the import program
   * @param start The start of the program
   */
  public static void logDuration(Logger logger, Class<?> cls, long start) {
    logger.info(cls.getSimpleName() + " took " + getDuration(start));
  }

  /**
   * Get the duration between {@code start} and now, formatted as HH:mm:ss.
   * 
   * @param start
   * @return
   */
  public static String getDuration(long start) {
    return getDuration(start, System.currentTimeMillis());
  }

  /**
   * Get the duration between {@code start} and {@code end}, formatted as HH:mm:ss.
   * 
   * @param start
   * @param end
   * @return
   */
  public static String getDuration(long start, long end) {
    int millis = (int) (end - start);
    int hours = millis / (60 * 60 * 1000);
    millis = millis % (60 * 60 * 1000);
    int minutes = millis / (60 * 1000);
    millis = millis % (60 * 1000);
    int seconds = millis / 1000;
    return zpad(hours, 2, ":") + zpad(minutes, 2, ":") + zpad(seconds, 2);
  }

  public static String getSpecimenPurl(String unitID) {
    try {
      purlBuilder.setPath(purlSpecimenPath + unitID);
      return purlBuilder.build().toString();
    } catch (URISyntaxException e) {
      throw new ETLRuntimeException(e);
    }
  }

  public static Logger getLogger(Class<?> forClass) {
    return ETLRegistry.getInstance().getLogger(forClass);
  }

  public static <T extends IDocumentObject> void truncate(DocumentType<T> dt) {
    if (ConfigObject.isEnabled(ETLConstants.SYSPROP_DRY_RUN)) {
      logger.info("Truncate skipped dry run mode");
      return;
    }
    ESUtil.truncate(dt);
  }

  public static <T extends IDocumentObject> void truncate(DocumentType<T> dt, SourceSystem ss) {
    if (ConfigObject.isEnabled(ETLConstants.SYSPROP_DRY_RUN)) {
      logger.info("Truncate skipped dry run mode");
      return;
    }
    ESUtil.truncate(dt, ss);
  }

  public static String[] getTestGenera() {
    String s = System.getProperty(SYSPROP_TEST_GENERA);
    if (s == null || s.length() == 0) {
      return null;
    }
    String[] testGenera = s.split(",");
    for (int i = 0; i < testGenera.length; i++) {
      testGenera[i] = testGenera[i].trim().toLowerCase();
    }
    return testGenera;
  }
  
  private static URIBuilder getPurlBuilder() {
    String value = null;
    try {
      value = DaoRegistry.getInstance().getConfiguration().get("purl.baseurl");
      if (value == null || value.trim().isEmpty()) {
        value = PURL_SERVER_BASE_URL;
      }
      return new URIBuilder(value);
    } catch (URISyntaxException e) {
      String fmt = "Could not create URIBuilder for PURL base URL \"%s\": %s";
      String msg = String.format(fmt, value, e.getMessage());
      throw new ETLRuntimeException(msg);
    }
  }

}
