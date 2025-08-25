
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
    battery_state INTEGER,
    battery DOUBLE,
    ssid TEXT,
    timestamp DATETIME NOT NULL,
    created_at DATETIME NOT NULL, import_id INTEGER REFERENCES import(id), course_accuracy INTEGER, raw_data JSONB, motions JSONB, geocode JSONB,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

CREATE UNIQUE INDEX idx_geometry_timestamp ON location(geometry, timestamp);

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
    count INTEGER, created_at DATETIME,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

CREATE TABLE `integration`
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER  NOT NULL UNIQUE,
    owntracks_username TEXT,
    owntracks_password TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, overland_api_key TEXT,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

CREATE TABLE region (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    lat FLOAT,
    lon FLOAT,
    rad INTEGER,
    beacon_uuid TEXT,
    beacon_major INTEGER,
    beacon_minor INTEGER,
    rid TEXT UNIQUE,
    geocode JSONB,
    created_at DATETIME DEFAULT CURRENT_DATETIME
);

CREATE TABLE `trip`
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER  NOT NULL,
    title      TEXT,
    start_at   DATETIME NOT NULL,
    end_at     DATETIME NOT NULL,
    created_at DATETIME NOT NULL, 
    uuid       TEXT UNIQUE NOT NULL, 
    is_public BOOLEAN DEFAULT false,
    FOREIGN KEY(user_id) REFERENCES user(id)
);
