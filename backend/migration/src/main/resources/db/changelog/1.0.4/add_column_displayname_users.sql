ALTER TABLE users
    ADD COLUMN display_name TEXT;

COMMENT ON COLUMN users.display_name IS 'Отображаемое имя';