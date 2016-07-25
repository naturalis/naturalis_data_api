########################################################################
#          Example of how to configure the DwCA generator.
#
# The keys specify the CSV headers. The values specify the following:
# [1]  If the value does not start with an asterisk (*) or percentage
#      sign (%), it specifies the full path of an  Elasticsearch field.
#      Array access can be achieved by adding the array index after the
#      name of the field that represents the array.
# [2]  If the value starts with an asterisk (*), it specifies a constant
#      (a.k.a. default) value. Everything _following_ the asterisk is
#      used as the default value for the CSV field. If you want to
#      include a CSV field, but leave it empty, just specify an asterisk
#      without anything after it.
# [3]  If the value is the percentage sign (%), it specifies a
#      calculated value. The DwCA generator will apply custom logic to
#      arrive at the value for the CSV field. Of course, this only works
#      if there actually is custom logic defined for that field.
#
# Both keys and values are whitespace-trimmed before being processed.
########################################################################
id = unitID
scientificName = identifications.0.scientificName.fullScientificName
scientificNameAuthorship = identifications.0.scientificName.authorshipVerbatim
basisOfRecord = recordBasis
typeStatus = typeStatus
lifeStage = phaseOrStage
sex = sex
preparations = preparationType
taxonRank = identifications.0.taxonRank
kingdom = identifications.0.defaultClassification.kingdom
phylum = identifications.0.defaultClassification.phylum
class = identifications.0.defaultClassification.className
order = identifications.0.defaultClassification.order
family = identifications.0.defaultClassification.family
genus = identifications.0.defaultClassification.genus
subgenus = identifications.0.defaultClassification.subgenus
specificEpithet = identifications.0.defaultClassification.specificEpithet
infraspecificEpithet = identifications.0.defaultClassification.infraspecificEpithet
collectionCode = collectionType
locality = gatheringEvent.locality
continent = gatheringEvent.continent
country = gatheringEvent.country
county = *
dateIdentified = identifications.0.dateIdentified
decimalLatitude = gatheringEvent.siteCoordinates.0.latitudeDecimal
decimalLongitude = gatheringEvent.siteCoordinates.0.longitudeDecimal
verbatimDepth = *
eventDate = gatheringEvent.dateTimeBegin
verbatimEventDate = *
institutionCode = *Naturalis
catalogNumber = sourceSystemId
occurrenceID = unitGUID
