package nl.naturalis.nba.etl.nsr.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@SuppressWarnings("unused")
public class Description {

    private String taxon_id;
    private String title;
    private String text;
    private String language;
    private OffsetDateTime last_change;
    private Author[] authors;

    public String getTaxon_id() {
        return taxon_id;
    }

    public void setTaxon_id(String taxon_id) {
        this.taxon_id = taxon_id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language.trim();
    }

    public OffsetDateTime getLast_change() {
        return last_change;
    }

    public void setLast_change(String last_change) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(last_change.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.last_change = OffsetDateTime.of(dateTime, ZoneOffset.ofHours(1));
        } catch (DateTimeParseException ignored) {}
    }

    public Author[] getAuthors() {
        return authors;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
    }
}
