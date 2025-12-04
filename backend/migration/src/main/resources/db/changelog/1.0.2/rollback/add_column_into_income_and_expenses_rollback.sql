DROP INDEX IF EXISTS idx_expenses_user_id;

ALTER TABLE public.expenses
DROP CONSTRAINT IF EXISTS expenses_user_fk,
    DROP COLUMN IF EXISTS user_id,
    DROP COLUMN IF EXISTS description;

DROP INDEX IF EXISTS idx_income_user_id;

ALTER TABLE public.income
DROP CONSTRAINT IF EXISTS income_user_fk,
    DROP COLUMN IF EXISTS user_id,
    DROP COLUMN IF EXISTS description;