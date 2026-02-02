ALTER TABLE incomes
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT incomes_user_fk FOREIGN KEY (user_id) REFERENCES users (id);

COMMENT ON COLUMN incomes.description IS 'Описание дохода';
COMMENT ON COLUMN incomes.user_id IS 'Пользователь';

ALTER TABLE expenses
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT expenses_user_fk FOREIGN KEY (user_id) REFERENCES users (id);

COMMENT ON COLUMN expenses.description IS 'Описание расхода';
COMMENT ON COLUMN expenses.user_id IS 'Пользователь';

CREATE INDEX IF NOT EXISTS idx_incomes_user_id ON incomes (user_id);
CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON expenses (user_id);