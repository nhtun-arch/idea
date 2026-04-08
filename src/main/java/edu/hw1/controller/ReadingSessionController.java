package edu.hw1.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hw1.model.ReadingProgress;
import edu.hw1.service.HistoryCatalogService;
import edu.hw1.service.ReadingSessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ReadingSessionController", urlPatterns = "/api/reading-session")
public class ReadingSessionController extends HttpServlet {
    private static final String REQUEST_COUNT_KEY = "requestCount";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HistoryCatalogService catalogService = HistoryCatalogService.getInstance();
    private final ReadingSessionService readingSessionService = ReadingSessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        int requestCount = incrementRequestCount(session);

        ReadingProgress progress = readingSessionService.getProgress(session.getId())
                .orElse(new ReadingProgress(null, 0));

        readingSessionService.saveProgress(session.getId(), requestCount, progress.getBookId(), progress.getLastPage());

        writeJson(resp, HttpServletResponse.SC_OK, buildPayload(session.getId(), requestCount, progress));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        upsertProgress(req, resp, HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        upsertProgress(req, resp, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            readingSessionService.resetProgress(session.getId());
            session.removeAttribute(REQUEST_COUNT_KEY);
        }
        writeJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Reading session reset"));
    }

    private void upsertProgress(HttpServletRequest req, HttpServletResponse resp, int status) throws IOException {
        HttpSession session = req.getSession(true);
        ReadingProgress input = mapper.readValue(req.getInputStream(), ReadingProgress.class);

        if (input.getBookId() == null || catalogService.getById(input.getBookId()).isEmpty()) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "bookId must refer to an existing catalog book"));
            return;
        }

        if (input.getLastPage() < 0) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "lastPage must be >= 0"));
            return;
        }

        int requestCount = incrementRequestCount(session);
        ReadingProgress saved = readingSessionService.saveProgress(
                session.getId(),
                requestCount,
                input.getBookId(),
                input.getLastPage());

        writeJson(resp, status, buildPayload(session.getId(), requestCount, saved));
    }

    private Map<String, Object> buildPayload(String sessionId, int requestCount, ReadingProgress progress) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sessionId", sessionId);
        payload.put("requestCount", requestCount);
        payload.put("bookId", progress.getBookId());
        payload.put("book", resolveBookTitle(progress.getBookId()));
        payload.put("lastPage", progress.getLastPage());
        return payload;
    }

    private String resolveBookTitle(Integer bookId) {
        if (bookId == null) {
            return null;
        }
        return catalogService.getById(bookId).map(book -> book.getTitle()).orElse("Unknown title");
    }

    private int incrementRequestCount(HttpSession session) {
        Integer count = (Integer) session.getAttribute(REQUEST_COUNT_KEY);
        int updated = count == null ? 1 : count + 1;
        session.setAttribute(REQUEST_COUNT_KEY, updated);
        return updated;
    }

    private void writeJson(HttpServletResponse resp, int status, Object payload) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        mapper.writeValue(resp.getOutputStream(), payload);
    }
}
