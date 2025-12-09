ALTER TABLE public.incomes
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT incomes_user_fk FOREIGN KEY (user_id) REFERENCES public.users (id);

COMMENT ON COLUMN public.incomes.description IS 'Описание дохода';
COMMENT ON COLUMN public.incomes.user_id IS 'Пользователь';

ALTER TABLE public.expenses
    ADD COLUMN description TEXT,
    ADD COLUMN user_id bigint NOT NULL,
    ADD CONSTRAINT expenses_user_fk FOREIGN KEY (user_id) REFERENCES public.users (id);

COMMENT ON COLUMN public.expenses.description IS 'Описание расхода';
COMMENT ON COLUMN public.expenses.user_id IS 'Пользователь';

CREATE INDEX IF NOT EXISTS idx_incomes_user_id ON public.incomes (user_id);
CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON public.expenses (user_id);