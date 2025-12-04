ALTER TABLE public.users
    ADD COLUMN email TEXT;

COMMENT ON COLUMN public.users.email IS 'Электронная почта';