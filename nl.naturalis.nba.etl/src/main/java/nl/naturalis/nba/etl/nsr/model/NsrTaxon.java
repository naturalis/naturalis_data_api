package nl.naturalis.nba.etl.nsr.model;

public class NsrTaxon {

    private String name;
    private String rank;
    private String nsr_id;
    private String nsr_id_parent;
    private String url;
    private Classification[] classification;
    private Description[] description;
    private Name[] names;
    private Image[] images;
    private Status status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.strip();
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {

        this.rank = rank.strip();
    }

    public String getNsr_id() {
        return nsr_id;
    }

    public void setNsr_id(String nsr_id) {

        this.nsr_id = nsr_id.strip();
    }

    public String getNsr_id_parent() {
        return nsr_id_parent;
    }

    public void setNsr_id_parent(String nsr_id_parent) {

        this.nsr_id_parent = nsr_id_parent.strip();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {

        this.url = url.strip();
    }

    public Classification[] getClassification() {
        return classification;
    }

    public void setClassification(Classification[] classification) {

        this.classification = classification;
    }

    public Description[] getDescription() {
        return description;
    }

    public void setDescription(Description[] description) {

        this.description = description;
    }

    public Name[] getNames() {
        return names;
    }

    public void setNames(Name[] names) {

        this.names = names;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {

        this.images = images;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }
}

