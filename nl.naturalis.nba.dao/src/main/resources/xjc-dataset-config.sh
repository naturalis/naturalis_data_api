# After you have executed this script you MUST make the following
# modifications to the DataSetXmlConfig class:
# [1] Annotate the class with: @XmlRootElement(name = "dataset-config")
# [2] Set name attribute of @XmlType annotation to empty string
rm -rf ../java/nl/naturalis/nba/dao/format/config/*XmlConfig.java
xjc -d ../java -p nl.naturalis.nba.dao.format.config dataset-config.xsd
