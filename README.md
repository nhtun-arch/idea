# HistoryShelf REST Service (Homework 3)

## MVC + PostgreSQL design
- **Controllers:** `BooksController`, `ReadingSessionController`
- **Services:** `HistoryCatalogService`, `ReadingSessionService`, `ExternalHistoryService`
- **DAOs:** `BookDao`, `ReadingProgressDao`
- **Filter:** `JsonContentTypeFilter`
- **Database:** PostgreSQL via JDBC

## Environment variables
Set database credentials before running:

```bash
export HISTORYSHELF_DB_URL="jdbc:postgresql://localhost:5432/historyshelf"
export HISTORYSHELF_DB_USER="postgres"
export HISTORYSHELF_DB_PASSWORD="postgres"
```

## Build and package
```bash
mvn clean package
```
WAR output: `target/studybuddy.war`

## SQL dump
- `db/historyshelf_dump.sql`

## MVC data-flow diagram
- `MVC_Dataflow_Diagram.md`

## REST examples
```bash
curl "http://localhost:8080/studybuddy/api/books?q=rome&includeExternal=true"

curl -X POST "http://localhost:8080/studybuddy/api/books" \
  -H "Content-Type: application/json" \
  -d '{"title":"The Crusades","author":"Thomas Asbridge"}'

curl -c cookies.txt -X POST "http://localhost:8080/studybuddy/api/reading-session" \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"lastPage":20}'
```
