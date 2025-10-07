package org.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections and table creation for the Scholar DB
 */
public class DatabaseConnection {
    // Database credentials - can be overridden by environment variables
    private static final String URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:postgresql://localhost:5432/scholar_db";

    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "postgres";

    private static final String PASSWORD = System.getenv("DB_PASS") != null
            ? System.getenv("DB_PASS")
            : "9502";

    /**
     * Establishes and returns a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Ensure PostgreSQL driver is loaded
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }

    /**
     * Creates necessary tables if they don't exist
     */
    public static void createTables() {
        String createResearchers = "CREATE TABLE IF NOT EXISTS researchers (" +
                "id SERIAL PRIMARY KEY, " +
                "author_id VARCHAR(255) UNIQUE NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "affiliation TEXT, " +
                "total_citations INTEGER DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String createArticles = "CREATE TABLE IF NOT EXISTS articles (" +
                "id SERIAL PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "authors TEXT, " +
                "publication_date INTEGER, " +
                "abstract TEXT, " +
                "link TEXT, " +
                "keywords TEXT, " +
                "cited_by INTEGER DEFAULT 0, " +
                "researcher_id INTEGER REFERENCES researchers(id) ON DELETE CASCADE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createResearchers);
            stmt.execute(createArticles);
            System.out.println("✅ Database tables created/validated successfully.");

        } catch (SQLException e) {
            System.err.println(" Error creating database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tests the database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println(" Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}