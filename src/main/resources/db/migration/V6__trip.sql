CREATE TABLE `trip`
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER  NOT NULL,
    title      TEXT,
    start_at   DATETIME NOT NULL,
    end_at     DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
