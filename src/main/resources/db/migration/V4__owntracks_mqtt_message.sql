CREATE TABLE owntracks_mqtt_message(
    id INTEGER PRIMARY KEY,
    uuid TEXT NOT NULL UNIQUE,
    topic TEXT NOT NULL,
    payload JSONB,
    status TEXT DEFAULT 'RECEIVED',
    reason TEXT,
    created_at DATETIME NOT NULL
);
