'use client';
import { Provider } from "react-redux";
import { PersistGate } from 'redux-persist/integration/react';
import { store, persistor } from "@/app/store";
import AuthCheck from "@/components/AuthCheck";
import AuthInitializer from "@/components/AuthInitializer";
import Header from "@/components/header/header";
import Sider from "@/components/sider/sider";
import Content from "@/components/content/content";
import { Grid } from "@mui/material";

export default function DashboardClientProviders({ children }) {
    return (
        <Provider store={store}>
            <PersistGate loading={null} persistor={persistor}>
                <AuthInitializer>
                    <AuthCheck>
                        <Grid container>
                            {/* Сider: скрыт на мобилках (xs), виден на десктопе (md) */}
                            <Grid
                                size={{ xs: 0, md: 1 }}
                                sx={{ display: { xs: 'none', md: 'block' } }}
                            >
                                <Sider />
                            </Grid>

                            {/* Основной контент: 12 колонок на мобилках, 11 на десктопе */}
                            <Grid size={{ xs: 12, md: 11 }}>
                                <Header />
                                <Content>{children}</Content>
                            </Grid>
                        </Grid>
                    </AuthCheck>
                </AuthInitializer>
            </PersistGate>
        </Provider>
    );
}
