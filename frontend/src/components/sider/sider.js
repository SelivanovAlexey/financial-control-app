'use client';
import { Box, Tooltip, IconButton } from "@mui/material";
import styles from "./sider.module.css";
import HomeIcon from '@mui/icons-material/Home';
import PaymentsIcon from '@mui/icons-material/Payments';
import HistoryIcon from '@mui/icons-material/History';
import { useEffect } from "react";
import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";

const Sider = () => {

    const pathname = usePathname();
    const [homeIsActive, setHomeIsActive] = React.useState(false);
    const [historyIsActive, setHistoryIsActive] = React.useState(false);

    useEffect(() => {
        setHomeIsActive(pathname === "/");
        setHistoryIsActive(pathname === "/history");
    }, [pathname]);

    return (
        <div className={styles.sider}>
            <Box style={{ width: "80%", height: "28%", background: "var(--foreground)", borderRadius: "80px", display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center", padding: "20px 0 20px 0", gap: "70px"}} >
                <div className="sider__logo">
                    <Link href="/" style={{ textDecoration: "none" }}>
                        <Tooltip title="Дашборд">
                            <IconButton>
                            <HomeIcon className={`${styles.sider_button} ${homeIsActive ? styles.sider_button_active : styles.sider_button}`} style={{ width: "40px", height: "40px", transition: "all 0.2s ease-in-out" }}/>
                            </IconButton>
                        </Tooltip>
                    </Link>
                </div>
                <div className="sider__logo">
                    <Link href="/history" style={{ textDecoration: "none" }}>
                        <Tooltip title="История операций">
                            <IconButton>
                            <HistoryIcon className={`${styles.sider_button} ${historyIsActive ? styles.sider_button_active : styles.sider_button}`} style={{ width: "40px", height: "40px", transition: "all 0.2s ease-in-out" }}/>
                            </IconButton>
                        </Tooltip>
                    </Link>
                </div>
            </Box>
        </div>
    );
}

export default Sider;