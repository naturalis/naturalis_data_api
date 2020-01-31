package nl.naturalis.nba.etl.nsr.model;

public class Status {

    private String status;
    private String reference_title;
    private String expert_name;
    private String organisation_name;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReference_title() {
        return reference_title;
    }

    public void setReference_title(String reference_title) {
        this.reference_title = reference_title;
    }

    public String getExpert_name() {
        return expert_name;
    }

    public void setExpert_name(String expert_name) {
        this.expert_name = expert_name;
    }

    public String getOrganisation_name() {
        return organisation_name;
    }

    public void setOrganisation_name(String organisation_name) {
        this.organisation_name = organisation_name;
    }

}
