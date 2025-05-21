CREATE TABLE `user`(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    `password` TEXT NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE `point`(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    `point` GEOMETRY NOT NULL,
    speed FLOAT,
    `timestamp` DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
