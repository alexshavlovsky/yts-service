-- init the native full text search on the comments table

-- create a tsv column
ALTER TABLE comments
    ADD tsv tsvector;

-- update a tsv column
update comments
set tsv = setweight(to_tsvector(coalesce(text, '')), 'A') ||
          setweight(to_tsvector(coalesce(author_text, '')), 'B');

-- create an index
CREATE INDEX comments_tsv_idx
    ON comments
        USING GIN (tsv);

-- create a trigger function
CREATE FUNCTION comments_tsv_trigger() RETURNS trigger AS
$$
begin
    new.tsv :=
                setweight(to_tsvector('english', coalesce(new.text, '')), 'A') ||
                setweight(to_tsvector('english', coalesce(new.author_text, '')), 'B');
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
