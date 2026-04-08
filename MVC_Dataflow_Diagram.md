# HistoryShelf MVC Data Flow Diagram

```mermaid
flowchart LR
    Client[HTTP Client / Front-end]
    Filter[JsonContentTypeFilter]
    BooksC[BooksController]
    SessionC[ReadingSessionController]
    CatalogS[HistoryCatalogService]
    SessionS[ReadingSessionService]
    ExternalS[ExternalHistoryService]
    BookDao[BookDao]
    ProgressDao[ReadingProgressDao]
    PG[(PostgreSQL)]
    OpenLib[(Open Library API)]

    Client --> Filter
    Filter --> BooksC
    Filter --> SessionC

    BooksC --> CatalogS
    BooksC --> ExternalS
    SessionC --> SessionS
    SessionC --> CatalogS

    CatalogS --> BookDao
    SessionS --> ProgressDao
    BookDao --> PG
    ProgressDao --> PG
    ExternalS --> OpenLib
```

## Dispatch and Data Flow Notes
1. Client request enters filter, which enforces JSON response type.
2. Controller parses HTTP method and payload.
3. Controller delegates business logic to service layer.
4. Services call DAO classes for persistence.
5. DAO executes SQL against PostgreSQL and returns model objects.
6. Controllers serialize model data as JSON back to client.
