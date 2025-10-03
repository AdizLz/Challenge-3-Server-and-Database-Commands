package org.example.model;
import java.util.List;

/**
 * Represents the author's profile data, including essential information
 * like name, affiliation, and citation count.
 */
public class Author {
    private String authorId;
    private String name;
    private String affiliation;
    private int totalCitations;
    private List<Publication> publications;

    /**
     * Required empty constructor for Gson serialization/deserialization.
     */
    public Author() {}

    /**
     * Constructor used by the Controller's mock or when transforming search results.
     */
    public Author(String authorId, String name, String affiliation, int totalCitations) {
        this.authorId = authorId;
        this.name = name;
        this.affiliation = affiliation;
        this.totalCitations = totalCitations;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public int getTotalCitations() { return totalCitations; }

    /**
     * Provides citation count, used for compatibility with the View's display logic.
     */
    public int getCitationCount() { return totalCitations; }
    public void setTotalCitations(int totalCitations) { this.totalCitations = totalCitations; }

    public List<Publication> getPublications() { return publications; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }

    // Optional method for easy printing of object details (debugging)
    @Override
    public String toString() {
        return "Name: " + name + ", Affiliation: " + affiliation + ", Citations: " + totalCitations;
    }
}