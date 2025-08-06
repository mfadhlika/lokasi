ALTER TABLE location RENAME COLUMN raw_data TO raw_data_old;
ALTER TABLE location ADD COLUMN raw_data JSONB;
UPDATE location SET raw_data = jsonb(raw_data_old);
ALTER TABLE location DROP COLUMN raw_data_old;

ALTER TABLE location RENAME COLUMN motions TO motions_old;
ALTER TABLE location ADD COLUMN motions JSONB;
UPDATE location SET motions = jsonb(motions_old);
ALTER TABLE location DROP COLUMN motions_old;
