package org.example.model;

/**
 * Model class representing a publication from Google Scholar.
 */
public class Publication {
    private String title;
    private String link;
    private int citations;

    // Empty constructor (required for Gson)
    public Publication() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }
}
