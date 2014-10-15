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
                "   \"sourceSystem\": {" +
                "      \"code\": \"COL\"," +
                "      \"name\": \"Catalogue Of Life\"" +
                "   }," +
                "   \"sourceSystemId\": \"4259353\"," +
                "   \"sourceSystemParentId\": null," +
                "   \"taxonRank\": null," +
                "   \"acceptedName\": {" +
                "      \"fullScientificName\": \"Hyphomonas oceanitis Weiner et al. 1985\"," +
                "      \"taxonomicStatus\": null," +
                "      \"genusOrMonomial\": \"" + genusOrMonomial + "\"," +
                "      \"subgenus\": null," +
                "      \"specificEpithet\": \"" + specificEpithet + "\"," +
                "      \"infraspecificEpithet\": \"" + infraspecificEpithet + "\"," +
                "      \"infraspecificMarker\": null," +
                "      \"nameAddendum\": null," +
                "      \"authorshipVerbatim\": \"Weiner et al. 1985\"," +
                "      \"author\": null," +
                "      \"year\": null," +
                "      \"references\": null," +
                "      \"experts\": null" +
                "   }," +
                "   \"defaultClassification\": {" +
                "      \"kingdom\": \"Bacteria\"," +
                "      \"phylum\": \"Proteobacteria\"," +
                "      \"className\": \"Alphaproteobacteria\"," +
                "      \"order\": \"Rhodobacterales\"," +
                "      \"superFamily\": \"\"," +
                "      \"family\": \"Rhodobacteraceae\"," +
                "      \"genus\": \"Hyphomonas\"," +
                "      \"subgenus\": \"\"," +
                "      \"specificEpithet\": \"oceanitis\"," +
                "      \"infraspecificEpithet\": \"\"," +
                "      \"infraspecificRank\": null" +
                "   }," +
                "   \"systemClassification\": [" +
                "      {" +
                "         \"rank\": \"kingdom\"," +
                "         \"name\": \"Bacteria\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"phylum\"," +
                "         \"name\": \"Proteobacteria\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"class\"," +
                "         \"name\": \"Alphaproteobacteria\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"order\"," +
                "         \"name\": \"Rhodobacterales\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"superfamily\"," +
                "         \"name\": \"\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"family\"," +
                "         \"name\": \"Rhodobacteraceae\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"genus\"," +
                "         \"name\": \"Hyphomonas\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"subgenus\"," +
                "         \"name\": \"\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"specificEpithet\"," +
                "         \"name\": \"oceanitis\"" +
                "      }," +
                "      {" +
                "         \"rank\": \"infraspecificEpithet\"," +
                "         \"name\": \"\"" +
                "      }" +
                "   ]," +
                "   \"synonyms\": {" +
                "       \"genusOrMonomial\": \"genusOrMonomialSynonyms\"," +
                "       \"specificEpithet\": \"specificEpithetSynonyms\"," +
                "       \"infraspecificEpithet\": null" +
                "   }," +
                "   \"vernacularNames\": {" +
                "       \"name\": \"henkie\"" +
                "   }," +
                "   \"descriptions\": null," +
                "   \"references\": null," +
                "   \"experts\": null" +
                "}";
    }

}
