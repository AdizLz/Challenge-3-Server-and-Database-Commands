package org.example.controller;

import org.example.view.AuthorView;
import org.example.model.Author;
// --- Libraries needed for REAL JSON parsing ---
import com.google.gson.Gson;
import org.example.model.SearchResponse;
// --- HTTP Libraries ---
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors; // For list transformation

public class AuthorController {

    // 🔑 YOUR LAST WORKING API KEY
    private final String API_KEY = "bd83c6ba82b4095b98b58abc29171e382d9449f163b85ea9416d95f594d0503e";
    private final String BASE_URL = "https://serpapi.com/search";

    // Reference to the View (for MVC interaction)
    private final AuthorView view;

    public AuthorController(AuthorView view) {
        this.view = view;
    }

    /**
     * Starts the search process: gets the query, searches, and displays results.
     */
    public void startSearchProcess() {
        try {
            String query = view.getSearchQuery();

            if (query == null || query.isBlank()) {
                view.displayError("Search query cannot be empty.");
                return;
            }

            List<Author> results = searchAuthors(query);
            view.displayResults(results);

        } catch (IOException e) {
            view.displayError("I/O Error (Network): " + e.getMessage());
        } catch (Exception e) {
            view.displayError("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Executes the API query and parses the JSON response into a list of Authors.
     */
    private List<Author> searchAuthors(String query) throws IOException {

        // Use engine=google_scholar and filter by 'author:' for relevant results
        String url = String.format(
                "%s?engine=google_scholar&q=author:%s&api_key=%s&hl=en&gl=us",
                BASE_URL,
                query.replace(" ", "+"),
                API_KEY
        );

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                int status = response.getStatusLine().getStatusCode();

                if (status != 200) {
                    view.displayError("HTTP Error: " + status + " while querying the API.");
                    return Collections.emptyList();
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());

                // 💡 REAL JSON PARSING (Using SearchResponse.java and Gson)
                Gson gson = new Gson();
                SearchResponse searchResponse = gson.fromJson(jsonResponse, SearchResponse.class);

                // Transform SearchResponse results into the Author model
                return transformResults(searchResponse);
            }
        }
    }

    /**
     * Transforms JSON results (SearchResponse.AuthorSearchResult) into our Author model.
     */
    private List<Author> transformResults(SearchResponse response) {
        if (response.getAuthorResults() == null || response.getAuthorResults().isEmpty()) {
            return Collections.emptyList();
        }

        // Map each SearchResult to a simple Author object
        return response.getAuthorResults().stream()
                .map(result -> new Author(
                        null, // ID not available in this type of search
                        result.getName(),
                        result.getAffiliation(),
                        result.getCitedBy()
                ))
                .collect(Collectors.toList());
    }
}
