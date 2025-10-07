package org.example;

import org.example.controller.AuthorController;
import org.example.model.DatabaseConnection;
import org.example.view.AuthorView;

/**
 * Main class to run the Google Scholar Author Search application.
 */

public class Main {
    public static void main(String[] args) {
        // Crear tablas si no existen
        DatabaseConnection.createTables();

        AuthorView view = new AuthorView();
        AuthorController controller = new AuthorController(view);
        controller.startSearchProcess();

        System.out.println("Process completed.");
    }
}
