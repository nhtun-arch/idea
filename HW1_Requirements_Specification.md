# Homework 3 - HistoryShelf Architecture, Logic, and Persistence Specification

## 1) Updated System Goal
HistoryShelf is a history-ebook REST service that now follows MVC and persists data in PostgreSQL.

## 2) MVC Architecture
- **View/Presentation layer:** REST endpoints (`/api/books`, `/api/reading-session`) returning JSON.
- **Controller/Dispatch layer:** `BooksController`, `ReadingSessionController`, plus servlet filter(s).
- **Model/Logic layer:** services (`HistoryCatalogService`, `ReadingSessionService`, `ExternalHistoryService`) and model classes (`Book`, `ReadingProgress`).
- **Persistence layer:** DAO classes (`BookDao`, `ReadingProgressDao`) using JDBC + SQL.

## 3) REST Endpoints (Final)
### Books
- `GET /api/books?q=&includeExternal=true|false`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

### Reading Session
- `GET /api/reading-session`
- `POST /api/reading-session`
- `PUT /api/reading-session`
- `DELETE /api/reading-session`

All responses use:
```java
response.setContentType("application/json; charset=UTF-8");
```

## 4) Database Requirements
PostgreSQL is used for persistence.

### Tables
1. `books`
   - `id SERIAL PRIMARY KEY`
   - `title VARCHAR(255) NOT NULL`
   - `author VARCHAR(255) NOT NULL`
   - `created_at TIMESTAMP NOT NULL DEFAULT NOW()`

2. `reading_progress`
   - `session_id VARCHAR(128) PRIMARY KEY`
   - `book_id INTEGER REFERENCES books(id) ON DELETE SET NULL`
   - `last_page INTEGER NOT NULL DEFAULT 0`
   - `request_count INTEGER NOT NULL DEFAULT 0`
   - `updated_at TIMESTAMP NOT NULL DEFAULT NOW()`

## 5) Validation Rules
- `POST /api/books`: title is required.
- `PUT/DELETE /api/books/{id}`: numeric id required.
- Reading session writes: `bookId` must exist in `books`; `lastPage >= 0`.

## 6) External Service
Open Library API is queried for external suggestions when requested by client on `GET /api/books`.

## 7) Deliverables for HW3
- Source code (MVC + PostgreSQL persistence)
- Packaged WAR (`target/studybuddy.war`)
- MVC diagram (`MVC_Dataflow_Diagram.md`)
- SQL dump (`db/historyshelf_dump.sql`)
