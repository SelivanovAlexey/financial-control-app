'use client';

import { Roboto } from "next/font/google";
import "../globals.css";
import Header from "@/components/header/header";
import Sider from "@/components/sider/sider";
import Content from "@/components/content/content";
import { Grid, useMediaQuery, useTheme } from "@mui/material";
import React from "react";
import { Provider } from "react-redux";
import { PersistGate } from 'redux-persist/integration/react';
import { store, persistor } from "@/app/store";
import AuthCheck from "@/components/AuthCheck";
import AuthInitializer from "@/components/AuthInitializer";


const roboto = Roboto({
  weight: "400",
  subsets: ["latin"]
});

const metadata = {
  title: "Finance App",
  description: "Finance App For Personal Use"
};

export default function DashboardLayout({ children }) {
  
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md')); // меньше чем 'md' (900px)

  return (
    <html lang="en">
      <head>
        <title>{metadata.title}</title>
        <meta name="description" content={metadata.description} />
      </head>
      <body style={{ fontFamily: roboto.style.fontFamily, height: "100vh"}}>
        <Provider store = {store}>
          <PersistGate loading={null} persistor={persistor}>
            <AuthInitializer>
              <AuthCheck>
                <Grid container>
                  {!isMobile && (
                  <Grid size={1}>
                    <Sider isMobile={isMobile}/>
                  </Grid>
                  )}
                  <Grid size={isMobile ? 12 : 11}>
                    <Header isMobile={isMobile}/>
                    <Content>{children}</Content>
                  </Grid>
                </Grid>
              </AuthCheck>
            </AuthInitializer>
          </PersistGate>
        </Provider>
      </body>
    </html>
  );
}
