-- HistoryShelf SQL dump (schema + seed)

CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS reading_progress (
    session_id VARCHAR(128) PRIMARY KEY,
    book_id INTEGER REFERENCES books(id) ON DELETE SET NULL,
    last_page INTEGER NOT NULL DEFAULT 0,
    request_count INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO books (title, author) VALUES
('SPQR: A History of Ancient Rome', 'Mary Beard'),
('The Silk Roads', 'Peter Frankopan'),
('The Guns of August', 'Barbara W. Tuchman')
ON CONFLICT DO NOTHING;
