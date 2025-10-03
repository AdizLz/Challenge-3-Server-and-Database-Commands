package org.example;

import org.example.controller.AuthorController;
import org.example.view.AuthorView;

/**
 * Main class to run the Google Scholar Author Search application.
 */
public class Main {
    public static void main(String[] args) {

        // 1. Instantiate the View
        AuthorView view = new AuthorView();

        // 2. Instantiate the Controller, passing the View
        AuthorController controller = new AuthorController(view);

        // 3. Start the interactive search process
        controller.startSearchProcess();

        System.out.println("Process completed.");

        // Note: The View class now handles the Scanner internally.
    }
}
