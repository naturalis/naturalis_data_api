package nl.naturalis.nda.elasticsearch.dao.dao;

/**
 * Class to create documents for testing purposes.
 *
 * @author Roberto van der Linden
 */
public class TestDocumentCreator {

    public String createSpecimenSource(String unitId, String fullname, String kingdom, final String genusOrMonomial,
                                       final String specificEpithet, final String infraspecificEpithet) {
        if ("".equals(genusOrMonomial) || "".equals(specificEpithet) || "".equals(infraspecificEpithet)) {
            throw new IllegalArgumentException("no empty string allowed as argument values - use null");
        }
        return "{\"sourceSystem\": {\n" +
                "       \"code\": \"BRAHMS\",\n" +
                "       \"name\": \"Brahms\"\n" +
                "    },\n" +
                "    \"sourceSystemId\": \"" + unitId + "\",\n" +
                "    \"unitID\": \"" + unitId + "\",\n" +
                "    \"unitGUID\": null,\n" +
                "    \"assemblageID\": \"BRAHMS-577339.000000\",\n" +
                "    \"sourceInstitutionID\": null,\n" +
                "    \"recordBasis\": \"PreservedSpecimen\",\n" +
                "    \"kindOfUnit\": null,\n" +
                "    \"collectionType\": null,\n" +
                "    \"typeStatus\": null,\n" +
                "    \"sex\": null,\n" +
                "    \"phaseOrStage\": null,\n" +
                "    \"title\": null,\n" +
                "    \"notes\": \"Tree, diameter 25 cm. bole 20 m. Crown silt roots on flat flying buttress 1.5 m. high. Inner bark redbrown 1 cm. fibrous. Sapwood white and yellow flush.\",\n" +
                "    \"preparationType\": null,\n" +
                "    \"numberOfSpecimen\": 0,\n" +
                "    \"fromCaptivity\": false,\n" +
                "    \"objectPublic\": false,\n" +
                "    \"multiMediaPublic\": false,\n" +
                "    \"acquiredFrom\": null,\n" +
                "    \"gatheringEvent\": {\n" +
                "       \"projectTitle\": null,\n" +
                "       \"worldRegion\": \"Asia\",\n" +
                "       \"continent\": \"Asia\",\n" +
                "       \"country\": \"Malaysia/Sabah\",\n" +
                "       \"iso3166Code\": null,\n" +
                "       \"provinceState\": \"Borneo\",\n" +
                "       \"island\": null,\n" +
                "       \"locality\": null,\n" +
                "       \"city\": null,\n" +
                "       \"sublocality\": null,\n" +
                "       \"localityText\": null,\n" +
                "       \"dateTimeBegin\": -299725200000,\n" +
                "       \"dateTimeEnd\": -299725200000,\n" +
                "       \"method\": null,\n" +
                "       \"altitude\": null,\n" +
                "       \"altitudeUnifOfMeasurement\": null,\n" +
                "       \"depth\": null,\n" +
                "       \"depthUnitOfMeasurement\": null,\n" +
                "       \"gatheringPersons\": [\n" +
                "          {\n" +
                "             \"agentText\": null,\n" +
                "             \"fullName\": \"" + fullname + "\",\n" +
                "             \"organization\": null\n" +
                "          }\n" +
                "       ],\n" +
                "       \"gatheringOrganizations\": null,\n" +
                "       \"siteCoordinates\": [\n" +
                "          {\n" +
                "             \"longitudeDecimal\": 12.746355,\n" +
                "             \"latitudeDecimal\": 14.584877,\n" +
                "             \"point\": {\n" +
                "                \"type\": \"POINT\",\n" +
                "                \"coordinates\": [\n" +
                "                   14.584877,\n" +
                "                   12.746355\n" +
                "                ]\n" +
                "             }\n" +
                "          }\n" +
                "       ]\n" +
                "    },\n" +
                "    \"identifications\": [\n" +
                "       {\n" +
                "          \"taxonRank\": \"species\",\n" +
                "          \"scientificName\": {\n" +
                "             \"fullScientificName\": \"Xylopia ferruginea (Hook.f. & Thomson) Baill.\",\n" +
                "             \"taxonomicStatus\": null,\n" +
                "             \"genusOrMonomial\": \"" + genusOrMonomial + "\",\n" +
                "             \"subgenus\": null,\n" +
                "             \"specificEpithet\": \"" + specificEpithet + "\",\n" +
                "             \"infraspecificEpithet\": \"" + infraspecificEpithet + "\",\n" +
                "             \"infraspecificMarker\": null,\n" +
                "             \"nameAddendum\": null,\n" +
                "             \"authorshipVerbatim\": \"(Hook.f. & Thomson) Baill.\",\n" +
                "             \"author\": null,\n" +
                "             \"year\": null,\n" +
                "             \"references\": null,\n" +
                "             \"experts\": null\n" +
                "          },\n" +
                "          \"defaultClassification\": {\n" +
                "             \"kingdom\": \"" + kingdom + "\",\n" +
                "             \"phylum\": null,\n" +
                "             \"className\": \"Magnoliopsida\",\n" +
                "             \"order\": \"Magnoliales\",\n" +
                "             \"superFamily\": null,\n" +
                "             \"family\": \"Annonaceae\",\n" +
                "             \"genus\": \"Xylopia\",\n" +
                "             \"subgenus\": null,\n" +
                "             \"specificEpithet\": \"ferruginea\",\n" +
                "             \"infraspecificEpithet\": null,\n" +
                "             \"infraspecificRank\": null\n" +
                "          },\n" +
                "          \"systemClassification\": [\n" +
                "             {\n" +
                "                \"rank\": \"kingdom\",\n" +
                "                \"name\": \"Plantae\"\n" +
                "             },\n" +
                "             {\n" +
                "                \"rank\": \"order\",\n" +
                "                \"name\": \"Magnoliales\"\n" +
                "             },\n" +
                "             {\n" +
                "                \"rank\": \"family\",\n" +
                "                \"name\": \"Annonaceae\"\n" +
                "             },\n" +
                "             {\n" +
                "                \"rank\": \"genus\",\n" +
                "                \"name\": \"Xylopia\"\n" +
                "             },\n" +
                "             {\n" +
                "                \"rank\": \"species\",\n" +
                "                \"name\": \"ferruginea\"\n" +
                "             }\n" +
                "          ],\n" +
                "          \"vernacularNames\": null,\n" +
                "          \"identificationQualifiers\": null,\n" +
                "          \"dateIdentified\": 1398895200000,\n" +
                "          \"identifiers\": [\n" +
                "             {\n" +
                "                \"agentText\": \"Johnson, D.M.\"\n" +
                "             }\n" +
                "          ],\n" +
                "          \"preferred\": false,\n" +
                "          \"verificationStatus\": null,\n" +
                "          \"rockType\": null,\n" +
                "          \"associatedFossilAssemblage\": null,\n" +
                "          \"rockMineralUsage\": null,\n" +
                "          \"associatedMineralName\": null,\n" +
                "          \"remarks\": null\n" +
                "       }\n" +
                "    ]\n" +
                " }";
    }

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
