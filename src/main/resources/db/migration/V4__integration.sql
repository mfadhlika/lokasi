CREATE TABLE `integration`
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER  NOT NULL UNIQUE,
    owntracks_enable BOOLEAN DEFAULT FALSE,
    owntracks_username TEXT,
    owntracks_password TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
