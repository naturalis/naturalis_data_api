package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

public class TaxonTransfer {

    private TaxonTransfer() {
        // Only static method in transfer objects
    }

    public static Taxon transfer(ESTaxon esTaxon) {
        Taxon taxon = new Taxon();
        taxon.setSourceSystem(esTaxon.getSourceSystem());
        taxon.setSourceSystemId(esTaxon.getSourceSystemId());
        taxon.setSourceSystemParentId(esTaxon.getSourceSystemParentId());
        taxon.setTaxonRank(esTaxon.getTaxonRank());
        taxon.setAcceptedName(esTaxon.getAcceptedName());
        taxon.setDefaultClassification(esTaxon.getDefaultClassification());
        taxon.setSystemClassification(esTaxon.getSystemClassification());
        taxon.setSynonyms(esTaxon.getSynonyms());
        taxon.setVernacularNames(esTaxon.getVernacularNames());
        taxon.setDescriptions(esTaxon.getDescriptions());
        taxon.setReferences(esTaxon.getReferences());
        taxon.setExperts(esTaxon.getExperts());
        return taxon;
    }
}
