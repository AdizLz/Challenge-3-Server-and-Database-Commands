package org.example.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Maps the general search response (engine=google_scholar)
 */
public class SearchResponse {

    // This field contains the list of authors found
    @SerializedName("author_results")
    private List<AuthorSearchResult> authorResults;

    public List<AuthorSearchResult> getAuthorResults() {
        return authorResults;
    }

    /**
     * Inner class that maps each individual author profile in the list
     */
    public static class AuthorSearchResult {
        private String name;
        private String affiliation;
        // Maps the number of citations for each profile
        @SerializedName("cited_by")
        private int citedBy;

        public String getName() {
            return name;
        }

        public String getAffiliation() {
            return affiliation;
        }

        public int getCitedBy() {
            return citedBy;
        }
    }
}
