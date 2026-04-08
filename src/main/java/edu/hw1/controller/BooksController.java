package edu.hw1.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hw1.model.Book;
import edu.hw1.service.ExternalHistoryService;
import edu.hw1.service.HistoryCatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BooksController", urlPatterns = "/api/books/*")
public class BooksController extends HttpServlet {
    private final HistoryCatalogService catalogService = HistoryCatalogService.getInstance();
    private final ExternalHistoryService externalHistoryService = new ExternalHistoryService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        List<Book> books = catalogService.listBooks(query);

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", books);
        payload.put("count", books.size());
        payload.put("query", query == null ? "" : query);

        if ("true".equalsIgnoreCase(req.getParameter("includeExternal"))) {
            payload.put("externalSuggestions", externalHistoryService.fetchSuggestedTitles(query));
        }

        writeJson(resp, HttpServletResponse.SC_OK, payload);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> input = mapper.readValue(req.getInputStream(), new TypeReference<>() {
        });
        String title = input.get("title") == null ? "" : input.get("title").toString();
        String author = input.get("author") == null ? "Unknown" : input.get("author").toString();

        if (title.isBlank()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Field 'title' is required");
            return;
        }

        Book created = catalogService.addBook(title.trim(), author.trim());
        writeJson(resp, HttpServletResponse.SC_CREATED, created);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = parsePathId(req);
        if (id == null) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Path id is required for PUT /api/books/{id}");
            return;
        }

        Map<String, Object> input = mapper.readValue(req.getInputStream(), new TypeReference<>() {
        });
        String title = input.get("title") == null ? null : input.get("title").toString();
        String author = input.get("author") == null ? null : input.get("author").toString();

        catalogService.updateBook(id, title, author)
                .ifPresentOrElse(
                        updated -> writeJson(resp, HttpServletResponse.SC_OK, updated),
                        () -> writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Book not found: " + id));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = parsePathId(req);
        if (id == null) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Path id is required for DELETE /api/books/{id}");
            return;
        }

        catalogService.deleteBook(id)
                .ifPresentOrElse(
                        deleted -> writeJson(resp, HttpServletResponse.SC_OK, deleted),
                        () -> writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Book not found: " + id));
    }

    private Integer parsePathId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank() || "/".equals(pathInfo)) {
            return null;
        }

        String rawId = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        if (!rawId.matches("\\d+")) {
            return null;
        }
        return Integer.parseInt(rawId);
    }

    private void writeJson(HttpServletResponse resp, int status, Object payload) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        mapper.writeValue(resp.getOutputStream(), payload);
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeJson(resp, status, Map.of("error", message));
    }
}
