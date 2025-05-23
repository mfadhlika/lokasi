CREATE TABLE `user`(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    `password` TEXT NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE `location`(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    device_id TEXT,
    geometry GEOMETRY NOT NULL,
    altitude INTEGER,
    course INTEGER,
    speed DOUBLE,
    accuracy INTEGER,
    vertical_accuracy INTEGER,
    motions JSON,
    battery_state INTEGER,
    battery DOUBLE,
    ssid TEXT,
    raw_data JSON,
    timestamp DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
