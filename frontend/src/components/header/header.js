"use client";
import React from "react";
import styles from "./header.module.css";
import { Avatar, Box, IconButton, Menu, MenuItem, Tooltip, Typography } from "@mui/material";
import { useRouter } from "next/navigation";
import { useDispatch } from "@/app/store";
import { usePathname } from "next/navigation";
import { userLogout } from "@/reducers/userReducer";
import ListIcon from '@mui/icons-material/List';
import { BarsOutlined, LeftOutlined } from "@ant-design/icons";

const userSettings = ['Личный кабинет','Выйти'];
const pageSettings = ['Дашборд', 'История операций'];

const Header = ({isMobile}) => {
    const [barItem, setBarItem] = React.useState(null);
    const [anchorElUser, setAnchorElUser] = React.useState(null);
    const router = useRouter();
    const dispatch = useDispatch();
    
    const pathname = usePathname();
    const isProfilePage = pathname === "/profile";

    const handleOpenBarMenu = (event) => {
        setBarItem(event.currentTarget);
    };

    const handleCloseBarMenu = (e) => {
        const setting = e.target.textContent
        if(setting === "Дашборд") {
            router.push("/")
        } else if(setting === "История операций") {
            router.push("/history")
        }
        setBarItem(null);
    }

    const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = (e) => {
    const setting = e.target.textContent
    if(setting === 'Личный кабинет') {
        router.push("/profile")
    } else if(setting === 'Выйти') {
        dispatch(userLogout());
    }
    setAnchorElUser(null);
  };

  // Функция для возврата назад
  const handleGoBack = () => {
    router.back();
  };

    return (
        <div className={styles.header}>
            {isMobile && (
                <div className={styles.header__left}>
                    {/* Кнопка назад показывается только на странице профиля в мобильной версии */}
                    {isProfilePage && isMobile ? (
                        <IconButton onClick={handleGoBack}>
                            <LeftOutlined style={{color: "var(--main-color)", width: "40px", height: "40px"}}/>
                        </IconButton>
                    ) : (
                        <div sx={{ display: "flex", alignItems: "center", gap: "15px" }}>
                            <IconButton onClick={handleOpenBarMenu}>
                                <BarsOutlined style={{color: "var(--main-color)", width: "40px", height: "40px"}}/>
                            </IconButton>
                            <Menu open={Boolean(barItem)} anchorEl={barItem} onClose={handleCloseBarMenu}>
                                {pageSettings.map((setting) => (
                                    <MenuItem key={setting} onClick={handleCloseBarMenu}>
                                        <Typography sx={{ textAlign: 'center' }}>{setting}</Typography>
                                    </MenuItem>
                                ))}
                            </Menu>
                        </div>
                    )}
                </div>
            )}
            
            <div className={styles.header__right}>
                {/* В мобильной версии на странице профиля скрываем имя пользователя и аватар */}
                {!(isMobile && isProfilePage) && (
                    <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
                        {/* <CustomDrawer /> */}
                        Username
                        <Box sx={{ flexGrow: 0 }}>
                        <Tooltip title="Настройки">
                        <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                            <Avatar className={styles.avatar} style={{ width: "45px", height: "45px", transition: "all 0.2s ease-in-out"}}/>
                        </IconButton>
                        </Tooltip>
                        <Menu
                            sx={{ mt: '65px'}}
                            id="menu-appbar"
                            anchorEl={anchorElUser}
                            anchorOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            keepMounted
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            open={Boolean(anchorElUser)}
                            onClose={handleCloseUserMenu}
                            >
                            {userSettings.map((setting) => (
                                <MenuItem key={setting} onClick={handleCloseUserMenu}>
                                <Typography sx={{ textAlign: 'center' }}>{setting}</Typography>
                                </MenuItem>
                            ))}
                        </Menu>
                        </Box>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Header;