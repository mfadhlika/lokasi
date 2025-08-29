CREATE TABLE place(
    id INTEGER PRIMARY KEY,
    provider TEXT NOT NULL,
    type TEXT,
    postcode TEXT,
    country_code TEXT,
    name TEXT,
    country TEXT,
    city TEXT,
    district TEXT,
    locality TEXT,
    street TEXT,
    state TEXT,
    geometry GEOMETRY,
    geodata JSONB,
    created_at DATETIME NOT NULL
);

CREATE UNIQUE INDEX idx_unique_place ON place(type, postcode, country_code, name, country, city, district, locality, street, state);

INSERT INTO place(provider, type, postcode, country_code, name, country, city, district, locality, street, state, geometry, geodata, created_at)
SELECT 
    DISTINCT
    'photon',
    json_extract(geocode, f.fullkey || '.properties.type'),
    json_extract(geocode, f.fullkey || '.properties.postcode'),
    json_extract(geocode, f.fullkey || '.properties.country_code'),
    json_extract(geocode, f.fullkey || '.properties.name'),
    json_extract(geocode, f.fullkey || '.properties.country'),
    json_extract(geocode, f.fullkey || '.properties.city'),
    json_extract(geocode, f.fullkey || '.properties.district'),
    json_extract(geocode, f.fullkey || '.properties.locality'),
    json_extract(geocode, f.fullkey || '.properties.street'),
    json_extract(geocode, f.fullkey || '.properties.state'),
    GeomFromGeoJSON(json_extract(geocode, f.fullkey || '.geometry')),
    jsonb_extract(geocode, f.fullkey),
    created_at
FROM location, json_each(geocode, '$.features') f
GROUP BY json_extract(geocode, f.fullkey || '.properties.osm_id');
