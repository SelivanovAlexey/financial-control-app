ALTER TABLE users
    ADD COLUMN email TEXT;

COMMENT ON COLUMN users.email IS 'Электронная почта';