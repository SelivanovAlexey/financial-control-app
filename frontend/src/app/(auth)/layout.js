import { PublicEnv } from '../../../public-env';
import "@/app/globals.css";
import styles from "./page.module.css";
import ClientProviders from "@/components/providers/ClientProviders";

export const metadata = {
  title: "Finance App",
  description: "Finance App For Personal Use"
};

export default function AuthLayout({ children }) {
    return (
        <html lang="en">
            <body className={styles.page}>
                <PublicEnv />
                <ClientProviders>
                    {children}
                </ClientProviders>
            </body>
        </html>
    );
}