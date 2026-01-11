ALTER TABLE region ADD COLUMN geometry GEOMETRY;

UPDATE region SET geometry = MakeCircle(lon, lat, rad);

ALTER TABLE region DROP COLUMN lat;
ALTER TABLE region DROP COLUMN lon;
ALTER TABLE region DROP COLUMN rad;
