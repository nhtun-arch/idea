package edu.hw1.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.hw1.model.Book;

public class BookDao {

    public void ensureSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS books (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT NOW()
                )
                """;

        try (Connection connection = ConnectionFactory.open();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to ensure books schema", ex);
        }
    }

    public List<Book> findAll(String query) {
        boolean hasQuery = query != null && !query.isBlank();
        String sql = hasQuery
                ? "SELECT id,title,author FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY id"
                : "SELECT id,title,author FROM books ORDER BY id";

        List<Book> result = new ArrayList<>();
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasQuery) {
                String token = "%" + query.trim().toLowerCase() + "%";
                statement.setString(1, token);
                statement.setString(2, token);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to fetch books", ex);
        }
        return result;
    }

    public Optional<Book> findById(int id) {
        String sql = "SELECT id,title,author FROM books WHERE id = ?";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to fetch book", ex);
        }
        return Optional.empty();
    }

    public Book insert(String title, String author) {
        String sql = "INSERT INTO books(title, author) VALUES (?, ?) RETURNING id, title, author";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to insert book", ex);
        }
    }

    public Optional<Book> update(int id, String title, String author) {
        String sql = "UPDATE books SET title = COALESCE(?, title), author = COALESCE(?, author) WHERE id = ? RETURNING id, title, author";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to update book", ex);
        }
        return Optional.empty();
    }

    public Optional<Book> delete(int id) {
        String sql = "DELETE FROM books WHERE id = ? RETURNING id, title, author";
        try (Connection connection = ConnectionFactory.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author")));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to delete book", ex);
        }
        return Optional.empty();
    }
}
