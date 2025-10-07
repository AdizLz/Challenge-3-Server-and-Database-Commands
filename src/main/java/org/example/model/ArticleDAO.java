package org.example.model;

import java.sql.*;

public class ArticleDAO {

    /**
     * Inserta el autor en la tabla researchers (si no existe) y devuelve su id de clave primaria.
     * Este ID es crucial para la llave foránea de la tabla articles.
     */
    public static int insertResearcherIfNotExists(String authorId, String name, String affiliation, int totalCitations) {
        // La sentencia SQL para la inserción.
        // ON CONFLICT (author_id) DO NOTHING asegura que no falle si el ID de Google Scholar ya existe.
        String sqlInsert = "INSERT INTO researchers (author_id, name, affiliation, total_citations) " +
                "VALUES (?, ?, ?, ?) ON CONFLICT (author_id) DO NOTHING";

        // Esta SELECT es CRUCIAL para obtener el ID generado si es nuevo O si ya existía.
        String sqlSelect = "SELECT id FROM researchers WHERE author_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Intentar insertar el autor
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setString(1, authorId);
                pstmt.setString(2, name);
                pstmt.setString(3, affiliation);
                pstmt.setInt(4, totalCitations);

                pstmt.executeUpdate();
            }

            // 2. Recuperar el ID del autor insertado o preexistente
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
                pstmt.setString(1, authorId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id"); // ¡Devuelve el ID real!
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar/recuperar researcher: " + e.getMessage());
        }

        // Devuelve -1 solo si la conexión/recuperación falla.
        return -1;
    }


    /**
     * Inserta artículos en la tabla articles, vinculándolos al autor a través de researcherId.
     */
    public static void insertArticle(Publication pub, String authorName, int researcherId) {
        // La tabla articles DEBE existir y tener la columna researcher_id
        String sqlInsert = "INSERT INTO articles (title, authors, publication_date, abstract, link, keywords, cited_by, researcher_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Verificar que researcherId no sea -1 antes de insertar (opcional, pero buena práctica)
        if (researcherId == -1) {
            System.err.println("Error de lógica: No se pudo obtener el ID del autor para insertar el artículo: " + pub.getTitle());
            return; // Termina la ejecución para este artículo
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setString(1, pub.getTitle());
            pstmt.setString(2, authorName);
            pstmt.setInt(3, 2024); // Fecha por defecto
            pstmt.setString(4, ""); // Abstract vacío
            pstmt.setString(5, pub.getLink());
            pstmt.setString(6, ""); // Keywords vacío
            pstmt.setInt(7, pub.getCitations());
            pstmt.setInt(8, researcherId); // ID real del autor.

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}