package edu.hw1.model;

public class ReadingProgress {
    private Integer bookId;
    private int lastPage;

    public ReadingProgress() {
    }

    public ReadingProgress(Integer bookId, int lastPage) {
        this.bookId = bookId;
        this.lastPage = lastPage;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
