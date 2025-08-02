CREATE TABLE `export`
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER  NOT NULL,
    filename   TEXT     NOT NULL,
    start_at   DATETIME NOT NULL,
    end_at     DATETIME NOT NULL,
    content    BLOB,
    done       BOOLEAN  NOT NULL DEFAULT false,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id)
);


CREATE UNIQUE INDEX idx_user_id_fileame ON export(user_id, filename);
