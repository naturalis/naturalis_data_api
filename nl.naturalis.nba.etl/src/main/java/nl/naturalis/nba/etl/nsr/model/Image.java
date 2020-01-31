package nl.naturalis.nba.etl.nsr.model;

public class Image {

    private String taxon_id;
    private String file_name;
    private String mime_type;
    private String photographer_name;
    private String date_taken;
    private String short_description;
    private String geography;
    private String copyright;
    private String maker_adress;
    private String licence;
    private String licence_type;
    private String url;

    public String getTaxon_id() {
        return taxon_id;
    }

    public void setTaxon_id(String taxon_id) {
        this.taxon_id = taxon_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getPhotographer_name() {
        return photographer_name;
    }

    public void setPhotographer_name(String photographer_name) {
        this.photographer_name = photographer_name;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getMaker_adress() {
        return maker_adress;
    }

    public void setMaker_adress(String maker_adress) {
        this.maker_adress = maker_adress;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getLicence_type() {
        return licence_type;
    }

    public void setLicence_type(String licence_type) {
        this.licence_type = licence_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
