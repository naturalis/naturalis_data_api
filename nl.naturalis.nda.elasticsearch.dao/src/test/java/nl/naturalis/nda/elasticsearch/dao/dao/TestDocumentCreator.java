package nl.naturalis.nda.elasticsearch.dao.dao;

/**
 * Class to create documents for testing purposes.
 *
 * @author Roberto van der Linden
 */
public class TestDocumentCreator {

    public String createTaxonSource(final String genusOrMonomial, final String specificEpithet,
                                    final String infraspecificEpithet) {
        if ("".equals(genusOrMonomial) || "".equals(specificEpithet) || "".equals(infraspecificEpithet)) {
            throw new IllegalArgumentException("no empty string allowed as argument values - use null");
        }
        return "{" +
                "}";
    }

}
