package edu.hw1.service;

import java.util.List;
import java.util.Optional;

import edu.hw1.dao.BookDao;
import edu.hw1.model.Book;

public class HistoryCatalogService {
    private static final HistoryCatalogService INSTANCE = new HistoryCatalogService();

    private final BookDao bookDao = new BookDao();

    private HistoryCatalogService() {
        bookDao.ensureSchema();
        seedIfEmpty();
    }

    public static HistoryCatalogService getInstance() {
        return INSTANCE;
    }

    public List<Book> listBooks(String query) {
        return bookDao.findAll(query);
    }

    public Book addBook(String title, String author) {
        return bookDao.insert(title, author);
    }

    public Optional<Book> updateBook(int id, String title, String author) {
        return bookDao.update(id, title, author);
    }

    public Optional<Book> deleteBook(int id) {
        return bookDao.delete(id);
    }

    public Optional<Book> getById(int id) {
        return bookDao.findById(id);
    }

    private void seedIfEmpty() {
        if (bookDao.findAll(null).isEmpty()) {
            bookDao.insert("SPQR: A History of Ancient Rome", "Mary Beard");
            bookDao.insert("The Silk Roads", "Peter Frankopan");
            bookDao.insert("The Guns of August", "Barbara W. Tuchman");
        }
    }
}
