package nl.naturalis.nba.etl.nsr.model;

@SuppressWarnings("unused")
public class Author {

    private String subject_id;
    private String name;

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }
}
