package org.example.view;

import org.example.model.Author;
import java.util.List;
import java.util.Scanner;

/**
 * View class for displaying Google Scholar author search results and handling user input.
 */
public class AuthorView {

    // Scanner object for reading user input (initialized only once)
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user for a search query.
     * @return the author name to search for
     */
    public String getSearchQuery() {
        System.out.println("\n--- Google Scholar Author Search ---");
        System.out.print("Enter author ID to search: ");

        return scanner.nextLine().trim();
    }

    /**
     * Displays the list of authors found in the search.
     * @param authors list of authors
     */
    public void displayResults(List<Author> authors) {
        if (authors.isEmpty()) {
            System.out.println("\n No authors found or an error occurred during the search.");
            return;
        }

        System.out.println("\n Search Results Found (" + authors.size() + " total):");
        System.out.println("-------------------------------------");

        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            // Output format: 1. Name: X, Affiliation: Y, Citations: Z
            System.out.printf("%d. Name: %s, Affiliation: %s, Citations: %d\n",
                    i + 1,
                    author.getName(),
                    author.getAffiliation(),
                    author.getCitationCount()
            );
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Displays an error message to the user.
     * @param message error message
     */
    public void displayError(String message) {
        System.err.println("ERROR: " + message);
    }

    // Note: Close the scanner in Main.java when the program exits.
}
