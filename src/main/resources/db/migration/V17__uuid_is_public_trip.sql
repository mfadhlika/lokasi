ALTER TABLE trip ADD COLUMN uuid TEXT;
ALTER TABLE trip ADD COLUMN is_public BOOLEAN DEFAULT false;

CREATE UNIQUE INDEX idx_trip_uuid ON trip(uuid);
