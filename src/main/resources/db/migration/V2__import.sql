CREATE TABLE `import`
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER  NOT NULL,
    source     TEXT     NOT NULL,
    filename   TEXT     NOT NULL,
    path       TEXT,
    content    BLOB,
    checksum   TEXT     NOT NULL UNIQUE,
    done       BOOLEAN  NOT NULL DEFAULT false,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

ALTER TABLE location ADD COLUMN import_id INTEGER REFERENCES import(id);