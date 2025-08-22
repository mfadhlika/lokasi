CREATE TABLE `owntracks_tour`
(
    id          INTEGER  PRIMARY KEY AUTOINCREMENT,
    user_id     INTEGER  NOT NULL,
    uuid        TEXT     UNIQUE,
    label       TEXT     NOT NULL,
    `from`      DATETIME NOT NULL,
    `to`        DATETIME NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
