-- temporary disable triggers
-- ALTER TABLE comments DISABLE TRIGGER all;
-- ALTER TABLE comments ENABLE TRIGGER all;

-- --------------------------------------------------
--                    COMMENTS
-- --------------------------------------------------

-- create a tsv column
ALTER TABLE comments
    ADD tsv tsvector;

-- update a tsv column
update comments
set tsv = to_tsvector(coalesce(text, ''));

-- create an index
CREATE INDEX comments_tsv_idx
    ON comments
        USING GIN (tsv);

-- create a trigger function
DROP FUNCTION IF EXISTS comments_tsv_trigger();
CREATE FUNCTION comments_tsv_trigger() RETURNS trigger AS '
begin
    new.tsv := to_tsvector(coalesce(new.text, ''''));
    return new;
end;
' LANGUAGE plpgsql;

-- create a trigger
CREATE TRIGGER comments_tsv_update
    BEFORE INSERT OR UPDATE
    ON comments
    FOR EACH ROW
EXECUTE PROCEDURE comments_tsv_trigger();

-- --------------------------------------------------
--                    CHANNELS
-- --------------------------------------------------

-- create a tsv column
ALTER TABLE channels
    ADD tsv tsvector;

-- update a tsv column
update channels
set tsv = to_tsvector(coalesce(title, ''));

-- create an index
CREATE INDEX channels_tsv_idx
    ON channels
        USING GIN (tsv);

-- create a trigger function
DROP FUNCTION IF EXISTS channels_tsv_trigger();
CREATE FUNCTION channels_tsv_trigger() RETURNS trigger AS '
begin
    new.tsv := to_tsvector(coalesce(new.title, ''''));
    return new;
end;
' LANGUAGE plpgsql;

-- create a trigger
CREATE TRIGGER channels_tsv_update
    BEFORE INSERT OR UPDATE
    ON channels
    FOR EACH ROW
EXECUTE PROCEDURE channels_tsv_trigger();

-- --------------------------------------------------
--                    VIDEOS
-- --------------------------------------------------

-- create a tsv column
ALTER TABLE videos
    ADD tsv tsvector;

-- update a tsv column
update videos
set tsv = to_tsvector(coalesce(title, ''));

-- create an index
CREATE INDEX videos_tsv_idx
    ON videos
        USING GIN (tsv);

-- create a trigger function
DROP FUNCTION IF EXISTS videos_tsv_trigger();
CREATE FUNCTION videos_tsv_trigger() RETURNS trigger AS '
begin
    new.tsv := to_tsvector(coalesce(new.title, ''''));
    return new;
end;
' LANGUAGE plpgsql;

-- create a trigger
CREATE TRIGGER videos_tsv_update
    BEFORE INSERT OR UPDATE
    ON videos
    FOR EACH ROW
EXECUTE PROCEDURE videos_tsv_trigger();
