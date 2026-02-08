import { createPublicEnv } from 'next-public-env';

export const { getPublicEnv, PublicEnv } = createPublicEnv(
    {
      NODE_ENV: process.env.NODE_ENV,
      BACKEND_URL: process.env.BACKEND_URL,
    },
    {
      schema: (z) => ({
        NODE_ENV: z.enum(['development', 'production', 'test']),
        BACKEND_URL: z.string()
      }),
    }
);