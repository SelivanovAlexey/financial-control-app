ALTER TABLE users
    ADD COLUMN version INT NOT NULL DEFAULT 0;

COMMENT ON COLUMN users.version IS 'Версия (для оптимистичной блокировки)';