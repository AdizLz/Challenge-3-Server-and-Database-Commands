package org.example.controller;

import org.example.model.ArticleDAO;
import org.example.view.AuthorView;
import org.example.model.Author;
import org.example.model.Publication;
import com.google.gson.Gson;
// --- HTTP Libraries ---
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class AuthorController {

    private final String API_KEY = "619324adda98afcccd33191b4106bc08cab6be29b7180d5304c8067b585a568d";
    private final String BASE_URL = "https://serpapi.com/search";

    private final AuthorView view;

    public AuthorController(AuthorView view) {
        this.view = view;
    }


    public void startSearchProcess() {
        try {
            final int researchersToProcess = 2; // 2 investigadores
            for (int i = 1; i <= researchersToProcess; i++) {
                System.out.println("\n--- Researcher " + i + " of " + researchersToProcess + " ---");
                String authorId = view.getSearchQuery(); // pide ID (igual vista actual)

                if (authorId == null || authorId.isBlank()) {
                    view.displayError("Author ID cannot be empty. Skipping.");
                    continue;
                }

                Author author = searchAuthorById(authorId);
                if (author != null) {

                    // === CORRECCIÓN CLAVE AQUÍ ===
                    // Si el AuthorId de la respuesta JSON es nulo, usamos el ID que el usuario ingresó.
                    if (author.getAuthorId() == null || author.getAuthorId().isBlank()) {
                        author.setAuthorId(authorId); // Asigna el ID ingresado al objeto Author
                    }
                    // ==============================

                    view.displayResults(Collections.singletonList(author));

                    // Guarda (o recupera) el researcher en la tabla researchers
                    int researcherId = ArticleDAO.insertResearcherIfNotExists(
                            author.getAuthorId(), // <-- Ahora garantizas que no es null
                            author.getName(),
                            author.getAffiliation(),
                            author.getTotalCitations()
                    );

                    // Guardar hasta 3 artículos por investigador
                    if (author.getPublications() != null) {
                        List<Publication> toSave = author.getPublications().stream().limit(3).collect(Collectors.toList());
                        for (Publication pub : toSave) {
                            ArticleDAO.insertArticle(pub, author.getName(), researcherId);
                            // pequeña pausa para respetar límites de la API (opcional)
                            try { Thread.sleep(500); } catch (InterruptedException e) { /* ignore */ }
                        }
                    } else {
                        System.out.println("No publications to save for this author.");
                    }
                } else {
                    view.displayError("No author found with that ID.");
                }

            } // end for

        } catch (IOException e) {
            view.displayError("I/O Error (Network): " + e.getMessage());
        } catch (Exception e) {
            view.displayError("Unexpected error: " + e.getMessage());
        }
    }


    /**
     * Busca directamente el perfil de un autor por su ID de Google Scholar
     */
    private Author searchAuthorById(String authorId) throws IOException {
        String url = String.format(
                "%s?engine=google_scholar_author&author_id=%s&api_key=%s&hl=en&gl=us",
                BASE_URL,
                authorId,
                API_KEY
        );

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != 200) {
                    view.displayError("HTTP Error: " + status);
                    return null;
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());

                Gson gson = new Gson();
                // SerpAPI devuelve directamente el perfil del autor con sus publicaciones
                AuthorProfileResponse profile = gson.fromJson(jsonResponse, AuthorProfileResponse.class);

                if (profile == null || profile.author == null) {
                    return null;
                }

                // NOTA: Si el campo 'authorId' en la respuesta JSON es en realidad 'scholar_id' u otro nombre,
                // el constructor recibirá 'null' aquí. La corrección se hace en startSearchProcess().
                Author author = new Author(
                        profile.author.authorId,
                        profile.author.name,
                        profile.author.affiliations,
                        profile.citedBy != null ? profile.citedBy.value : 0
                );

                if (profile.articles != null) {
                    List<Publication> publications = profile.articles.stream()
                            .map(article -> {
                                Publication pub = new Publication();
                                pub.setTitle(article.title);
                                pub.setLink(article.link);
                                pub.setCitations(article.citedBy != null ? article.citedBy.value : 0);
                                return pub;
                            })
                            .collect(Collectors.toList());
                    author.setPublications(publications);
                }

                return author;
            }
        }
    }

    /**
     * Clase auxiliar para mapear el JSON de SerpAPI
     */
    private static class AuthorProfileResponse {
        private AuthorData author;
        private Citation citedBy;
        private List<Article> articles;

        private static class AuthorData {
            private String authorId; // <-- POSIBLE PUNTO DE FALLA. Si la API usa 'scholar_id', esto será null.
            private String name;
            private String affiliations;
        }

        private static class Citation {
            private int value;
        }

        private static class Article {
            private String title;
            private String link;
            private Citation citedBy;
        }
    }
}