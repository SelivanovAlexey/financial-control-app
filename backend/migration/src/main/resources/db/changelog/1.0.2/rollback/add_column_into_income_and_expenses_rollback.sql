DROP INDEX IF EXISTS idx_expenses_user_id;

ALTER TABLE expenses
DROP CONSTRAINT IF EXISTS expenses_user_fk,
    DROP COLUMN IF EXISTS user_id,
    DROP COLUMN IF EXISTS description;

DROP INDEX IF EXISTS idx_incomes_user_id;

ALTER TABLE incomes
DROP CONSTRAINT IF EXISTS incomes_user_fk,
    DROP COLUMN IF EXISTS user_id,
    DROP COLUMN IF EXISTS description;