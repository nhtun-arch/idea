package edu.hw1.service;

import java.util.Optional;

import edu.hw1.dao.ReadingProgressDao;
import edu.hw1.model.ReadingProgress;

public class ReadingSessionService {
    private static final ReadingSessionService INSTANCE = new ReadingSessionService();

    private final ReadingProgressDao readingProgressDao = new ReadingProgressDao();

    private ReadingSessionService() {
        readingProgressDao.ensureSchema();
    }

    public static ReadingSessionService getInstance() {
        return INSTANCE;
    }

    public Optional<ReadingProgress> getProgress(String sessionId) {
        return readingProgressDao.findBySessionId(sessionId);
    }

    public ReadingProgress saveProgress(String sessionId, int requestCount, Integer bookId, int lastPage) {
        return readingProgressDao.upsert(sessionId, requestCount, bookId, lastPage);
    }

    public void resetProgress(String sessionId) {
        readingProgressDao.deleteBySessionId(sessionId);
    }
}
