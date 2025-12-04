'use client';

import { Roboto } from "next/font/google";
import "../globals.css";
import Header from "@/components/header/header";
import Sider from "@/components/sider/sider";
import Content from "@/components/content/content";
import { Grid } from "@mui/material";
import React from "react";
import { Provider } from "react-redux";
import { PersistGate } from 'redux-persist/integration/react';
import { store, persistor, useSelector } from "@/app/store";
import AuthCheck from "@/components/AuthCheck";


const roboto = Roboto({
  weight: "400",
  subsets: ["latin"]
});

const metadata = {
  title: "Finance App",
  description: "Finance App For Personal Use"
};

export default function DashboardLayout({ children }) {
  
  return (
    <html lang="en">
      <head>
        <title>{metadata.title}</title>
        <meta name="description" content={metadata.description} />
      </head>
      <body style={{ fontFamily: roboto.style.fontFamily, height: "100vh"}}>
        <Provider store = {store}>
          <PersistGate loading={null} persistor={persistor}>
            <AuthCheck>
              <Grid container>
                <Grid size={1}>
                  <Sider />
                </Grid>
                <Grid size={11}>
                  <Header />
                  <Content>{children}</Content>
                </Grid>
              </Grid>
            </AuthCheck>
          </PersistGate>
        </Provider>
      </body>
    </html>
  );
}
