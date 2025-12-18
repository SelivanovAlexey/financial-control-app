ALTER TABLE public.users
    ADD COLUMN display_name TEXT;

COMMENT ON COLUMN public.users.display_name IS 'Отображаемое имя';