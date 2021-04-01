-- init the native full text search on the comments table

-- create a tsv column
ALTER TABLE comments
    ADD tsv tsvector;

-- update a tsv column
update comments
set tsv = to_tsvector(coalesce(text, '') || ' ' || coalesce(author_text, ''));

-- create an index
CREATE INDEX comments_tsv_idx
    ON comments
        USING GIN (tsv);

-- create a trigger function
CREATE FUNCTION comments_tsv_trigger() RETURNS trigger AS
$$
begin
    new.tsv := to_tsvector(coalesce(new.text, '') || ' ' || coalesce(new.author_text, ''));
    return new;
end
$$ LANGUAGE plpgsql;

-- create a trigger
CREATE TRIGGER comments_tsv_update
    BEFORE INSERT OR UPDATE
    ON comments
    FOR EACH ROW
EXECUTE PROCEDURE comments_tsv_trigger();

-- temporary disable triggers
-- ALTER TABLE comments DISABLE TRIGGER all;
-- ALTER TABLE comments ENABLE TRIGGER all;


-- create a tsv column
ALTER TABLE channels
    ADD tsv tsvector;

-- update a tsv column
update channels
set tsv = to_tsvector(coalesce(title, '') || ' ' || coalesce(channel_vanity_name, ''));

-- create an index
CREATE INDEX channels_tsv_idx
    ON channels
        USING GIN (tsv);

-- create a trigger function
CREATE FUNCTION channels_tsv_trigger() RETURNS trigger AS
$$
begin
    new.tsv := to_tsvector(coalesce(new.title, '') || ' ' || coalesce(new.channel_vanity_name, ''));
    return new;
end
$$ LANGUAGE plpgsql;

-- create a trigger
CREATE TRIGGER channels_tsv_update
    BEFORE INSERT OR UPDATE
    ON channels
    FOR EACH ROW
EXECUTE PROCEDURE channels_tsv_trigger();
