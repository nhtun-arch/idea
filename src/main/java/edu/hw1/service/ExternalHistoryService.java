package edu.hw1.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalHistoryService {
    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/search.json?q=%s&limit=3";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> fetchSuggestedTitles(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        try {
            String encoded = URLEncoder.encode(query + " history", StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(OPEN_LIBRARY_URL, encoded)))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return Collections.emptyList();
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode docs = root.path("docs");
            if (!docs.isArray()) {
                return Collections.emptyList();
            }

            return mapper.convertValue(
                    docs.findValuesAsText("title").stream().limit(3).toList(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }
}
