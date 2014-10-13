package nl.naturalis.nda.elasticsearch.dao.dao;

/**
 * TODO javadoc
 *
 * @author Roberto van der Linden
 */
public class TestDocumentCreator {

    public String createSpecimenSource(String unitId, String fullname) {
        return "{" +
                "\"sourceSystem\" : {" +
                "\"code\" : \"BRAHMS\"," +
                "\"name\" : \"Brahms\"" +
                "}," +
                "\"sourceSystemId\" : \"" + unitId + "\"," +
                "\"unitID\" : \"" + unitId + "\"," +
                "\"unitGUID\" : null," +
                "\"assemblageID\" : \"BRAHMS-577339.000000\"," +
                "\"sourceInstitutionID\" : null," +
                "\"recordBasis\" : \"PreservedSpecimen\"," +
                "\"kindOfUnit\" : null," +
                "\"collectionType\" : null," +
                "\"typeStatus\" : null," +
                "\"sex\" : null," +
                "\"phaseOrStage\" : null," +
                "\"title\" : null," +
                "\"notes\" : \"Tree, diameter 25 cm. bole 20 m. Crown silt roots on flat flying buttress 1.5 m. high. Inner bark redbrown 1 cm. fibrous. Sapwood white and yellow flush.\"," +
                "\"preparationType\" : null," +
                "\"numberOfSpecimen\" : 0," +
                "\"fromCaptivity\" : false," +
                "\"objectPublic\" : false," +
                "\"multiMediaPublic\" : false," +
                "\"acquiredFrom\" : null," +
                "\"gatheringEvent\" : {" +
                "\"projectTitle\" : null," +
                "\"worldRegion\" : \"Asia\"," +
                "\"continent\" : \"Asia\"," +
                "\"country\" : \"Malaysia/Sabah\"," +
                "\"iso3166Code\" : null," +
                "\"provinceState\" : \"Borneo\"," +
                "\"island\" : null," +
                "\"locality\" : null," +
                "\"city\" : null," +
                "\"sublocality\" : null," +
                "\"localityText\" : null," +
                "\"dateTimeBegin\" : -299725200000," +
                "\"dateTimeEnd\" : -299725200000," +
                "\"method\" : null," +
                "\"altitude\" : null," +
                "\"altitudeUnifOfMeasurement\" : null," +
                "\"depth\" : null," +
                "\"depthUnitOfMeasurement\" : null," +
                "\"gatheringPersons\" : [{" +
                "\"agentText\" : null," +
                "\"fullName\" : \"" + fullname + "\"," +
                "\"organization\" : null" +
                "}" +
                "]," +
                "\"gatheringOrganizations\" : null," +
                "\"siteCoordinates\" : [{" +
                "\"longitudeDecimal\" : 0," +
                "\"latitudeDecimal\" : 0," +
                "\"point\" : {" +
                "\"type\" : \"POINT\"," +
                "\"coordinates\" : [" +
                "0," +
                "0" +
                "]" +
                "}" +
                "}" +
                "]" +
                "}," +
                "\"identifications\" : [{" +
                "\"taxonRank\" : \"species\"," +
                "\"scientificName\" : {" +
                "\"fullScientificName\" : \"Xylopia ferruginea (Hook.f. & Thomson) Baill.\"," +
                "\"taxonomicStatus\" : null," +
                "\"genusOrMonomial\" : \"Xylopia\"," +
                "\"subgenus\" : null," +
                "\"specificEpithet\" : \"ferruginea\"," +
                "\"infraspecificEpithet\" : null," +
                "\"infraspecificMarker\" : null," +
                "\"nameAddendum\" : null," +
                "\"authorshipVerbatim\" : \"(Hook.f. & Thomson) Baill.\"," +
                "\"author\" : null," +
                "\"year\" : null," +
                "\"references\" : null," +
                "\"experts\" : null" +
                "}," +
                "\"defaultClassification\" : {" +
                "\"kingdom\" : \"Plantae\"," +
                "\"phylum\" : null," +
                "\"className\" : \"Magnoliopsida\"," +
                "\"order\" : \"Magnoliales\"," +
                "\"superFamily\" : null," +
                "\"family\" : \"Annonaceae\"," +
                "\"genus\" : \"Xylopia\"," +
                "\"subgenus\" : null," +
                "\"specificEpithet\" : \"ferruginea\"," +
                "\"infraspecificEpithet\" : null," +
                "\"infraspecificRank\" : null" +
                "}," +
                "\"systemClassification\" : [{" +
                "\"rank\" : \"kingdom\"," +
                "\"name\" : \"Plantae\"" +
                "}, {" +
                "\"rank\" : \"order\"," +
                "\"name\" : \"Magnoliales\"" +
                "}, {" +
                "\"rank\" : \"family\"," +
                "\"name\" : \"Annonaceae\"" +
                "}, {" +
                "\"rank\" : \"genus\"," +
                "\"name\" : \"Xylopia\"" +
                "}, {" +
                "\"rank\" : \"species\"," +
                "\"name\" : \"ferruginea\"" +
                "}" +
                "]," +
                "\"vernacularNames\" : null," +
                "\"identificationQualifiers\" : null," +
                "\"dateIdentified\" : 1398895200000," +
                "\"identifiers\" : [{" +
                "\"agentText\" : \"Johnson, D.M.\"" +
                "}" +
                "]," +
                "\"preferred\" : false," +
                "\"verificationStatus\" : null," +
                "\"rockType\" : null," +
                "\"associatedFossilAssemblage\" : null," +
                "\"rockMineralUsage\" : null," +
                "\"associatedMineralName\" : null," +
                "\"remarks\" : null" +
                "}" +
                "]" +
                "}";
    }

    public String createTaxonSource() {
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
                "      \"genusOrMonomial\": \"Hyphomonas\"," +
                "      \"subgenus\": null," +
                "      \"specificEpithet\": \"oceanitis\"," +
                "      \"infraspecificEpithet\": \"\"," +
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
                "   \"synonyms\": null," +
                "   \"vernacularNames\": null," +
                "   \"descriptions\": null," +
                "   \"references\": null," +
                "   \"experts\": null" +
                "}";
    }

}
