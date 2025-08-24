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
