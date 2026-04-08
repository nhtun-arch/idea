package edu.hw1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import edu.hw1.model.ReadingProgress;

public class ReadingProgressDao {

    public void ensureSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS reading_progress (
                    session_id VARCHAR(128) PRIMARY KEY,
                    book_id INTEGER REFERENCES books(id) ON DELETE SET NULL,
                    last_page INTEGER NOT NULL DEFAULT 0,
                    request_count INTEGER NOT NULL DEFAULT 0,
                    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
                )
                """;

        try (Connection connection = ConnectionFactory.open();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to ensure reading_progress schema", ex);
        }
    }

    public Optional<ReadingProgress> findBySessionId(String sessionId) {
        String sql = "SELECT book_id, last_page FROM reading_progress WHERE session_id = ?";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int bookId = rs.getInt("book_id");
                    Integer nullableBookId = rs.wasNull() ? null : bookId;
                    return Optional.of(new ReadingProgress(nullableBookId, rs.getInt("last_page")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query reading progress", ex);
        }
        return Optional.empty();
    }

    public ReadingProgress upsert(String sessionId, int requestCount, Integer bookId, int lastPage) {
        String sql = """
                INSERT INTO reading_progress(session_id, book_id, last_page, request_count, updated_at)
                VALUES (?, ?, ?, ?, NOW())
                ON CONFLICT(session_id)
                DO UPDATE SET
                    book_id = EXCLUDED.book_id,
                    last_page = EXCLUDED.last_page,
                    request_count = EXCLUDED.request_count,
                    updated_at = NOW()
                RETURNING book_id, last_page
                """;

        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionId);
            if (bookId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, bookId);
            }
            statement.setInt(3, lastPage);
            statement.setInt(4, requestCount);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                int storedBookId = rs.getInt("book_id");
                Integer nullableBookId = rs.wasNull() ? null : storedBookId;
                return new ReadingProgress(nullableBookId, rs.getInt("last_page"));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to upsert reading progress", ex);
        }
    }

    public void deleteBySessionId(String sessionId) {
        String sql = "DELETE FROM reading_progress WHERE session_id = ?";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to delete reading progress", ex);
        }
    }
}
