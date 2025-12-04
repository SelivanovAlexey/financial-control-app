"use client";
import "@/app/globals.css";
import styles from "./page.module.css";
import { Provider } from "react-redux";
import { store } from "@/app/store";

const metadata = {
  title: "Finance App",
  description: "Finance App For Personal Use"
};

export default function AuthLayout({ children }) {
    return (
        <Provider store = {store}>
            <html lang="en">
                <head>
                    <title>{metadata.title}</title>
                    <meta name="description" content={metadata.description} />
                </head>
                <body className={styles.page}>
                    {children}
                </body>
            </html>
        </Provider>
    );
}