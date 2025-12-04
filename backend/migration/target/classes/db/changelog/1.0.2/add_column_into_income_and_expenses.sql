ALTER TABLE public.income
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT income_user_fk FOREIGN KEY (user_id) REFERENCES public.users (id);

COMMENT ON COLUMN public.income.description IS 'Описание дохода';
COMMENT ON COLUMN public.income.user_id IS 'Пользователь';

ALTER TABLE public.expenses
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT expenses_user_fk FOREIGN KEY (user_id) REFERENCES public.users (id);

COMMENT ON COLUMN public.expenses.description IS 'Описание расхода';
COMMENT ON COLUMN public.expenses.user_id IS 'Пользователь';

CREATE INDEX IF NOT EXISTS idx_income_user_id ON public.income (user_id);
CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON public.expenses (user_id);